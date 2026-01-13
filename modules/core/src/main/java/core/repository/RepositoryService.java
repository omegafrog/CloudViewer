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
        if (availability == null) {
            throw preconditionViolation("Plugin availability is null");
        }
        if (availability.getStatus() != PluginAvailability.Status.AVAILABLE) {
            String message = availability.getMessage() == null ? "UNAVAILABLE" : availability.getMessage();
            String reason = availability.getReasonCode() == null ? "UNKNOWN" : availability.getReasonCode();
            throw preconditionViolation("Plugin unavailable: " + reason + " - " + message);
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
                .orElseThrow(() -> preconditionViolation("No plugin for repository type: " + descriptor.type()));
    }

    private IllegalStateException preconditionViolation(String message) {
        return new IllegalStateException("Precondition violation: " + message);
    }
}
