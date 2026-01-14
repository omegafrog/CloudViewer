package plugins.onedrive;

import java.util.Objects;

public record TokenResponse(String accessToken, String refreshToken, int expiresInSeconds) {
    public TokenResponse {
        Objects.requireNonNull(accessToken, "accessToken");
        if (expiresInSeconds <= 0) {
            throw new IllegalArgumentException("expiresInSeconds must be > 0");
        }
    }
}
