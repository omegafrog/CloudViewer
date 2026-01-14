package core.file;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.common.RepositoryDescriptor;
import api.repository.FileHandle;
import api.repository.RepositoryHandle;
import core.repository.RepositoryCatalog;
import core.repository.RepositoryService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class FileService {
    private final RepositoryService repositoryService;
    private final RepositoryCatalog repositoryCatalog;

    public FileService(RepositoryService repositoryService, RepositoryCatalog repositoryCatalog) {
        this.repositoryService = repositoryService;
        this.repositoryCatalog = repositoryCatalog;
    }

    public FileNode getFile(RepositoryDescriptor repo, NodeId id) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileHandle fileHandle = handle.fileHandle();
        // v1 is read-only; immutable file validation can be added here as an extension point.
        return fileHandle.get(id);
    }

    public FileNode getFileById(String repositoryId, NodeId id) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(repositoryId);
        return getFile(repo, id);
    }

    public List<FileNode> listFiles(RepositoryDescriptor repo, Path path, PageRequest page) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileHandle fileHandle = handle.fileHandle();
        // v1 is read-only; immutable file validation can be added here as an extension point.
        return fileHandle.list(path, page);
    }

    public List<FileNode> listFilesById(String repositoryId, Path path, PageRequest page) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(repositoryId);
        return listFiles(repo, path, page);
    }

    public DownloadStream download(RepositoryDescriptor repo, NodeId id) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileHandle fileHandle = handle.fileHandle();
        // v1 is read-only; immutable file validation can be added here as an extension point.
        return fileHandle.download(id);
    }

    public DownloadStream downloadById(String repositoryId, NodeId id) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(repositoryId);
        return download(repo, id);
    }
}
