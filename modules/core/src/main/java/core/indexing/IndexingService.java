package core.indexing;

import api.common.RepositoryDescriptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class IndexingService {
    private final Map<RepositoryDescriptor, IndexStatus> statuses = new ConcurrentHashMap<>();
    private final Map<RepositoryDescriptor, IndexSnapshot> snapshots = new ConcurrentHashMap<>();
    private final FileTreeIndexer fileTreeIndexer = new FileTreeIndexer();

    public void scheduleSync(RepositoryDescriptor repo) {
        String rootPath = repo.config().get("rootPath");
        if (rootPath == null || rootPath.isBlank()) {
            statuses.put(repo, IndexStatus.DEFERRED);
            return;
        }

        Path root = Path.of(rootPath);
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            statuses.put(repo, IndexStatus.DEFERRED);
            return;
        }

        statuses.put(repo, IndexStatus.SCHEDULED);
        try {
            IndexNode rootNode = fileTreeIndexer.index(root);
            IndexSnapshot snapshot = new IndexSnapshot(repo, rootNode, System.currentTimeMillis());
            snapshots.put(repo, snapshot);
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
}
