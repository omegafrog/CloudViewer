package core.web.dto;

import api.common.NodeId;

import java.util.Objects;

public record FileByIdRequest(String repositoryId, String nodeId) {
    public FileByIdRequest {
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(nodeId, "nodeId");
    }

    public NodeId toNodeId() {
        return new NodeId(nodeId);
    }
}
