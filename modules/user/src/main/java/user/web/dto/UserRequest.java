package user.web.dto;

import api.common.UserRegistration;

import java.util.Objects;

public record UserRequest(String userId) {
    public UserRequest {
        Objects.requireNonNull(userId, "userId");
    }

    public UserRegistration toRegistration() {
        return new UserRegistration(userId);
    }
}
