package user.web.dto;

import java.util.Objects;

public record UserUpdateRequest(String sessionId, PersonalInfoUpdateRequest personalInfo) {
    public UserUpdateRequest {
        Objects.requireNonNull(personalInfo, "personalInfo");
    }
}
