package core.repository;

import api.common.RepositoryDescriptor;
import api.plugin.PluginAvailability;
import api.repository.RepositoryHandle;
import core.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import plugin.runtime.PluginRegistry;
import testsupport.TestFileHandle;
import testsupport.TestRepositoryConnector;
import testsupport.TestRepositoryHandle;
import testsupport.TestRepositoryPlugin;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryServiceTest {

    @Test
    void openRepositoryFailsWhenPluginMissing() {
        PluginRegistry registry = new PluginRegistry();
        RepositoryService service = new RepositoryService(registry, pluginManager(registry));

        RepositoryDescriptor descriptor = new RepositoryDescriptor("repo-1", "MISSING", java.util.Map.of());
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.openRepository(descriptor));

        assertTrue(ex.getMessage().contains("Precondition violation"));
        assertTrue(ex.getMessage().contains("No plugin for repository type"));
    }

    @Test
    void openRepositoryFailsWhenAvailabilityNull() {
        PluginRegistry registry = new PluginRegistry();
        RepositoryService service = new RepositoryService(registry, pluginManager(registry));

        TestRepositoryPlugin plugin = new TestRepositoryPlugin(
                "plugin-1",
                "TEST",
                null,
                new TestRepositoryConnector(new TestRepositoryHandle(
                        new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of()),
                        new TestFileHandle()
                ))
        );
        registry.register(plugin);

        RepositoryDescriptor descriptor = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.openRepository(descriptor));

        assertTrue(ex.getMessage().contains("Precondition violation"));
        assertTrue(ex.getMessage().contains("Plugin availability is null"));
    }

    @Test
    void openRepositoryFailsWhenUnavailable() {
        PluginRegistry registry = new PluginRegistry();
        RepositoryService service = new RepositoryService(registry, pluginManager(registry));

        PluginAvailability availability = new PluginAvailability(
                PluginAvailability.Status.UNAVAILABLE,
                "NO_ACCESS",
                "no access"
        );
        TestRepositoryPlugin plugin = new TestRepositoryPlugin(
                "plugin-1",
                "TEST",
                availability,
                new TestRepositoryConnector(new TestRepositoryHandle(
                        new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of()),
                        new TestFileHandle()
                ))
        );
        registry.register(plugin);

        RepositoryDescriptor descriptor = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.openRepository(descriptor));

        assertTrue(ex.getMessage().contains("Precondition violation"));
        assertTrue(ex.getMessage().contains("NO_ACCESS"));
        assertTrue(ex.getMessage().contains("no access"));
    }

    @Test
    void openRepositoryReturnsHandleWhenAvailable() {
        PluginRegistry registry = new PluginRegistry();
        RepositoryService service = new RepositoryService(registry, pluginManager(registry));

        RepositoryDescriptor descriptor = new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of());
        TestFileHandle fileHandle = new TestFileHandle();
        RepositoryHandle handle = new TestRepositoryHandle(descriptor, fileHandle);
        TestRepositoryPlugin plugin = new TestRepositoryPlugin(
                "plugin-1",
                "TEST",
                new PluginAvailability(PluginAvailability.Status.AVAILABLE, null, null),
                new TestRepositoryConnector(handle)
        );
        registry.register(plugin);

        RepositoryHandle returned = service.openRepository(descriptor);
        assertEquals(handle, returned);
    }

    @Test
    void checkAvailabilityWrapsPluginAvailability() {
        PluginRegistry registry = new PluginRegistry();
        RepositoryService service = new RepositoryService(registry, pluginManager(registry));

        PluginAvailability availability = new PluginAvailability(
                PluginAvailability.Status.UNAVAILABLE,
                "NO_ACCESS",
                "no access"
        );
        TestRepositoryPlugin plugin = new TestRepositoryPlugin(
                "plugin-1",
                "TEST",
                availability,
                new TestRepositoryConnector(new TestRepositoryHandle(
                        new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of()),
                        new TestFileHandle()
                ))
        );
        registry.register(plugin);

        RepositoryAvailability wrapped = service.checkAvailability(
                new RepositoryDescriptor("repo-1", "TEST", java.util.Map.of())
        );

        assertEquals(RepositoryAvailability.Status.UNAVAILABLE, wrapped.status());
        assertEquals("NO_ACCESS", wrapped.reasonCode());
        assertEquals("no access", wrapped.message());
    }

    private PluginManager pluginManager(PluginRegistry registry) {
        try {
            Path dir = Files.createTempDirectory("plugins");
            return new PluginManager(registry, new plugin.runtime.PluginLoader(), dir);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
