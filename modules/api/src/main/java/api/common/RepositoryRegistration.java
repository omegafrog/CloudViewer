package api.common;

import java.util.Map;
import java.util.Objects;

public record RepositoryRegistration(String userId, String repositoryId,
                                     String type, Map<String, String> config) {
    public RepositoryRegistration {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(type, "type");
        if (config == null) {
            config = Map.of();
        } else {
            config = Map.copyOf(config);
        }
    }

    public RepositoryDescriptor toDescriptor() {
        return new RepositoryDescriptor(repositoryId, type, config);
    }
}
