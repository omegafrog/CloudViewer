package plugins.onedrive;

import java.util.Map;
import java.util.Objects;

public record OneDriveItem(String id, String name, String path, boolean isFile, Map<String, String> metadata) {
    public OneDriveItem {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(path, "path");
        if (metadata == null) {
            metadata = Map.of();
        } else {
            metadata = Map.copyOf(metadata);
        }
    }
}
