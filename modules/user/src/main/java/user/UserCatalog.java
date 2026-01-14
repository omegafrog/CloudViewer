package user;

import api.user.PersonalInfo;
import api.user.UserId;
import api.user.UserRole;
import api.user.UserStatus;
import org.springframework.stereotype.Service;

import user.oauth.OAuthIdentity;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserCatalog {
    private final Map<String, UserAccount> byId = new ConcurrentHashMap<>();
    private final Map<String, String> byUsername = new ConcurrentHashMap<>();
    private final Map<String, String> byEmail = new ConcurrentHashMap<>();
    private final Map<OAuthIdentity, String> byOAuthIdentity = new ConcurrentHashMap<>();

    public UserAccount register(UserAccount account) {
        Objects.requireNonNull(account, "account");
        String userId = account.userId().value();
        String username = account.username();
        String email = account.personalInfo().email();

        if (byId.containsKey(userId)) {
            throw preconditionViolation("User already registered: " + userId);
        }
        if (byUsername.containsKey(username)) {
            throw preconditionViolation("Username already registered: " + username);
        }
        if (byEmail.containsKey(email)) {
            throw preconditionViolation("Email already registered: " + email);
        }
        byId.put(userId, account);
        byUsername.put(username, userId);
        byEmail.put(email, userId);
        return account;
    }

    public Optional<UserAccount> findById(String userId) {
        Objects.requireNonNull(userId, "userId");
        return Optional.ofNullable(byId.get(userId));
    }

    public Optional<UserAccount> findByUsername(String username) {
        Objects.requireNonNull(username, "username");
        return Optional.ofNullable(byUsername.get(username)).flatMap(this::findById);
    }

    public Optional<UserAccount> findByEmail(String email) {
        Objects.requireNonNull(email, "email");
        return Optional.ofNullable(byEmail.get(email)).flatMap(this::findById);
    }

    public Optional<UserAccount> findByOAuthIdentity(OAuthIdentity identity) {
        Objects.requireNonNull(identity, "identity");
        return Optional.ofNullable(byOAuthIdentity.get(identity)).flatMap(this::findById);
    }

    public UserAccount getOrThrow(String userId) {
        Objects.requireNonNull(userId, "userId");
        return findById(userId)
                .orElseThrow(() -> preconditionViolation("User not registered: " + userId));
    }

    public UserAccount updatePersonalInfo(String userId, PersonalInfo personalInfo) {
        UserAccount account = getOrThrow(userId);
        String previousEmail = account.personalInfo().email();
        if (!previousEmail.equals(personalInfo.email()) && byEmail.containsKey(personalInfo.email())) {
            throw preconditionViolation("Email already registered: " + personalInfo.email());
        }
        account.updatePersonalInfo(personalInfo);
        if (!previousEmail.equals(personalInfo.email())) {
            byEmail.remove(previousEmail);
            byEmail.put(personalInfo.email(), userId);
        }
        return account;
    }

    public UserAccount updateRole(String userId, UserRole role) {
        UserAccount account = getOrThrow(userId);
        account.updateRole(role);
        return account;
    }

    public UserAccount attachOAuthIdentity(String userId, OAuthIdentity identity) {
        if (byOAuthIdentity.containsKey(identity)) {
            throw preconditionViolation("OAuth identity already linked: " + identity.providerId());
        }
        UserAccount account = getOrThrow(userId);
        account.attachOAuthIdentity(identity);
        byOAuthIdentity.put(identity, userId);
        return account;
    }

    public UserAccount softDelete(String userId) {
        UserAccount account = getOrThrow(userId);
        account.softDelete();
        return account;
    }

    public UserAccount createAdmin(UserId userId, String username, PersonalInfo personalInfo) {
        return register(UserAccount.createAdmin(userId, username, personalInfo));
    }

    private IllegalStateException preconditionViolation(String message) {
        return new IllegalStateException("Precondition violation: " + message);
    }
}
