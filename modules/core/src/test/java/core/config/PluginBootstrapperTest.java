package core.config;

import coretestplugin.TestFileHandle;
import coretestplugin.TestPlugin;
import coretestplugin.TestRepositoryConnector;
import coretestplugin.TestRepositoryHandle;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import plugin.runtime.PluginLoader;
import plugin.runtime.PluginRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginBootstrapperTest {
    @TempDir
    Path tempDir;

    @Test
    @Disabled
    void loadsPluginsFromDirectory() throws IOException {
        Path jar = tempDir.resolve("test-plugin.jar");
        createJar(jar,
                List.of(TestPlugin.class, TestRepositoryConnector.class, TestRepositoryHandle.class, TestFileHandle.class),
                "coretestplugin.TestPlugin\n");

        PluginRegistry registry = new PluginRegistry();
        PluginBootstrapper bootstrapper = new PluginBootstrapper();
        PluginLoader loader = new PluginLoader();

        bootstrapper.loadFromDirectory(tempDir, registry, loader);

        assertTrue(registry.findByType("TEST").isPresent());
    }

    private void createJar(Path jarPath, List<Class<?>> classes, String serviceContent) throws IOException {
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
            jarOutput.putNextEntry(new JarEntry("META-INF/services/api.plugin.RepositoryPlugin"));
            jarOutput.write(serviceContent.getBytes(StandardCharsets.UTF_8));
            jarOutput.closeEntry();
        }
    }
}
