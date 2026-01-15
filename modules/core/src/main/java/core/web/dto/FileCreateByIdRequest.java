package core.web.dto;

import api.common.UserRepositoryRef;
import java.util.Objects;

public record FileCreateByIdRequest(String userId, String repositoryId, String path, boolean directory) {
    public FileCreateByIdRequest {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(path, "path");
    }

    public UserRepositoryRef toUserRef() {
        return new UserRepositoryRef(userId, repositoryId);
    }
}
