package api.repository;

import api.common.IndexNode;
import java.nio.file.Path;

public interface PluginIndexingHandle {
    IndexNode index(Path root, int depth);
}
