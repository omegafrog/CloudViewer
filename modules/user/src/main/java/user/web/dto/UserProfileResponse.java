package user.web.dto;

import api.user.UserProfile;

import java.util.Objects;

public record UserProfileResponse(UserProfile profile) {
    public UserProfileResponse {
        Objects.requireNonNull(profile, "profile");
    }
}
