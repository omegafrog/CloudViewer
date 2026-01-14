package core.web.dto;

import api.common.NodeId;
import api.common.UserRepositoryRef;

import java.util.Objects;

public record FileByIdRequest(String userId, String repositoryId, String nodeId) {
    public FileByIdRequest {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(nodeId, "nodeId");
    }

    public UserRepositoryRef toUserRef() {
        return new UserRepositoryRef(userId, repositoryId);
    }

    public NodeId toNodeId() {
        return new NodeId(nodeId);
    }
}
