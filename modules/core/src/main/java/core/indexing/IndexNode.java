package core.indexing;

import java.util.List;
import java.util.Objects;

public record IndexNode(String path, String name, boolean isFile, List<IndexNode> children) {
    public IndexNode {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(name, "name");
        if (children == null) {
            children = List.of();
        } else {
            children = List.copyOf(children);
        }
    }
}
