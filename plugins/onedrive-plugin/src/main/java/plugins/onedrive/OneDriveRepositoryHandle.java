package plugins.onedrive;

import api.common.RepositoryDescriptor;
import api.common.RepositoryMeta;
import api.repository.FileHandle;
import api.repository.RepositoryHandle;

public class OneDriveRepositoryHandle implements RepositoryHandle {
    private final RepositoryDescriptor descriptor;
    private final OneDriveApiClient client;
    private final String rootPath;

    public OneDriveRepositoryHandle(RepositoryDescriptor descriptor, OneDriveApiClient client, String rootPath) {
        this.descriptor = descriptor;
        this.client = client;
        this.rootPath = rootPath;
    }

    @Override
    public FileHandle fileHandle() {
        return new OneDriveFileHandle(client, rootPath);
    }

    @Override
    public RepositoryMeta meta() {
        return new RepositoryMeta(descriptor.repositoryId(), descriptor.type(), "AVAILABLE");
    }
}
