package core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plugin.runtime.PluginRegistry;

@Configuration
public class PluginRuntimeConfig {
    @Bean
    public PluginRegistry pluginRegistry() {
        return new PluginRegistry();
    }
}
