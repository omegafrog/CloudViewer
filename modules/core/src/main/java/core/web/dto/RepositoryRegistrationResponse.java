package core.web.dto;

import java.util.Objects;

public record RepositoryRegistrationResponse(String userId, String repositoryId, String type) {
    public RepositoryRegistrationResponse {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(type, "type");
    }
}
