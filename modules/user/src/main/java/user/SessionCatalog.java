package user;

import api.user.UserId;
import api.user.UserSession;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionCatalog {
    private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionsByUser = new ConcurrentHashMap<>();

    public UserSession create(UserId userId) {
        Objects.requireNonNull(userId, "userId");
        String sessionId = UUID.randomUUID().toString();
        UserSession session = new UserSession(sessionId, userId);
        sessions.put(sessionId, session);
        sessionsByUser.put(userId.value(), sessionId);
        return session;
    }

    public Optional<UserSession> find(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public void remove(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");
        UserSession session = sessions.remove(sessionId);
        if (session != null) {
            sessionsByUser.remove(session.userId().value());
        }
    }

    public void removeByUserId(String userId) {
        Objects.requireNonNull(userId, "userId");
        String sessionId = sessionsByUser.remove(userId);
        if (sessionId != null) {
            sessions.remove(sessionId);
        }
    }
}
