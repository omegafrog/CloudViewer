package core.repository;

import api.common.RepositoryDescriptor;
import api.plugin.PluginAvailability;
import api.plugin.RepositoryPlugin;
import api.repository.RepositoryConnector;
import api.repository.RepositoryHandle;
import plugin.runtime.PluginRegistry;

public class RepositoryService {
    private final PluginRegistry pluginRegistry;

    public RepositoryService(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public RepositoryHandle openRepository(RepositoryDescriptor descriptor) {
        RepositoryPlugin plugin = resolvePlugin(descriptor);
        PluginAvailability availability = plugin.availability(descriptor);
        if (availability == null || availability.getStatus() != PluginAvailability.Status.AVAILABLE) {
            String reason = availability == null ? "Plugin availability is null" : availability.getMessage();
            throw new IllegalStateException("Repository unavailable: " + reason);
        }
        RepositoryConnector connector = plugin.connector(descriptor);
        return connector.open(descriptor);
    }

    public RepositoryAvailability checkAvailability(RepositoryDescriptor descriptor) {
        RepositoryPlugin plugin = resolvePlugin(descriptor);
        return RepositoryAvailability.fromPlugin(plugin.availability(descriptor));
    }

    private RepositoryPlugin resolvePlugin(RepositoryDescriptor descriptor) {
        return pluginRegistry.findByType(descriptor.type())
                .orElseThrow(() -> new IllegalStateException("No plugin for repository type: " + descriptor.type()));
    }
}
