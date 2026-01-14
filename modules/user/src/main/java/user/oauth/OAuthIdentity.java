package user.oauth;

import java.util.Objects;

public record OAuthIdentity(String providerId, String providerUserId) {
    public OAuthIdentity {
        Objects.requireNonNull(providerId, "providerId");
        Objects.requireNonNull(providerUserId, "providerUserId");
    }
}
