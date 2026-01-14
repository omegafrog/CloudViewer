package user.oauth;

import java.util.Objects;

public record OAuthCallbackRequest(String code, String redirectUri) {
    public OAuthCallbackRequest {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(redirectUri, "redirectUri");
    }
}
