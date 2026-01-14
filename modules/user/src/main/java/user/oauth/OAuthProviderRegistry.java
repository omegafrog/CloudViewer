package user.oauth;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthProviderRegistry {
    private final Map<String, OAuthProvider> providers = new ConcurrentHashMap<>();

    public OAuthProviderRegistry(List<OAuthProvider> providers) {
        for (OAuthProvider provider : providers) {
            this.providers.put(provider.providerId(), provider);
        }
    }

    public OAuthProvider getOrThrow(String providerId) {
        Objects.requireNonNull(providerId, "providerId");
        OAuthProvider provider = providers.get(providerId);
        if (provider == null) {
            throw new IllegalStateException("Precondition violation: OAuth provider not registered: " + providerId);
        }
        return provider;
    }
}
