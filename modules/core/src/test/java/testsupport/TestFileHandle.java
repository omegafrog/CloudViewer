package testsupport;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.repository.FileCommandHandle;
import api.repository.FileHandle;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TestFileHandle implements FileHandle, FileCommandHandle {
    private int getCalls;
    private int listCalls;
    private int downloadCalls;
    private int createCalls;
    private int deleteCalls;
    private int moveCalls;

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

    @Override
    public FileNode create(Path path, boolean directory) {
        createCalls++;
        String name = path.getFileName() == null ? "/" : path.getFileName().toString();
        return new FileNode(new NodeId("created:" + name), path.toString(), name, directory, Map.of());
    }

    @Override
    public boolean delete(NodeId id) {
        deleteCalls++;
        return true;
    }

    @Override
    public FileNode move(NodeId id, Path targetPath) {
        moveCalls++;
        String name = targetPath.getFileName() == null ? "/" : targetPath.getFileName().toString();
        return new FileNode(id, targetPath.toString(), name, true, Map.of());
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

    public int createCalls() {
        return createCalls;
    }

    public int deleteCalls() {
        return deleteCalls;
    }

    public int moveCalls() {
        return moveCalls;
    }
}
