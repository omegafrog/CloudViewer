package api.common;

import java.util.Map;
import java.util.Objects;

public record RepositoryDescriptor(String repositoryId, String type, Map<String, String> config) {
    public RepositoryDescriptor {
        Objects.requireNonNull(repositoryId, "repositoryId");
        Objects.requireNonNull(type, "type");
        if (config == null) {
            config = Map.of();
        } else {
            config = Map.copyOf(config);
        }
    }
}
