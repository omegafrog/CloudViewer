package user;

import api.user.PersonalInfo;
import api.user.UserId;
import api.user.UserRole;
import api.user.UserStatus;
import user.oauth.OAuthIdentity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserAccount {
    private final UserId userId;
    private String username;
    private String passwordHash;
    private PersonalInfo personalInfo;
    private UserRole role;
    private UserStatus status;
    private final Set<OAuthIdentity> oauthIdentities = new HashSet<>();

    private UserAccount(UserId userId,
                        String username,
                        String passwordHash,
                        PersonalInfo personalInfo,
                        UserRole role,
                        UserStatus status) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.username = Objects.requireNonNull(username, "username");
        this.passwordHash = passwordHash;
        this.personalInfo = Objects.requireNonNull(personalInfo, "personalInfo");
        this.role = Objects.requireNonNull(role, "role");
        this.status = Objects.requireNonNull(status, "status");
    }

    public static UserAccount createPasswordUser(UserId userId,
                                                 String username,
                                                 String passwordHash,
                                                 PersonalInfo personalInfo) {
        Objects.requireNonNull(passwordHash, "passwordHash");
        return new UserAccount(userId, username, passwordHash, personalInfo, UserRole.USER, UserStatus.ACTIVE);
    }

    public static UserAccount createOAuthUser(UserId userId,
                                              String username,
                                              PersonalInfo personalInfo,
                                              OAuthIdentity identity) {
        UserAccount account = new UserAccount(userId, username, null, personalInfo, UserRole.USER, UserStatus.ACTIVE);
        account.oauthIdentities.add(Objects.requireNonNull(identity, "identity"));
        return account;
    }

    public static UserAccount createAdmin(UserId userId, String username, PersonalInfo personalInfo) {
        return new UserAccount(userId, username, null, personalInfo, UserRole.ADMIN, UserStatus.ACTIVE);
    }

    public UserId userId() {
        return userId;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public PersonalInfo personalInfo() {
        return personalInfo;
    }

    public UserRole role() {
        return role;
    }

    public UserStatus status() {
        return status;
    }

    public Set<OAuthIdentity> oauthIdentities() {
        return Set.copyOf(oauthIdentities);
    }

    public void updatePersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = Objects.requireNonNull(personalInfo, "personalInfo");
    }

    public void updateRole(UserRole role) {
        this.role = Objects.requireNonNull(role, "role");
    }

    public void softDelete() {
        this.status = UserStatus.DELETED;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public void attachOAuthIdentity(OAuthIdentity identity) {
        oauthIdentities.add(Objects.requireNonNull(identity, "identity"));
    }
}
