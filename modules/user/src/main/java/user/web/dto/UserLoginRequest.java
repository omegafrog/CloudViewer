package user.web.dto;

import java.util.Objects;

public record UserLoginRequest(String username, String password) {
    public UserLoginRequest {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");
    }
}
