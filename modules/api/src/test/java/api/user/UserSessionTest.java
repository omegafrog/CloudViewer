package api.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserSessionTest {

    @Test
    void requiresFields() {
        UserId userId = new UserId("u1");
        assertThrows(NullPointerException.class, () -> new UserSession(null, userId));
        assertThrows(NullPointerException.class, () -> new UserSession("s1", null));
    }

    @Test
    void preservesValues() {
        UserSession session = new UserSession("s1", new UserId("u1"));
        assertEquals("s1", session.sessionId());
        assertEquals("u1", session.userId().value());
    }
}
