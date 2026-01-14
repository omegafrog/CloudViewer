package user.web.dto;

import java.util.Objects;

public record UserRegistrationResponse(String userId) {
    public UserRegistrationResponse {
        Objects.requireNonNull(userId, "userId");
    }
}
