package core.file;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.common.RepositoryDescriptor;
import api.common.UserRepositoryRef;
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
        FileFacade facade = FileFacade.from(handle);
        // File access is isolated from indexing; only repository IO is performed here.
        return facade.get(id);
    }

    public FileNode getFileById(UserRepositoryRef ref, NodeId id) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(ref);
        return getFile(repo, id);
    }

    public List<FileNode> listFiles(RepositoryDescriptor repo, Path path, PageRequest page) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileFacade facade = FileFacade.from(handle);
        // File access is isolated from indexing; only repository IO is performed here.
        return facade.list(path, page);
    }

    public List<FileNode> listFilesById(UserRepositoryRef ref, Path path, PageRequest page) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(ref);
        return listFiles(repo, path, page);
    }

    public DownloadStream download(RepositoryDescriptor repo, NodeId id) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileFacade facade = FileFacade.from(handle);
        // File access is isolated from indexing; only repository IO is performed here.
        return facade.download(id);
    }

    public DownloadStream downloadById(UserRepositoryRef ref, NodeId id) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(ref);
        return download(repo, id);
    }

    public FileNode createFile(RepositoryDescriptor repo, Path path, boolean directory) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileFacade facade = FileFacade.from(handle);
        return facade.create(path, directory);
    }

    public FileNode createFileById(UserRepositoryRef ref, Path path, boolean directory) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(ref);
        return createFile(repo, path, directory);
    }

    public boolean deleteFile(RepositoryDescriptor repo, NodeId id) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileFacade facade = FileFacade.from(handle);
        return facade.delete(id);
    }

    public boolean deleteFileById(UserRepositoryRef ref, NodeId id) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(ref);
        return deleteFile(repo, id);
    }

    public FileNode moveFile(RepositoryDescriptor repo, NodeId id, Path targetPath) {
        RepositoryHandle handle = repositoryService.openRepository(repo);
        FileFacade facade = FileFacade.from(handle);
        return facade.move(id, targetPath);
    }

    public FileNode moveFileById(UserRepositoryRef ref, NodeId id, Path targetPath) {
        RepositoryDescriptor repo = repositoryCatalog.getOrThrow(ref);
        return moveFile(repo, id, targetPath);
    }
}
