package user.web.dto;

import java.util.Objects;

public record OAuthAuthorizeRequest(String providerId, String redirectUri) {
    public OAuthAuthorizeRequest {
        Objects.requireNonNull(providerId, "providerId");
        Objects.requireNonNull(redirectUri, "redirectUri");
    }
}
