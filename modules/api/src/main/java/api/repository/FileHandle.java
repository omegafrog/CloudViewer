package api.repository;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;

import java.nio.file.Path;
import java.util.List;

public interface FileHandle {
    FileNode get(NodeId id);
    List<FileNode> list(Path path, PageRequest page);
    DownloadStream download(NodeId id);
}
