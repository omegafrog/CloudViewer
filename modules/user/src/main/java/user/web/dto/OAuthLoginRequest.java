package user.web.dto;

import java.util.Objects;

public record OAuthLoginRequest(String providerId, String code, String state, String redirectUri) {
    public OAuthLoginRequest {
        Objects.requireNonNull(providerId, "providerId");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(redirectUri, "redirectUri");
    }
}
