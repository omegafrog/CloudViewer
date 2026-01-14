package core.web.dto;

import api.common.RepositoryDescriptor;
import api.common.RepositoryRegistration;
import api.common.UserRepositoryRef;

import java.util.Map;
import java.util.Objects;

public record RepositoryRequest(String userId, String repositoryId, String type, Map<String, String> config) {
    public RepositoryRequest {
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

    public RepositoryRegistration toRegistration() {
        return new RepositoryRegistration(userId, repositoryId, type, config);
    }

    public UserRepositoryRef toUserRef() {
        return new UserRepositoryRef(userId, repositoryId);
    }
}
