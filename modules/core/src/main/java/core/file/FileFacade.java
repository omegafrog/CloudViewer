package core.file;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.repository.FileCommandHandle;
import api.repository.FileHandle;
import api.repository.RepositoryHandle;
import java.nio.file.Path;
import java.util.List;

public class FileFacade {
    private final FileHandle fileHandle;
    private final FileCommandHandle commandHandle;

    private FileFacade(FileHandle fileHandle) {
        this.fileHandle = fileHandle;
        this.commandHandle = fileHandle instanceof FileCommandHandle
                ? (FileCommandHandle) fileHandle
                : null;
    }

    public static FileFacade from(RepositoryHandle handle) {
        return new FileFacade(handle.fileHandle());
    }

    public FileNode get(NodeId id) {
        return fileHandle.get(id);
    }

    public List<FileNode> list(Path path, PageRequest page) {
        return fileHandle.list(path, page);
    }

    public DownloadStream download(NodeId id) {
        return fileHandle.download(id);
    }

    public FileNode create(Path path, boolean directory) {
        return commandHandleOrThrow().create(path, directory);
    }

    public boolean delete(NodeId id) {
        return commandHandleOrThrow().delete(id);
    }

    public FileNode move(NodeId id, Path targetPath) {
        return commandHandleOrThrow().move(id, targetPath);
    }

    private FileCommandHandle commandHandleOrThrow() {
        if (commandHandle == null) {
            throw preconditionViolation("File mutations are not supported by this repository.");
        }
        return commandHandle;
    }

    private IllegalStateException preconditionViolation(String message) {
        return new IllegalStateException("Precondition violation: " + message);
    }
}
