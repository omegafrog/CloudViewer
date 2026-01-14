package core.plugin;

import java.util.Objects;

public record PluginRecord(String pluginId, String repositoryType, String jarName, PluginStatus status) {
    public PluginRecord {
        Objects.requireNonNull(pluginId, "pluginId");
        Objects.requireNonNull(repositoryType, "repositoryType");
        Objects.requireNonNull(jarName, "jarName");
        Objects.requireNonNull(status, "status");
    }

    public PluginRecord withStatus(PluginStatus status) {
        return new PluginRecord(pluginId, repositoryType, jarName, status);
    }
}
