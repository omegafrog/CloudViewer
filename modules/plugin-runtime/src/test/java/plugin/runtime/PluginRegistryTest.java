package plugin.runtime;

import org.junit.jupiter.api.Test;
import testplugin.FirstTestPlugin;
import testplugin.SecondTestPlugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginRegistryTest {

    @Test
    void registerAndFindByType() {
        PluginRegistry registry = new PluginRegistry();
        registry.register(new FirstTestPlugin());

        var plugin = registry.findByType("TEST");
        assertTrue(plugin.isPresent());
        assertEquals("test-plugin-1", plugin.get().pluginId());
    }

    @Test
    void duplicateRepositoryTypeFails() {
        PluginRegistry registry = new PluginRegistry();
        registry.register(new FirstTestPlugin());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registry.register(new FirstTestPlugin()));
        assertTrue(ex.getMessage().contains("Duplicate repositoryType registration"));
    }

    @Test
    void registerDifferentTypesAllowed() {
        PluginRegistry registry = new PluginRegistry();
        registry.register(new FirstTestPlugin());
        registry.register(new SecondTestPlugin());

        assertTrue(registry.findByType("TEST").isPresent());
        assertTrue(registry.findByType("TEST-2").isPresent());
    }
}
