package plugins.onedrive;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.IndexNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.repository.FileHandle;
import api.repository.PluginIndexingHandle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OneDriveFileHandle implements FileHandle, PluginIndexingHandle {
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

    @Override
    public IndexNode index(Path root, int depth) {
        String normalizedRoot = normalizeRootPath();
        String requestedRoot = normalizePath(root == null ? "/" : root.toString());
        String rootName = nameFromPath(requestedRoot, normalizedRoot);
        List<IndexNode> children = indexChildren(resolvePath(requestedRoot), depth);
        return new IndexNode(requestedRoot, rootName, false, children);
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

    private List<IndexNode> indexChildren(String resolvedPath, int depth) {
        List<IndexNode> nodes = new ArrayList<>();
        List<OneDriveItem> items = client.listChildren(resolvedPath);
        for (OneDriveItem item : items) {
            String itemPath = normalizePath(item.path());
            List<IndexNode> children = List.of();
            if (!item.isFile() && depth > 0) {
                String relativePath = toRelativePath(itemPath);
                String childResolved = resolvePath(relativePath);
                children = indexChildren(childResolved, depth - 1);
            }
            nodes.add(new IndexNode(itemPath, item.name(), item.isFile(), children));
        }
        return nodes;
    }

    private String normalizeRootPath() {
        if (rootPath == null || rootPath.isBlank()) {
            return "";
        }
        return rootPath.startsWith("/") ? rootPath.substring(1) : rootPath;
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank() || ".".equals(path) || "/".equals(path)) {
            return "/";
        }
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private String toRelativePath(String path) {
        String normalizedRoot = normalizeRootPath();
        if (normalizedRoot.isBlank()) {
            return path;
        }
        if (path.equals(normalizedRoot)) {
            return "/";
        }
        String prefix = normalizedRoot + "/";
        if (path.startsWith(prefix)) {
            return path.substring(prefix.length());
        }
        return path;
    }

    private String nameFromPath(String requestedRoot, String normalizedRoot) {
        String base = "/".equals(requestedRoot) && !normalizedRoot.isBlank()
                ? normalizedRoot
                : requestedRoot;
        if ("/".equals(base)) {
            return "/";
        }
        String trimmed = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        int slash = trimmed.lastIndexOf('/');
        return slash >= 0 ? trimmed.substring(slash + 1) : trimmed;
    }

    private FileNode toFileNode(OneDriveItem item) {
        return new FileNode(new NodeId(item.id()), item.path(), item.name(), item.isFile(), item.metadata());
    }
}
