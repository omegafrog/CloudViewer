package user.oauth;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthStateCatalog {
    private final Map<String, String> states = new ConcurrentHashMap<>();

    public String issue(String providerId) {
        Objects.requireNonNull(providerId, "providerId");
        String state = UUID.randomUUID().toString();
        states.put(state, providerId);
        return state;
    }

    public void validateAndConsume(String state, String providerId) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(providerId, "providerId");
        String stored = states.remove(state);
        if (stored == null) {
            throw new IllegalStateException("Precondition violation: OAuth state not found");
        }
        if (!stored.equals(providerId)) {
            throw new IllegalStateException("Precondition violation: OAuth state mismatch for provider");
        }
    }
}
