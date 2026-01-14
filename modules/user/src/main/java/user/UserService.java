package user;

import api.user.PersonalInfo;
import api.user.UserId;
import api.user.UserProfile;
import api.user.UserRole;
import api.user.UserSession;
import api.user.UserStatus;
import user.oauth.OAuthIdentity;
import user.oauth.OAuthAuthorizationRequest;
import user.oauth.OAuthCallbackRequest;
import user.oauth.OAuthProvider;
import user.oauth.OAuthProviderRegistry;
import user.oauth.OAuthStateCatalog;
import user.oauth.OAuthUserInfo;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {
    private final UserCatalog userCatalog;
    private final SessionCatalog sessionCatalog;
    private final OAuthProviderRegistry oauthProviderRegistry;
    private final OAuthStateCatalog oauthStateCatalog;
    private final PasswordHasher passwordHasher = new PasswordHasher();

    public UserService(UserCatalog userCatalog,
                       SessionCatalog sessionCatalog,
                       OAuthProviderRegistry oauthProviderRegistry,
                       OAuthStateCatalog oauthStateCatalog) {
        this.userCatalog = userCatalog;
        this.sessionCatalog = sessionCatalog;
        this.oauthProviderRegistry = oauthProviderRegistry;
        this.oauthStateCatalog = oauthStateCatalog;
    }

    public UserProfile registerWithPassword(String username, String password, PersonalInfo personalInfo) {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");
        Objects.requireNonNull(personalInfo, "personalInfo");

        String hash = passwordHasher.hash(password);
        UserAccount account = UserAccount.createPasswordUser(new UserId(generateUserId()), username, hash, personalInfo);
        userCatalog.register(account);
        return toProfile(account);
    }

    public UserSession loginWithPassword(String username, String password) {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");

        UserAccount account = userCatalog.findByUsername(username)
                .orElseThrow(() -> preconditionViolation("User not found: " + username));
        if (!account.isActive()) {
            throw preconditionViolation("User inactive: " + username);
        }
        if (account.passwordHash() == null) {
            throw preconditionViolation("Password login unavailable for user: " + username);
        }
        String hash = passwordHasher.hash(password);
        if (!hash.equals(account.passwordHash())) {
            throw preconditionViolation("Invalid password for user: " + username);
        }
        return sessionCatalog.create(account.userId());
    }

    public OAuthStartResult startOAuthLogin(String providerId, String redirectUri) {
        Objects.requireNonNull(providerId, "providerId");
        Objects.requireNonNull(redirectUri, "redirectUri");
        OAuthProvider provider = oauthProviderRegistry.getOrThrow(providerId);
        String state = oauthStateCatalog.issue(providerId);
        OAuthAuthorizationRequest request = new OAuthAuthorizationRequest(
                redirectUri,
                state,
                provider.defaultScope()
        );
        String authorizationUrl = provider.buildAuthorizationUrl(request);
        return new OAuthStartResult(authorizationUrl, state);
    }

    public UserSession loginWithOAuth(String providerId, String code, String state, String redirectUri) {
        Objects.requireNonNull(providerId, "providerId");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(redirectUri, "redirectUri");
        oauthStateCatalog.validateAndConsume(state, providerId);

        OAuthProvider provider = oauthProviderRegistry.getOrThrow(providerId);
        OAuthUserInfo info = provider.exchange(new OAuthCallbackRequest(code, redirectUri));
        OAuthIdentity identity = new OAuthIdentity(providerId, info.providerUserId());

        UserAccount account = userCatalog.findByOAuthIdentity(identity)
                .orElseGet(() -> resolveOrRegisterOAuthUser(info, identity));

        if (!account.isActive()) {
            throw preconditionViolation("User inactive: " + account.userId().value());
        }
        return sessionCatalog.create(account.userId());
    }

    public UserProfile updateProfile(String sessionId, PersonalInfoUpdate update) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(update, "update");

        UserAccount account = resolveSessionUser(sessionId);
        PersonalInfo updated = update.applyTo(account.personalInfo());
        UserAccount updatedAccount = userCatalog.updatePersonalInfo(account.userId().value(), updated);
        return toProfile(updatedAccount);
    }

    public void withdraw(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");
        UserAccount account = resolveSessionUser(sessionId);
        userCatalog.softDelete(account.userId().value());
        sessionCatalog.removeByUserId(account.userId().value());
    }

    public void logout(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");
        sessionCatalog.remove(sessionId);
    }

    public UserProfile adminUpdateUser(String adminSessionId, String targetUserId, PersonalInfoUpdate update, UserRole role) {
        Objects.requireNonNull(adminSessionId, "adminSessionId");
        Objects.requireNonNull(targetUserId, "targetUserId");
        Objects.requireNonNull(update, "update");

        UserAccount admin = requireAdmin(adminSessionId);
        UserAccount target = userCatalog.getOrThrow(targetUserId);
        if (target.role() != UserRole.USER) {
            throw preconditionViolation("Admin can manage only normal users: " + targetUserId);
        }
        PersonalInfo updatedInfo = update.applyTo(target.personalInfo());
        userCatalog.updatePersonalInfo(targetUserId, updatedInfo);
        if (role != null) {
            userCatalog.updateRole(targetUserId, role);
        }
        return toProfile(userCatalog.getOrThrow(targetUserId));
    }

    public void adminDeactivateUser(String adminSessionId, String targetUserId) {
        Objects.requireNonNull(adminSessionId, "adminSessionId");
        Objects.requireNonNull(targetUserId, "targetUserId");

        requireAdmin(adminSessionId);
        UserAccount target = userCatalog.getOrThrow(targetUserId);
        if (target.role() != UserRole.USER) {
            throw preconditionViolation("Admin can manage only normal users: " + targetUserId);
        }
        userCatalog.softDelete(targetUserId);
        sessionCatalog.removeByUserId(targetUserId);
    }

    public UserProfile getProfile(String sessionId) {
        UserAccount account = resolveSessionUser(sessionId);
        return toProfile(account);
    }

    private UserAccount resolveSessionUser(String sessionId) {
        UserSession session = sessionCatalog.find(sessionId)
                .orElseThrow(() -> preconditionViolation("Session not found: " + sessionId));
        UserAccount account = userCatalog.getOrThrow(session.userId().value());
        if (!account.isActive()) {
            throw preconditionViolation("User inactive: " + account.userId().value());
        }
        return account;
    }

    private UserAccount requireAdmin(String sessionId) {
        UserAccount account = resolveSessionUser(sessionId);
        if (account.role() != UserRole.ADMIN) {
            throw preconditionViolation("Admin privileges required: " + account.userId().value());
        }
        return account;
    }

    private UserAccount resolveOrRegisterOAuthUser(OAuthUserInfo info, OAuthIdentity identity) {
        return userCatalog.findByEmail(info.email())
                .map(existing -> userCatalog.attachOAuthIdentity(existing.userId().value(), identity))
                .orElseGet(() -> registerOAuthUser(info, identity));
    }

    private UserAccount registerOAuthUser(OAuthUserInfo info, OAuthIdentity identity) {
        PersonalInfo personalInfo = new PersonalInfo(info.name(), info.email(), info.profileImageUrl());
        String username = "oauth-" + identity.providerId() + "-" + identity.providerUserId();
        UserAccount account = UserAccount.createOAuthUser(new UserId(generateUserId()), username, personalInfo, identity);
        userCatalog.register(account);
        return account;
    }

    private UserProfile toProfile(UserAccount account) {
        return new UserProfile(account.userId(), account.username(), account.personalInfo(), account.role(), account.status());
    }

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }

    private IllegalStateException preconditionViolation(String message) {
        return new IllegalStateException("Precondition violation: " + message);
    }

    public record PersonalInfoUpdate(String name, String email, String profileImageUrl) {
        public PersonalInfoUpdate {
            if (name == null && email == null && profileImageUrl == null) {
                throw new IllegalArgumentException("At least one field must be provided for update");
            }
        }

        public PersonalInfo applyTo(PersonalInfo current) {
            String nextName = name == null ? current.name() : name;
            String nextEmail = email == null ? current.email() : email;
            String nextProfile = profileImageUrl == null ? current.profileImageUrl() : profileImageUrl;
            return new PersonalInfo(nextName, nextEmail, nextProfile);
        }
    }

    public record OAuthStartResult(String authorizationUrl, String state) {
        public OAuthStartResult {
            Objects.requireNonNull(authorizationUrl, "authorizationUrl");
            Objects.requireNonNull(state, "state");
        }
    }
}
