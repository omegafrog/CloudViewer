package core.config;

import core.plugin.PluginManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plugin.runtime.PluginLoader;
import plugin.runtime.PluginRegistry;

import java.nio.file.Path;

@Configuration
public class PluginRuntimeConfig {
    @Bean
    public PluginRegistry pluginRegistry() {
        return new PluginRegistry();
    }

    @Bean
    public PluginLoader pluginLoader() {
        return new PluginLoader();
    }

    @Bean
    public PluginBootstrapper pluginBootstrapper() {
        return new PluginBootstrapper();
    }

    @Bean
    public PluginManager pluginManager(PluginRegistry registry, PluginLoader loader) {
        String dir = System.getProperty("cloudviewer.plugins.dir", "modules/plugins");
        return new PluginManager(registry, loader, resolvePluginsDir(dir));
    }

    @Bean
    public CommandLineRunner pluginBootstrapRunner(PluginManager pluginManager) {
        return args -> pluginManager.bootstrap();
    }

    private Path resolvePluginsDir(String pluginsDir) {
        Path candidate = Path.of(pluginsDir);
        if (candidate.isAbsolute() && java.nio.file.Files.exists(candidate)) {
            return candidate.normalize();
        }
        Path base = Path.of(System.getProperty("user.dir"));
        Path direct = base.resolve(pluginsDir).normalize();
        if (java.nio.file.Files.exists(direct)) {
            return direct;
        }
        Path current = base;
        while (current != null) {
            Path resolved = current.resolve(pluginsDir).normalize();
            if (java.nio.file.Files.exists(resolved)) {
                return resolved;
            }
            Path parent = current.getParent();
            if (parent == null || parent.equals(current)) {
                break;
            }
            current = parent;
        }
        return direct;
    }
}
