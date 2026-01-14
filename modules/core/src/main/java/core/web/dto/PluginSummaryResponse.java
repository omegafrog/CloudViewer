package core.web.dto;

import core.plugin.PluginRecord;
import core.plugin.PluginStatus;

import java.util.Objects;

public record PluginSummaryResponse(String pluginId, String repositoryType, String jarName, PluginStatus status) {
    public PluginSummaryResponse {
        Objects.requireNonNull(pluginId, "pluginId");
        Objects.requireNonNull(repositoryType, "repositoryType");
        Objects.requireNonNull(jarName, "jarName");
        Objects.requireNonNull(status, "status");
    }

    public static PluginSummaryResponse fromRecord(PluginRecord record) {
        return new PluginSummaryResponse(
                record.pluginId(),
                record.repositoryType(),
                record.jarName(),
                record.status()
        );
    }
}
