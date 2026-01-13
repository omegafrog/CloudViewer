package core.indexing;

import api.common.RepositoryDescriptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexingService {
    private final Map<RepositoryDescriptor, IndexStatus> statuses = new ConcurrentHashMap<>();

    public void scheduleSync(RepositoryDescriptor repo) {
        statuses.put(repo, IndexStatus.SCHEDULED);
    }

    public IndexStatus status(RepositoryDescriptor repo) {
        return statuses.getOrDefault(repo, IndexStatus.UNKNOWN);
    }
}
