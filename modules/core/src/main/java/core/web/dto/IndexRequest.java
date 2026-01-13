package core.web.dto;

import api.common.RepositoryDescriptor;

import java.util.Objects;

public record IndexRequest(RepositoryRequest repository) {
    public IndexRequest {
        Objects.requireNonNull(repository, "repository");
    }

    public RepositoryDescriptor descriptor() {
        return repository.toDescriptor();
    }
}
