package plugins.onedrive;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.repository.FileHandle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OneDriveFileHandle implements FileHandle {
    private final OneDriveApiClient client;
    private final String rootPath;

    public OneDriveFileHandle(OneDriveApiClient client, String rootPath) {
        this.client = client;
        this.rootPath = rootPath;
    }

    @Override
    public FileNode get(NodeId id) {
        OneDriveItem item = client.getItem(id.value());
        return toFileNode(item);
    }

    @Override
    public List<FileNode> list(Path path, PageRequest page) {
        String resolved = resolvePath(path.toString());
        List<OneDriveItem> items = client.listChildren(resolved);
        List<FileNode> nodes = new ArrayList<>();
        for (OneDriveItem item : items) {
            nodes.add(toFileNode(item));
        }
        return nodes;
    }

    @Override
    public DownloadStream download(NodeId id) {
        return client.download(id.value());
    }

    private String resolvePath(String path) {
        if (rootPath == null || rootPath.isBlank()) {
            return path;
        }
        String normalized = path == null ? "" : path;
        if (".".equals(normalized) || "/".equals(normalized)) {
            return rootPath;
        }
        String trimmedRoot = rootPath.startsWith("/") ? rootPath.substring(1) : rootPath;
        String trimmedPath = normalized.startsWith("/") ? normalized.substring(1) : normalized;
        return trimmedRoot + "/" + trimmedPath;
    }

    private FileNode toFileNode(OneDriveItem item) {
        return new FileNode(new NodeId(item.id()), item.path(), item.name(), item.isFile(), item.metadata());
    }
}
