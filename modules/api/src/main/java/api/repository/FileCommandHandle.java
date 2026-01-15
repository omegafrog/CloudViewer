package api.repository;

import api.common.FileNode;
import api.common.NodeId;
import java.nio.file.Path;

public interface FileCommandHandle {
    FileNode create(Path path, boolean directory);
    boolean delete(NodeId id);
    FileNode move(NodeId id, Path targetPath);
}
