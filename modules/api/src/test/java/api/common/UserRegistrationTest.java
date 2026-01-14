package api.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRegistrationTest {

    @Test
    void requiresUserId() {
        assertThrows(NullPointerException.class, () -> new UserRegistration(null));
    }

    @Test
    void preservesUserId() {
        UserRegistration registration = new UserRegistration("user-1");
        assertEquals("user-1", registration.userId());
    }
}
