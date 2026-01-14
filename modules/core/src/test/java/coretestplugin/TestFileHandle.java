package coretestplugin;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.repository.FileHandle;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TestFileHandle implements FileHandle {
    @Override
    public FileNode get(NodeId id) {
        return new FileNode(id, "/", id.value(), true, Map.of());
    }

    @Override
    public List<FileNode> list(Path path, PageRequest page) {
        return List.of();
    }

    @Override
    public DownloadStream download(NodeId id) {
        return new DownloadStream(new ByteArrayInputStream(new byte[0]), "application/octet-stream", 0);
    }
}
