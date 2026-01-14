package user.web.dto;

import api.user.UserRole;

import java.util.Objects;

public record AdminUserUpdateRequest(String adminSessionId, String targetUserId,
                                     PersonalInfoUpdateRequest personalInfo, UserRole role) {
    public AdminUserUpdateRequest {
        Objects.requireNonNull(adminSessionId, "adminSessionId");
        Objects.requireNonNull(targetUserId, "targetUserId");
        Objects.requireNonNull(personalInfo, "personalInfo");
    }
}
