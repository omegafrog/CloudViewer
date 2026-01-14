package core.web.dto;

import java.util.Objects;

public record RepositoryRegistrationResponse(String repositoryId, String type) {
    public RepositoryRegistrationResponse {
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(type, "type");
    }
}
