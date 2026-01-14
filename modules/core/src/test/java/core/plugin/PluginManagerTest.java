package core.plugin;

import api.plugin.PluginAvailability;
import api.repository.RepositoryHandle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import plugin.runtime.PluginLoader;
import plugin.runtime.PluginRegistry;
import testsupport.TestFileHandle;
import testsupport.TestRepositoryConnector;
import testsupport.TestRepositoryHandle;
import testsupport.TestRepositoryPlugin;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PluginManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void uploadRegistersPlugin() throws Exception {
        PluginRegistry registry = new PluginRegistry();
        TestRepositoryPlugin plugin = new TestRepositoryPlugin(
                "plugin-1",
                "TEST",
                new PluginAvailability(PluginAvailability.Status.AVAILABLE, null, null),
                new TestRepositoryConnector(new TestRepositoryHandle(
                        new api.common.RepositoryDescriptor("repo-1", "TEST", java.util.Map.of()),
                        new TestFileHandle()
                ))
        );
        PluginManager manager = new PluginManager(registry, new StubPluginLoader(plugin), tempDir);
        Path jar = tempDir.resolve("upload.jar");
        Files.write(jar, new byte[]{0x1, 0x2});

        PluginRecord record = manager.upload(new StubMultipartFile("upload.jar", jar));

        assertEquals("plugin-1", record.pluginId());
        assertEquals("TEST", record.repositoryType());
        assertEquals(PluginStatus.ENABLED, record.status());
    }

    @Test
    void disableMarksPluginUnavailable() throws Exception {
        PluginRegistry registry = new PluginRegistry();
        TestRepositoryPlugin plugin = new TestRepositoryPlugin(
                "plugin-1",
                "TEST",
                new PluginAvailability(PluginAvailability.Status.AVAILABLE, null, null),
                new TestRepositoryConnector(new TestRepositoryHandle(
                        new api.common.RepositoryDescriptor("repo-1", "TEST", java.util.Map.of()),
                        new TestFileHandle()
                ))
        );
        PluginManager manager = new PluginManager(registry, new StubPluginLoader(plugin), tempDir);
        Path jar = tempDir.resolve("upload.jar");
        Files.write(jar, new byte[]{0x1, 0x2});
        manager.upload(new StubMultipartFile("upload.jar", jar));

        PluginRecord disabled = manager.disable("plugin-1");

        assertEquals(PluginStatus.DISABLED, disabled.status());
        assertEquals(true, manager.isDisabled("TEST"));
    }

    @Test
    void uploadRejectsDuplicatePluginId() throws Exception {
        PluginRegistry registry = new PluginRegistry();
        TestRepositoryPlugin plugin = new TestRepositoryPlugin(
                "plugin-1",
                "TEST",
                new PluginAvailability(PluginAvailability.Status.AVAILABLE, null, null),
                new TestRepositoryConnector(new TestRepositoryHandle(
                        new api.common.RepositoryDescriptor("repo-1", "TEST", java.util.Map.of()),
                        new TestFileHandle()
                ))
        );
        PluginManager manager = new PluginManager(registry, new StubPluginLoader(plugin), tempDir);
        Path jar = tempDir.resolve("upload.jar");
        Files.write(jar, new byte[]{0x1, 0x2});
        manager.upload(new StubMultipartFile("upload.jar", jar));

        assertThrows(IllegalStateException.class, () -> manager.upload(new StubMultipartFile("upload.jar", jar)));
    }

    private static class StubPluginLoader extends PluginLoader {
        private final TestRepositoryPlugin plugin;

        private StubPluginLoader(TestRepositoryPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public api.plugin.RepositoryPlugin load(Path jarPath) {
            return plugin;
        }
    }
}
