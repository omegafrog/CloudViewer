package api.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonalInfoTest {

    @Test
    void requiresAllFields() {
        assertThrows(NullPointerException.class, () -> new PersonalInfo(null, "email", "url"));
        assertThrows(NullPointerException.class, () -> new PersonalInfo("name", null, "url"));
        assertThrows(NullPointerException.class, () -> new PersonalInfo("name", "email", null));
    }

    @Test
    void preservesValues() {
        PersonalInfo info = new PersonalInfo("name", "email", "url");
        assertEquals("name", info.name());
        assertEquals("email", info.email());
        assertEquals("url", info.profileImageUrl());
    }
}
