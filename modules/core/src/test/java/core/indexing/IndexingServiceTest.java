package core.indexing;

import api.common.RepositoryDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexingServiceTest {

    @Test
    void scheduleSetsStatus() {
        IndexingService service = new IndexingService();
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST",
                java.util.Map.of("rootPath", tempRoot.toString()));

        service.scheduleSync(repo);

        assertEquals(IndexStatus.INDEXED, service.status(repo));
        assertTrue(service.latestSnapshot(repo).isPresent());
    }

    @Test
    void deferSetsStatus() {
        IndexingService service = new IndexingService();
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());

        service.deferSync(repo);

        assertEquals(IndexStatus.DEFERRED, service.status(repo));
    }

    @Test
    void statusUnknownWhenNotScheduled() {
        IndexingService service = new IndexingService();
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());

        assertEquals(IndexStatus.UNKNOWN, service.status(repo));
    }

    @TempDir
    java.nio.file.Path tempRoot;

    @Test
    void scheduleDefersWhenRootMissing() {
        IndexingService service = new IndexingService();
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());

        service.scheduleSync(repo);

        assertEquals(IndexStatus.DEFERRED, service.status(repo));
    }

    @Test
    void indexingCreatesSnapshotTree() throws IOException {
        java.nio.file.Path subDir = java.nio.file.Files.createDirectory(tempRoot.resolve("sub"));
        java.nio.file.Files.writeString(tempRoot.resolve("a.txt"), "a");
        java.nio.file.Files.writeString(subDir.resolve("b.txt"), "b");

        IndexingService service = new IndexingService();
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST",
                java.util.Map.of("rootPath", tempRoot.toString()));

        service.scheduleSync(repo);

        IndexSnapshot snapshot = service.latestSnapshot(repo).orElseThrow();
        assertEquals(tempRoot.toString(), snapshot.root().path());
        assertEquals(IndexStatus.INDEXED, service.status(repo));
        assertTrue(snapshot.root().children().size() >= 1);
    }
}
