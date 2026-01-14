package api.common;

import java.util.Objects;

public record UserRepositoryRef(String userId, String repositoryId) {
    public UserRepositoryRef {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
    }
}
