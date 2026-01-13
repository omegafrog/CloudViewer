package api.common;

import java.util.Objects;

public record RepositoryMeta(String repositoryId, String type, String status) {
    public RepositoryMeta {
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(status, "status");
    }
}
