package core.web.dto;

import java.util.Objects;

public record RepositoryIdRequest(String repositoryId) {
    public RepositoryIdRequest {
        Objects.requireNonNull(repositoryId, "repositoryId");
    }
}
