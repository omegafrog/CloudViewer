package user.web.dto;

import api.user.PersonalInfo;

import java.util.Objects;

public record UserRegisterRequest(String username, String password, PersonalInfo personalInfo) {
    public UserRegisterRequest {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");
        Objects.requireNonNull(personalInfo, "personalInfo");
    }
}
