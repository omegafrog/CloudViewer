package testsupport;

import api.common.RepositoryDescriptor;
import api.repository.RepositoryConnector;
import api.repository.RepositoryHandle;

public class TestRepositoryConnector implements RepositoryConnector {
    private final RepositoryHandle handle;

    public TestRepositoryConnector(RepositoryHandle handle) {
        this.handle = handle;
    }

    @Override
    public RepositoryHandle open(RepositoryDescriptor repo) {
        return handle;
    }
}
