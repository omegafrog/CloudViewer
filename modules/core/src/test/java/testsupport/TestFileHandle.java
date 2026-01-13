package testsupport;

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
    private int getCalls;
    private int listCalls;
    private int downloadCalls;

    @Override
    public FileNode get(NodeId id) {
        getCalls++;
        return new FileNode(id, "/", id.value(), true, Map.of());
    }

    @Override
    public List<FileNode> list(Path path, PageRequest page) {
        listCalls++;
        return List.of();
    }

    @Override
    public DownloadStream download(NodeId id) {
        downloadCalls++;
        return new DownloadStream(new ByteArrayInputStream(new byte[0]), "application/octet-stream", 0);
    }

    public int getCalls() {
        return getCalls;
    }

    public int listCalls() {
        return listCalls;
    }

    public int downloadCalls() {
        return downloadCalls;
    }
}
