package core.indexing;

import api.common.IndexNode;
import api.common.RepositoryDescriptor;

import java.util.Objects;

public record IndexSnapshot(RepositoryDescriptor repository, IndexNode root, long collectedAtEpochMillis) {
    public IndexSnapshot {
        Objects.requireNonNull(repository, "repository");
        Objects.requireNonNull(root, "root");
        if (collectedAtEpochMillis < 0) {
            throw new IllegalArgumentException("collectedAtEpochMillis must be >= 0");
        }
    }
}
