package core.web.dto;

import api.common.PageRequest;

import java.util.Objects;

public record FileListByIdRequest(String repositoryId, String path, PageRequest page) {
    public FileListByIdRequest {
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(page, "page");
    }
}
