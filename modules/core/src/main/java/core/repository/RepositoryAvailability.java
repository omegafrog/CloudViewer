package core.repository;

import api.plugin.PluginAvailability;

public record RepositoryAvailability(Status status, String reasonCode, String message) {
    public enum Status { AVAILABLE, UNAVAILABLE }

    public static RepositoryAvailability fromPlugin(PluginAvailability availability) {
        if (availability == null) {
            return new RepositoryAvailability(Status.UNAVAILABLE, "PLUGIN_AVAILABILITY_NULL", "Plugin returned null");
        }
        Status status = availability.getStatus() == PluginAvailability.Status.AVAILABLE
                ? Status.AVAILABLE
                : Status.UNAVAILABLE;
        return new RepositoryAvailability(status, availability.getReasonCode(), availability.getMessage());
    }
}
