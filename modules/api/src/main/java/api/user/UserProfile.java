package api.user;

import java.util.Objects;

public record UserProfile(UserId userId, String username, PersonalInfo personalInfo, UserRole role, UserStatus status) {
    public UserProfile {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(personalInfo, "personalInfo");
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(status, "status");
    }
}
