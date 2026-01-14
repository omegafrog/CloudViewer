package user;

import api.user.PersonalInfo;
import api.user.UserProfile;
import api.user.UserRole;
import api.user.UserSession;
import api.user.UserStatus;
import user.UserService.PersonalInfoUpdate;
import user.oauth.OAuthAuthorizationRequest;
import user.oauth.OAuthCallbackRequest;
import user.oauth.OAuthProvider;
import user.oauth.OAuthProviderRegistry;
import user.oauth.OAuthStateCatalog;
import user.oauth.OAuthUserInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    @Test
    void registerWithPasswordReturnsProfile() {
        UserService service = buildService();
        PersonalInfo info = new PersonalInfo("name", "email", "url");

        UserProfile profile = service.registerWithPassword("user", "pw", info);

        assertEquals("user", profile.username());
        assertEquals(UserRole.USER, profile.role());
        assertEquals(UserStatus.ACTIVE, profile.status());
    }

    @Test
    void loginWithPasswordFailsWhenInvalidPassword() {
        UserService service = buildService();
        PersonalInfo info = new PersonalInfo("name", "email", "url");
        service.registerWithPassword("user", "pw", info);

        assertThrows(IllegalStateException.class, () -> service.loginWithPassword("user", "bad"));
    }

    @Test
    void loginWithOAuthRegistersUser() {
        UserService service = buildService();
        String state = service.startOAuthLogin("google", "https://app/callback").state();
        UserSession session = service.loginWithOAuth("google", "code-1", state, "https://app/callback");
        UserProfile profile = service.getProfile(session.sessionId());

        assertEquals("email", profile.personalInfo().email());
    }

    @Test
    void updateProfileChangesEmail() {
        UserService service = buildService();
        PersonalInfo info = new PersonalInfo("name", "email", "url");
        service.registerWithPassword("user", "pw", info);
        UserSession session = service.loginWithPassword("user", "pw");

        UserProfile profile = service.updateProfile(session.sessionId(),
                new PersonalInfoUpdate(null, "email2", null));

        assertEquals("email2", profile.personalInfo().email());
    }

    @Test
    void adminUpdateRequiresAdmin() {
        UserCatalog catalog = new UserCatalog();
        SessionCatalog sessions = new SessionCatalog();
        OAuthProviderRegistry registry = new OAuthProviderRegistry(List.of(new TestOAuthProvider()));
        UserService service = new UserService(catalog, sessions, registry, new OAuthStateCatalog());

        PersonalInfo info = new PersonalInfo("name", "email", "url");
        UserAccount admin = UserAccount.createAdmin(new api.user.UserId("admin"), "admin", info);
        catalog.register(admin);
        UserSession adminSession = sessions.create(admin.userId());

        PersonalInfo userInfo = new PersonalInfo("user", "user@example.com", "url");
        UserProfile userProfile = service.registerWithPassword("user", "pw", userInfo);

        UserProfile updated = service.adminUpdateUser(adminSession.sessionId(), userProfile.userId().value(),
                new PersonalInfoUpdate("new", null, null), null);

        assertEquals("new", updated.personalInfo().name());
    }

    private UserService buildService() {
        UserCatalog catalog = new UserCatalog();
        SessionCatalog sessions = new SessionCatalog();
        OAuthProviderRegistry registry = new OAuthProviderRegistry(List.of(new TestOAuthProvider()));
        return new UserService(catalog, sessions, registry, new OAuthStateCatalog());
    }

    private static class TestOAuthProvider implements OAuthProvider {
        @Override
        public String providerId() {
            return "google";
        }

        @Override
        public String defaultScope() {
            return "openid email profile";
        }

        @Override
        public String buildAuthorizationUrl(OAuthAuthorizationRequest request) {
            return "https://auth?state=" + request.state();
        }

        @Override
        public OAuthUserInfo exchange(OAuthCallbackRequest request) {
            return new OAuthUserInfo("p1", "email", "name", "url");
        }
    }
}
