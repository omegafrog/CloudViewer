package core.web.dto;

import api.common.NodeId;
import api.common.UserRepositoryRef;
import java.util.Objects;

public record FileDeleteByIdRequest(String userId, String repositoryId, String nodeId) {
    public FileDeleteByIdRequest {
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
