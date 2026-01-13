package testplugin;

import api.common.RepositoryDescriptor;
import api.repository.RepositoryConnector;
import api.repository.RepositoryHandle;

public class TestRepositoryConnector implements RepositoryConnector {
    @Override
    public RepositoryHandle open(RepositoryDescriptor repo) {
        return new TestRepositoryHandle(repo);
    }
}
