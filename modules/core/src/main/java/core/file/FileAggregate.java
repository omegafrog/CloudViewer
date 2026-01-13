package core.file;

import api.common.NodeId;
import api.common.RepositoryDescriptor;

public class FileAggregate {
    private final RepositoryDescriptor repository;
    private final NodeId nodeId;

    public FileAggregate(RepositoryDescriptor repository, NodeId nodeId) {
        this.repository = repository;
        this.nodeId = nodeId;
    }

    public RepositoryDescriptor getRepository() {
        return repository;
    }

    public NodeId getNodeId() {
        return nodeId;
    }
}
