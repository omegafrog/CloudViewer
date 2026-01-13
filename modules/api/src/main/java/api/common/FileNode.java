package api.common;

import java.util.Map;

public record FileNode(NodeId id, String path, String name, boolean isFile, Map<String, String> metadata) {}
