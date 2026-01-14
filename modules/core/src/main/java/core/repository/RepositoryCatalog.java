package core.repository;

import api.common.RepositoryDescriptor;
import api.common.RepositoryRegistration;
import api.common.UserRepositoryRef;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RepositoryCatalog {
    private final Map<UserRepositoryRef, RepositoryDescriptor> entries = new ConcurrentHashMap<>();

    public RepositoryDescriptor register(RepositoryRegistration registration) {
        Objects.requireNonNull(registration, "registration");
        UserRepositoryRef ref = new UserRepositoryRef(registration.userId(), registration.repositoryId());
        RepositoryDescriptor descriptor = registration.toDescriptor();
        entries.put(ref, descriptor);
        return descriptor;
    }

    public Optional<RepositoryDescriptor> find(UserRepositoryRef ref) {
        Objects.requireNonNull(ref, "ref");
        return Optional.ofNullable(entries.get(ref));
    }

    public RepositoryDescriptor getOrThrow(UserRepositoryRef ref) {
        Objects.requireNonNull(ref, "ref");
        return find(ref)
                .orElseThrow(() -> preconditionViolation(
                        "Repository not registered: user=" + ref.userId() + ", repository=" + ref.repositoryId()));
    }

    private IllegalStateException preconditionViolation(String message) {
        return new IllegalStateException("Precondition violation: " + message);
    }
}
