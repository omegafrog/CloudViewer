package testplugin;

import api.common.RepositoryDescriptor;
import api.common.RepositoryMeta;
import api.repository.FileHandle;
import api.repository.RepositoryHandle;

public class TestRepositoryHandle implements RepositoryHandle {
    private final RepositoryDescriptor descriptor;

    public TestRepositoryHandle(RepositoryDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public FileHandle fileHandle() {
        return new TestFileHandle();
    }

    @Override
    public RepositoryMeta meta() {
        return new RepositoryMeta(descriptor.repositoryId(), descriptor.type(), "AVAILABLE");
    }
}
