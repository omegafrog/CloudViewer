package core.web.dto;

import api.common.PageRequest;
import api.common.RepositoryDescriptor;

import java.util.Objects;

public record FileListRequest(RepositoryRequest repository, String path, PageRequest page) {
    public FileListRequest {
        Objects.requireNonNull(repository, "repository");
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(page, "page");
    }

    public RepositoryDescriptor descriptor() {
        return repository.toDescriptor();
    }
}
