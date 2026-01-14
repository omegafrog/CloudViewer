package core.file;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.common.RepositoryDescriptor;
import api.repository.FileHandle;
import api.repository.RepositoryHandle;
import core.plugin.PluginManager;
import core.repository.RepositoryCatalog;
import core.repository.RepositoryService;
import org.junit.jupiter.api.Test;
import plugin.runtime.PluginRegistry;
import testsupport.TestFileHandle;
import testsupport.TestRepositoryHandle;

import java.nio.file.Path;
import java.util.List;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileServiceTest {

    private static class StubRepositoryService extends RepositoryService {
        private int openCalls;
        private RepositoryHandle handle;
        private RuntimeException toThrow;

        public StubRepositoryService(PluginManager pluginManager) {
            super(new PluginRegistry(), pluginManager);
        }

        public void returns(RepositoryHandle handle) {
            this.handle = handle;
        }

        public void throwsError(RuntimeException ex) {
            this.toThrow = ex;
        }

        public int openCalls() {
            return openCalls;
        }

        @Override
        public RepositoryHandle openRepository(RepositoryDescriptor descriptor) {
            openCalls++;
            if (toThrow != null) {
                throw toThrow;
            }
            return handle;
        }
    }

    @Test
    void getFileDelegatesAfterPreconditionCheck() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        TestFileHandle fileHandle = new TestFileHandle();
        RepositoryHandle handle = new TestRepositoryHandle(repo, fileHandle);

        StubRepositoryService repositoryService = new StubRepositoryService(pluginManager());
        repositoryService.returns(handle);
        FileService service = new FileService(repositoryService, new RepositoryCatalog());

        FileNode node = service.getFile(repo, new NodeId("n1"));
        assertEquals(1, repositoryService.openCalls());
        assertEquals("n1", node.id().value());
        assertEquals(1, fileHandle.getCalls());
    }

    @Test
    void listFilesDelegatesAfterPreconditionCheck() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        TestFileHandle fileHandle = new TestFileHandle();
        RepositoryHandle handle = new TestRepositoryHandle(repo, fileHandle);

        StubRepositoryService repositoryService = new StubRepositoryService(pluginManager());
        repositoryService.returns(handle);
        FileService service = new FileService(repositoryService, new RepositoryCatalog());

        List<FileNode> nodes = service.listFiles(repo, Path.of("/"), new PageRequest(null, 10));
        assertEquals(1, repositoryService.openCalls());
        assertEquals(0, nodes.size());
        assertEquals(1, fileHandle.listCalls());
    }

    @Test
    void downloadDelegatesAfterPreconditionCheck() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        TestFileHandle fileHandle = new TestFileHandle();
        RepositoryHandle handle = new TestRepositoryHandle(repo, fileHandle);

        StubRepositoryService repositoryService = new StubRepositoryService(pluginManager());
        repositoryService.returns(handle);
        FileService service = new FileService(repositoryService, new RepositoryCatalog());

        DownloadStream stream = service.download(repo, new NodeId("n1"));
        assertEquals(1, repositoryService.openCalls());
        assertEquals(0, stream.length());
        assertEquals(1, fileHandle.downloadCalls());
    }

    @Test
    void repositoryFailurePropagates() {
        RepositoryDescriptor repo = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());

        StubRepositoryService repositoryService = new StubRepositoryService(pluginManager());
        repositoryService.throwsError(new IllegalStateException("precondition"));
        FileService service = new FileService(repositoryService, new RepositoryCatalog());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.getFile(repo, new NodeId("n1")));
        assertTrue(ex.getMessage().contains("precondition"));
        assertEquals(1, repositoryService.openCalls());
    }

    private PluginManager pluginManager() {
        try {
            return new PluginManager(new PluginRegistry(), new plugin.runtime.PluginLoader(),
                    Files.createTempDirectory("plugins"));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
