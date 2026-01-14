package user.web.dto;

import java.util.Objects;

public record OAuthAuthorizeResponse(String authorizationUrl, String state) {
    public OAuthAuthorizeResponse {
        Objects.requireNonNull(authorizationUrl, "authorizationUrl");
        Objects.requireNonNull(state, "state");
    }
}
