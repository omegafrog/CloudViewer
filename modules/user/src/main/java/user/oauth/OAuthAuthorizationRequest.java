package user.oauth;

import java.util.Objects;

public record OAuthAuthorizationRequest(String redirectUri, String state, String scope) {
    public OAuthAuthorizationRequest {
        Objects.requireNonNull(redirectUri, "redirectUri");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(scope, "scope");
    }
}
