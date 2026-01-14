package core.web.dto;

import api.common.UserRepositoryRef;

import java.util.Objects;

public record RepositoryIdRequest(String userId, String repositoryId) {
    public RepositoryIdRequest {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
    }

    public UserRepositoryRef toUserRef() {
        return new UserRepositoryRef(userId, repositoryId);
    }
}
