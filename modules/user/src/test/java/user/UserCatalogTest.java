package user;

import api.user.PersonalInfo;
import api.user.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserCatalogTest {

    @Test
    void registerStoresUser() {
        UserCatalog catalog = new UserCatalog();
        PersonalInfo info = new PersonalInfo("name", "user@example.com", "url");
        UserAccount account = UserAccount.createPasswordUser(new UserId("user-1"), "user", "hash", info);

        UserAccount stored = catalog.register(account);

        assertEquals(account, stored);
        assertEquals(account, catalog.getOrThrow("user-1"));
    }

    @Test
    void getOrThrowFailsWhenMissing() {
        UserCatalog catalog = new UserCatalog();

        assertThrows(IllegalStateException.class, () -> catalog.getOrThrow("user-404"));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        UserCatalog catalog = new UserCatalog();
        PersonalInfo info = new PersonalInfo("name", "user@example.com", "url");
        UserAccount first = UserAccount.createPasswordUser(new UserId("user-1"), "user1", "hash", info);
        UserAccount second = UserAccount.createPasswordUser(new UserId("user-2"), "user2", "hash", info);

        catalog.register(first);

        assertThrows(IllegalStateException.class, () -> catalog.register(second));
    }
}
