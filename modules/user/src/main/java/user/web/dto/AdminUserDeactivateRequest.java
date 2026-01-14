package user.web.dto;

import java.util.Objects;

public record AdminUserDeactivateRequest(String adminSessionId, String targetUserId) {
    public AdminUserDeactivateRequest {
        Objects.requireNonNull(adminSessionId, "adminSessionId");
        Objects.requireNonNull(targetUserId, "targetUserId");
    }
}
