package core.indexing;

import java.io.IOException;
import api.common.IndexNode;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileTreeIndexer {
    public IndexNode index(Path root) throws IOException {
        Objects.requireNonNull(root, "root");

        NodeBuilder rootBuilder = new NodeBuilder(root, false);
        Map<Path, NodeBuilder> builders = new HashMap<>();
        builders.put(root, rootBuilder);

        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (!dir.equals(root)) {
                    NodeBuilder dirBuilder = new NodeBuilder(dir, false);
                    builders.put(dir, dirBuilder);
                    NodeBuilder parent = builders.get(dir.getParent());
                    if (parent != null) {
                        parent.children.add(dirBuilder);
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                NodeBuilder fileBuilder = new NodeBuilder(file, true);
                NodeBuilder parent = builders.get(file.getParent());
                if (parent != null) {
                    parent.children.add(fileBuilder);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return rootBuilder.toIndexNode();
    }

    private static class NodeBuilder {
        private final String path;
        private final String name;
        private final boolean isFile;
        private final List<NodeBuilder> children = new ArrayList<>();

        private NodeBuilder(Path path, boolean isFile) {
            this.path = path.toString();
            Path fileName = path.getFileName();
            this.name = fileName == null ? path.toString() : fileName.toString();
            this.isFile = isFile;
        }

        private IndexNode toIndexNode() {
            List<IndexNode> childNodes = new ArrayList<>();
            for (NodeBuilder child : children) {
                childNodes.add(child.toIndexNode());
            }
            return new IndexNode(path, name, isFile, childNodes);
        }
    }
}
