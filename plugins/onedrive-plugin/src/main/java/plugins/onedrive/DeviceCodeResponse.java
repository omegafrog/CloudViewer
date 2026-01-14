package plugins.onedrive;

import java.util.Objects;

public record DeviceCodeResponse(String deviceCode, String userCode, String verificationUri,
                                 int intervalSeconds, int expiresInSeconds, String message) {
    public DeviceCodeResponse {
        Objects.requireNonNull(deviceCode, "deviceCode");
        Objects.requireNonNull(userCode, "userCode");
        Objects.requireNonNull(verificationUri, "verificationUri");
        if (intervalSeconds <= 0) {
            throw new IllegalArgumentException("intervalSeconds must be > 0");
        }
        if (expiresInSeconds <= 0) {
            throw new IllegalArgumentException("expiresInSeconds must be > 0");
        }
    }
}
