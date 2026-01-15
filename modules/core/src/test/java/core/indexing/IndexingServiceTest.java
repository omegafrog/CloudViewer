package core.indexing;

import api.common.IndexNode;
import api.common.RepositoryDescriptor;
import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.plugin.PluginAvailability;
import api.repository.FileHandle;
import api.repository.PluginIndexingHandle;
import api.repository.RepositoryHandle;
import core.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import plugin.runtime.PluginRegistry;
import testsupport.TestFileHandle;
import testsupport.TestRepositoryConnector;
import testsupport.TestRepositoryHandle;
import testsupport.TestRepositoryPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexingServiceTest {

    @Test
    void scheduleSetsStatus() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST",
                java.util.Map.of("rootPath", tempRoot.toString()));
        IndexingService service = serviceWithHandle(repo, new TestRepositoryHandle(repo, new TestFileHandle()));

        service.scheduleSync(repo);

        assertEquals(IndexStatus.INDEXED, service.status(repo));
        assertTrue(service.latestSnapshot(repo).isPresent());
    }

    @Test
    void deferSetsStatus() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        IndexingService service = serviceWithHandle(repo, new TestRepositoryHandle(repo, new TestFileHandle()));

        service.deferSync(repo);

        assertEquals(IndexStatus.DEFERRED, service.status(repo));
    }

    @Test
    void statusUnknownWhenNotScheduled() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        IndexingService service = serviceWithHandle(repo, new TestRepositoryHandle(repo, new TestFileHandle()));

        assertEquals(IndexStatus.UNKNOWN, service.status(repo));
    }

    @TempDir
    java.nio.file.Path tempRoot;

    @Test
    void scheduleDefersWhenRootMissing() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        IndexingService service = serviceWithHandle(repo, new TestRepositoryHandle(repo, new TestFileHandle()));

        service.scheduleSync(repo);

        assertEquals(IndexStatus.DEFERRED, service.status(repo));
    }

    @Test
    void indexingCreatesSnapshotTree() throws IOException {
        java.nio.file.Path subDir = java.nio.file.Files.createDirectory(tempRoot.resolve("sub"));
        java.nio.file.Files.writeString(tempRoot.resolve("a.txt"), "a");
        java.nio.file.Files.writeString(subDir.resolve("b.txt"), "b");

        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST",
                java.util.Map.of("rootPath", tempRoot.toString()));
        IndexingService service = serviceWithHandle(repo, new TestRepositoryHandle(repo, new TestFileHandle()));

        service.scheduleSync(repo);

        IndexSnapshot snapshot = service.latestSnapshot(repo).orElseThrow();
        assertEquals(tempRoot.toString(), snapshot.root().path());
        assertEquals(IndexStatus.INDEXED, service.status(repo));
        assertTrue(snapshot.root().children().size() >= 1);
    }

    @Test
    void remoteIndexingUsesPluginIndexingHandle() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "REMOTE",
                java.util.Map.of("rootPath", "/"));
        RepositoryHandle handle = new TestRepositoryHandle(repo, new RemoteIndexingHandle());
        IndexingService service = serviceWithHandle(repo, handle);

        service.scheduleSync(repo);

        IndexSnapshot snapshot = service.latestSnapshot(repo).orElseThrow();
        assertEquals("/remote", snapshot.root().path());
        assertEquals(IndexStatus.INDEXED, service.status(repo));
    }

    private IndexingService serviceWithHandle(RepositoryDescriptor repo, RepositoryHandle handle) {
        PluginRegistry registry = new PluginRegistry();
        PluginManager manager = pluginManager(registry);
        registry.register(new TestRepositoryPlugin("test-plugin", repo.type(),
                new PluginAvailability(PluginAvailability.Status.AVAILABLE, null, null),
                new TestRepositoryConnector(handle)));
        return new IndexingService(registry, manager);
    }

    private PluginManager pluginManager(PluginRegistry registry) {
        try {
            return new PluginManager(registry, new plugin.runtime.PluginLoader(),
                    Files.createTempDirectory("plugins"));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static class RemoteIndexingHandle implements FileHandle, PluginIndexingHandle {
        @Override
        public IndexNode index(Path root, int depth) {
            return new IndexNode("/remote", "remote", false, List.of());
        }

        @Override
        public FileNode get(NodeId id) {
            return new FileNode(id, "/", id.value(), true, java.util.Map.of());
        }

        @Override
        public List<FileNode> list(Path path, PageRequest page) {
            return List.of();
        }

        @Override
        public DownloadStream download(NodeId id) {
            return new DownloadStream(new java.io.ByteArrayInputStream(new byte[0]),
                    "application/octet-stream", 0);
        }
    }
}
