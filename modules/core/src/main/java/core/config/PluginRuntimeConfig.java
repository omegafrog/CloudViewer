package core.config;

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
    public CommandLineRunner pluginBootstrapRunner(PluginBootstrapper bootstrapper,
                                                   PluginRegistry registry,
                                                   PluginLoader loader) {
        return args -> {
            String dir = System.getProperty("cloudviewer.plugins.dir", "modules/plugins");
            bootstrapper.loadFromDirectory(Path.of(dir), registry, loader);
        };
    }
}
