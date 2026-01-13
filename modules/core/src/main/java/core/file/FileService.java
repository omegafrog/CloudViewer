package core.file;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.common.RepositoryDescriptor;
import api.repository.FileHandle;
import api.repository.RepositoryHandle;
import core.repository.RepositoryService;

import java.nio.file.Path;
import java.util.List;

public class FileService {
    private final RepositoryService repositoryService;

    public FileService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public FileNode getFile(RepositoryDescriptor repo, NodeId id) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileHandle fileHandle = handle.fileHandle();
        return fileHandle.get(id);
    }

    public List<FileNode> listFiles(RepositoryDescriptor repo, Path path, PageRequest page) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileHandle fileHandle = handle.fileHandle();
        return fileHandle.list(path, page);
    }

    public DownloadStream download(RepositoryDescriptor repo, NodeId id) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileHandle fileHandle = handle.fileHandle();
        return fileHandle.download(id);
    }
}
