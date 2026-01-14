package api.user;

import java.util.Objects;

public record UserSession(String sessionId, UserId userId) {
    public UserSession {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(userId, "userId");
    }
}
