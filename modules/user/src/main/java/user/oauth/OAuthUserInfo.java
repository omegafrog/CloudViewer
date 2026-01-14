package user.oauth;

import java.util.Objects;

public record OAuthUserInfo(String providerUserId, String email, String name, String profileImageUrl) {
    public OAuthUserInfo {
        Objects.requireNonNull(providerUserId, "providerUserId");
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(profileImageUrl, "profileImageUrl");
    }
}
