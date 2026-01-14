package core.repository;

import api.common.RepositoryDescriptor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RepositoryCatalog {
    private final Map<String, RepositoryDescriptor> entries = new ConcurrentHashMap<>();

    public RepositoryDescriptor register(RepositoryDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
        entries.put(descriptor.repositoryId(), descriptor);
        return descriptor;
    }

    public Optional<RepositoryDescriptor> find(String repositoryId) {
        return Optional.ofNullable(entries.get(repositoryId));
    }

    public RepositoryDescriptor getOrThrow(String repositoryId) {
        Objects.requireNonNull(repositoryId, "repositoryId");
        return find(repositoryId)
                .orElseThrow(() -> preconditionViolation("Repository not registered: " + repositoryId));
    }

    private IllegalStateException preconditionViolation(String message) {
        return new IllegalStateException("Precondition violation: " + message);
    }
}
