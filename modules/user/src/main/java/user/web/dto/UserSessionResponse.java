package user.web.dto;

import api.user.UserProfile;
import api.user.UserSession;

import java.util.Objects;

public record UserSessionResponse(UserSession session, UserProfile profile) {
    public UserSessionResponse {
        Objects.requireNonNull(session, "session");
        Objects.requireNonNull(profile, "profile");
    }
}
