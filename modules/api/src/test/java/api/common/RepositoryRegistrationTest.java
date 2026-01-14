package api.common;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RepositoryRegistrationTest {
    @Test
    void requiresUserId() {
        assertThrows(NullPointerException.class,
                () -> new RepositoryRegistration(null, "repo-1", "ONEDRIVE", Map.of()));
    }

    @Test
    void requiresRepositoryId() {
        assertThrows(NullPointerException.class,
                () -> new RepositoryRegistration("user-1", null, "ONEDRIVE", Map.of()));
    }

    @Test
    void requiresType() {
        assertThrows(NullPointerException.class,
                () -> new RepositoryRegistration("user-1", "repo-1", null, Map.of()));
    }

    @Test
    void wrapsDescriptor() {
        RepositoryRegistration registration = new RepositoryRegistration(
                "user-1", "repo-1", "ONEDRIVE", Map.of("key", "value"));
        RepositoryDescriptor descriptor = registration.toDescriptor();
        assertEquals("repo-1", descriptor.repositoryId());
        assertEquals("ONEDRIVE", descriptor.type());
        assertEquals("value", descriptor.config().get("key"));
    }
}
