package api.common;

import java.util.Objects;

public record UserRegistration(String userId) {
    public UserRegistration {
        Objects.requireNonNull(userId, "userId");
    }
}
