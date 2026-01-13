package plugin.runtime;

import org.junit.jupiter.api.Test;
import testplugin.FirstTestPlugin;
import testplugin.SecondTestPlugin;
import testplugin.TestFileHandle;
import testplugin.TestRepositoryConnector;
import testplugin.TestRepositoryHandle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginLoaderTest {

    @Test
    void loadSinglePluginFromJar() throws Exception {
        Path jar = createJar(
                List.of(
                        FirstTestPlugin.class,
                        TestRepositoryConnector.class,
                        TestRepositoryHandle.class,
                        TestFileHandle.class
                ),
                "testplugin.FirstTestPlugin\n"
        );

        PluginLoader loader = new PluginLoader();
        var plugin = loader.load(jar);

        assertEquals("test-plugin-1", plugin.pluginId());
        assertEquals("TEST", plugin.repositoryType());
    }

    @Test
    void loadFailsWhenNoImplementationIsDeclared() throws Exception {
        Path jar = createJar(
                List.of(TestRepositoryConnector.class, TestRepositoryHandle.class, TestFileHandle.class),
                null
        );

        PluginLoader loader = new PluginLoader();
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> loader.load(jar));
        assertTrue(ex.getMessage().contains("Failed to load plugin from"));
        assertTrue(ex.getCause() != null && ex.getCause().getMessage().contains("No RepositoryPlugin implementation found"));
    }

    @Test
    void loadFailsWhenMultipleImplementationsDeclared() throws Exception {
        Path jar = createJar(
                List.of(
                        FirstTestPlugin.class,
                        SecondTestPlugin.class,
                        TestRepositoryConnector.class,
                        TestRepositoryHandle.class,
                        TestFileHandle.class
                ),
                "testplugin.FirstTestPlugin\n" +
                        "testplugin.SecondTestPlugin\n"
        );

        PluginLoader loader = new PluginLoader();
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> loader.load(jar));
        assertTrue(ex.getMessage().contains("Failed to load plugin from"));
        assertTrue(ex.getCause() != null && ex.getCause().getMessage().contains("Multiple RepositoryPlugin implementations found"));
    }

    private Path createJar(List<Class<?>> classes, String serviceContent) throws IOException {
        Path jarPath = Files.createTempFile("plugin-test", ".jar");
        try (JarOutputStream jarOutput = new JarOutputStream(Files.newOutputStream(jarPath))) {
            for (Class<?> clazz : classes) {
                String entryName = clazz.getName().replace('.', '/') + ".class";
                jarOutput.putNextEntry(new JarEntry(entryName));
                try (InputStream in = clazz.getClassLoader().getResourceAsStream(entryName)) {
                    if (in == null) {
                        throw new IllegalStateException("Missing class resource: " + entryName);
                    }
                    in.transferTo(jarOutput);
                }
                jarOutput.closeEntry();
            }

            if (serviceContent != null) {
                jarOutput.putNextEntry(new JarEntry("META-INF/services/api.plugin.RepositoryPlugin"));
                jarOutput.write(serviceContent.getBytes(StandardCharsets.UTF_8));
                jarOutput.closeEntry();
            }
        }
        return jarPath;
    }
}
