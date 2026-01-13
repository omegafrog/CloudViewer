package api.plugin;

public final class PluginAvailability {
    public enum Status { AVAILABLE, UNAVAILABLE }

    private final Status status;
    private final String reasonCode;
    private final String message;

    public PluginAvailability(Status status, String reasonCode, String message) {
        this.status = status;
        this.reasonCode = reasonCode;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public String getMessage() {
        return message;
    }
}
