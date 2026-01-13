package core.repository;

import api.common.RepositoryDescriptor;
import api.common.RepositoryMeta;

public class RepositoryAggregate {
    private final RepositoryDescriptor descriptor;
    private final RepositoryMeta meta;

    public RepositoryAggregate(RepositoryDescriptor descriptor, RepositoryMeta meta) {
        this.descriptor = descriptor;
        this.meta = meta;
    }

    public RepositoryDescriptor getDescriptor() {
        return descriptor;
    }

    public RepositoryMeta getMeta() {
        return meta;
    }
}
