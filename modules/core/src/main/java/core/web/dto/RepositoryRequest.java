package core.web.dto;

import api.common.RepositoryDescriptor;

import java.util.Map;
import java.util.Objects;

public record RepositoryRequest(String repositoryId, String type, Map<String, String> config) {
    public RepositoryRequest {
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(type, "type");
    }

    public RepositoryDescriptor toDescriptor() {
        return new RepositoryDescriptor(repositoryId, type, config);
    }
}
