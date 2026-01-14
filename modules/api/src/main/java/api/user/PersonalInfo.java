package api.user;

import java.util.Objects;

public record PersonalInfo(String name, String email, String profileImageUrl) {
    public PersonalInfo {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(profileImageUrl, "profileImageUrl");
    }
}
