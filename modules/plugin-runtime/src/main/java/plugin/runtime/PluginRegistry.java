package plugin.runtime;

import api.plugin.RepositoryPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PluginRegistry {
    private final Map<String, RepositoryPlugin> pluginsByType = new ConcurrentHashMap<>();

    /**
     * Registers a plugin by repositoryType.
     * Duplicate repositoryType registrations are rejected to avoid ambiguous resolution.
     */
    public void register(RepositoryPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin must not be null");
        }
        RepositoryPlugin existing = pluginsByType.putIfAbsent(plugin.repositoryType(), plugin);
        if (existing != null) {
            throw new IllegalStateException(
                    "Duplicate repositoryType registration: " + plugin.repositoryType()
            );
        }
    }

    public Optional<RepositoryPlugin> findByType(String repositoryType) {
        return Optional.ofNullable(pluginsByType.get(repositoryType));
    }
}
