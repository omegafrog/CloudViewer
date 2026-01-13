package api.common;

import java.util.Map;
import java.util.Objects;

public record FileNode(NodeId id, String path, String name, boolean isFile, Map<String, String> metadata) {
    public FileNode {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(name, "name");
        if (metadata == null) {
            metadata = Map.of();
        } else {
            metadata = Map.copyOf(metadata);
        }
    }
}
