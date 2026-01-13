package plugin.runtime;

import api.plugin.RepositoryPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PluginRegistry {
    private final Map<String, RepositoryPlugin> pluginsByType = new ConcurrentHashMap<>();

    public void register(RepositoryPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin must not be null");
        }
        pluginsByType.put(plugin.repositoryType(), plugin);
    }

    public Optional<RepositoryPlugin> findByType(String repositoryType) {
        return Optional.ofNullable(pluginsByType.get(repositoryType));
    }
}
