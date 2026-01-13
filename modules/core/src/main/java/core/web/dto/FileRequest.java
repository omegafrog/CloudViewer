package core.web.dto;

import api.common.NodeId;
import api.common.RepositoryDescriptor;

import java.util.Objects;

public record FileRequest(RepositoryRequest repository, String nodeId) {
    public FileRequest {
        Objects.requireNonNull(repository, "repository");
        Objects.requireNonNull(nodeId, "nodeId");
    }

    public RepositoryDescriptor descriptor() {
        return repository.toDescriptor();
    }

    public NodeId toNodeId() {
        return new NodeId(nodeId);
    }
}
