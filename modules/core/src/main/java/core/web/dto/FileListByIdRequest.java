package core.web.dto;

import api.common.PageRequest;
import api.common.UserRepositoryRef;

import java.util.Objects;

public record FileListByIdRequest(String userId, String repositoryId, String path, PageRequest page) {
    public FileListByIdRequest {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(page, "page");
    }

    public UserRepositoryRef toUserRef() {
        return new UserRepositoryRef(userId, repositoryId);
    }
}
