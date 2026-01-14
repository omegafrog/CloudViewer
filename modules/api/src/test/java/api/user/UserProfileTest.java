package api.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserProfileTest {

    @Test
    void requiresFields() {
        PersonalInfo info = new PersonalInfo("name", "email", "url");
        UserId userId = new UserId("u1");

        assertThrows(NullPointerException.class, () -> new UserProfile(null, "user", info, UserRole.USER, UserStatus.ACTIVE));
        assertThrows(NullPointerException.class, () -> new UserProfile(userId, null, info, UserRole.USER, UserStatus.ACTIVE));
        assertThrows(NullPointerException.class, () -> new UserProfile(userId, "user", null, UserRole.USER, UserStatus.ACTIVE));
        assertThrows(NullPointerException.class, () -> new UserProfile(userId, "user", info, null, UserStatus.ACTIVE));
        assertThrows(NullPointerException.class, () -> new UserProfile(userId, "user", info, UserRole.USER, null));
    }

    @Test
    void preservesValues() {
        PersonalInfo info = new PersonalInfo("name", "email", "url");
        UserProfile profile = new UserProfile(new UserId("u1"), "user", info, UserRole.USER, UserStatus.ACTIVE);

        assertEquals("u1", profile.userId().value());
        assertEquals("user", profile.username());
        assertEquals("name", profile.personalInfo().name());
    }
}
