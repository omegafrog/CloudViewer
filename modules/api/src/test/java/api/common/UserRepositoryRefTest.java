package api.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRepositoryRefTest {
    @Test
    void requiresUserId() {
        assertThrows(NullPointerException.class, () -> new UserRepositoryRef(null, "repo-1"));
    }

    @Test
    void requiresRepositoryId() {
        assertThrows(NullPointerException.class, () -> new UserRepositoryRef("user-1", null));
    }

    @Test
    void preservesValues() {
        UserRepositoryRef ref = new UserRepositoryRef("user-1", "repo-1");
        assertEquals("user-1", ref.userId());
        assertEquals("repo-1", ref.repositoryId());
    }
}
