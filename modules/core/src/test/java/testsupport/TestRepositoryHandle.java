package testsupport;

import api.common.RepositoryDescriptor;
import api.common.RepositoryMeta;
import api.repository.FileHandle;
import api.repository.RepositoryHandle;

public class TestRepositoryHandle implements RepositoryHandle {
    private final RepositoryDescriptor descriptor;
    private final FileHandle fileHandle;

    public TestRepositoryHandle(RepositoryDescriptor descriptor, FileHandle fileHandle) {
        this.descriptor = descriptor;
        this.fileHandle = fileHandle;
    }

    @Override
    public FileHandle fileHandle() {
        return fileHandle;
    }

    @Override
    public RepositoryMeta meta() {
        return new RepositoryMeta(descriptor.repositoryId(), descriptor.type(), "AVAILABLE");
    }
}
