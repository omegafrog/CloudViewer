package core.repository;

import api.common.RepositoryDescriptor;
import api.common.RepositoryRegistration;
import api.common.UserRepositoryRef;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public boolean unregister(UserRepositoryRef ref) {
        Objects.requireNonNull(ref, "ref");
        return entries.remove(ref) != null;
    }

    public List<RepositoryRegistration> listByUserId(String userId) {
        Objects.requireNonNull(userId, "userId");
        return entries.entrySet().stream()
                .filter(entry -> userId.equals(entry.getKey().userId()))
                .map(entry -> new RepositoryRegistration(
                        entry.getKey().userId(),
                        entry.getKey().repositoryId(),
                        entry.getValue().type(),
                        entry.getValue().config()))
                .collect(Collectors.toList());
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
