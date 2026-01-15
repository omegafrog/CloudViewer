package core.web.dto;

import api.common.NodeId;
import api.common.UserRepositoryRef;
import java.util.Objects;

public record FileMoveByIdRequest(String userId, String repositoryId, String nodeId, String targetPath) {
    public FileMoveByIdRequest {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(nodeId, "nodeId");
        Objects.requireNonNull(targetPath, "targetPath");
    }

    public UserRepositoryRef toUserRef() {
        return new UserRepositoryRef(userId, repositoryId);
    }

    public NodeId toNodeId() {
        return new NodeId(nodeId);
    }
}
