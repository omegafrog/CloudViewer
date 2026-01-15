package core.indexing;

import api.common.IndexNode;
import api.common.RepositoryDescriptor;
import api.plugin.PluginAvailability;
import api.plugin.RepositoryPlugin;
import api.repository.FileHandle;
import api.repository.PluginIndexingHandle;
import api.repository.RepositoryConnector;
import api.repository.RepositoryHandle;
import core.plugin.PluginManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import plugin.runtime.PluginRegistry;

/**
 * Indexing builds read-only snapshots for repository browsing and search.
 * It never mutates repositories, and file commands never trigger indexing.
 */
@Service
public class IndexingService {
    private final Map<RepositoryDescriptor, IndexStatus> statuses = new ConcurrentHashMap<>();
    private final Map<RepositoryDescriptor, IndexSnapshot> snapshots = new ConcurrentHashMap<>();
    private static final int DEFAULT_REMOTE_DEPTH = 2;
    private final PluginRegistry pluginRegistry;
    private final PluginManager pluginManager;
    private final FileTreeIndexer fileTreeIndexer = new FileTreeIndexer();

    public IndexingService(PluginRegistry pluginRegistry, PluginManager pluginManager) {
        this.pluginRegistry = Objects.requireNonNull(pluginRegistry, "pluginRegistry");
        this.pluginManager = Objects.requireNonNull(pluginManager, "pluginManager");
    }

    public void scheduleSync(RepositoryDescriptor repo) {
        Objects.requireNonNull(repo, "repo");
        statuses.put(repo, IndexStatus.SCHEDULED);

        try {
            RepositoryHandle handle = openRepository(repo);
            FileHandle fileHandle = handle.fileHandle();
            if (fileHandle instanceof PluginIndexingHandle indexingHandle) {
                IndexNode rootNode = indexingHandle.index(resolveRootPath(repo), DEFAULT_REMOTE_DEPTH);
                snapshots.put(repo, new IndexSnapshot(repo, rootNode, System.currentTimeMillis()));
                statuses.put(repo, IndexStatus.INDEXED);
                return;
            }
        } catch (RuntimeException ex) {
            statuses.put(repo, IndexStatus.DEFERRED);
            return;
        }

        Path root = resolveLocalRoot(repo);
        if (root == null) {
            statuses.put(repo, IndexStatus.DEFERRED);
            return;
        }

        try {
            IndexNode rootNode = fileTreeIndexer.index(root);
            snapshots.put(repo, new IndexSnapshot(repo, rootNode, System.currentTimeMillis()));
            statuses.put(repo, IndexStatus.INDEXED);
        } catch (IOException ex) {
            statuses.put(repo, IndexStatus.DEFERRED);
        }
    }

    public void deferSync(RepositoryDescriptor repo) {
        statuses.put(repo, IndexStatus.DEFERRED);
    }

    public Optional<IndexSnapshot> latestSnapshot(RepositoryDescriptor repo) {
        return Optional.ofNullable(snapshots.get(repo));
    }

    public IndexStatus status(RepositoryDescriptor repo) {
        return statuses.getOrDefault(repo, IndexStatus.UNKNOWN);
    }

    private RepositoryHandle openRepository(RepositoryDescriptor repo) {
        if (pluginManager.isDisabled(repo.type())) {
            throw preconditionViolation("Plugin disabled: " + repo.type());
        }
        RepositoryPlugin plugin = pluginRegistry.findByType(repo.type())
                .orElseThrow(() -> preconditionViolation("No plugin for repository type: " + repo.type()));
        PluginAvailability availability = plugin.availability(repo);
        if (availability == null) {
            throw preconditionViolation("Plugin availability is null");
        }
        if (availability.getStatus() != PluginAvailability.Status.AVAILABLE) {
            String message = availability.getMessage() == null ? "UNAVAILABLE" : availability.getMessage();
            String reason = availability.getReasonCode() == null ? "UNKNOWN" : availability.getReasonCode();
            throw preconditionViolation("Plugin unavailable: " + reason + " - " + message);
        }
        RepositoryConnector connector = plugin.connector(repo);
        return connector.open(repo);
    }

    private IllegalStateException preconditionViolation(String message) {
        return new IllegalStateException("Precondition violation: " + message);
    }

    private Path resolveRootPath(RepositoryDescriptor repo) {
        String rootPath = repo.config().get("rootPath");
        if (rootPath == null || rootPath.isBlank()) {
            return Path.of("/");
        }
        return Path.of(rootPath);
    }

    private Path resolveLocalRoot(RepositoryDescriptor repo) {
        String rootPath = repo.config().get("rootPath");
        if (rootPath == null || rootPath.isBlank()) {
            return null;
        }

        Path root = Path.of(rootPath);
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            return null;
        }
        return root;
    }
}
