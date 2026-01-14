package coretestplugin;

import api.common.RepositoryMeta;
import api.repository.FileHandle;
import api.repository.RepositoryHandle;

public class TestRepositoryHandle implements RepositoryHandle {
    @Override
    public FileHandle fileHandle() {
        return new TestFileHandle();
    }

    @Override
    public RepositoryMeta meta() {
        return new RepositoryMeta("repo", "TEST", "AVAILABLE");
    }
}
