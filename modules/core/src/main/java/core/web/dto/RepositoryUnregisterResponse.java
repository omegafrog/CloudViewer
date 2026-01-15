package core.web.dto;

import java.util.Objects;

public record RepositoryUnregisterResponse(String userId, String repositoryId, boolean removed) {
    public RepositoryUnregisterResponse {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
    }
}
