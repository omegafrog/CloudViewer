package user.web;

import api.user.UserProfile;
import api.user.UserSession;
import user.UserService;
import user.UserService.PersonalInfoUpdate;
import user.UserService.OAuthStartResult;
import user.web.dto.OAuthAuthorizeResponse;
import user.web.dto.AdminUserDeactivateRequest;
import user.web.dto.AdminUserUpdateRequest;
import user.oauth.GoogleOAuthProperties;
import user.web.dto.UserLoginRequest;
import user.web.dto.UserProfileResponse;
import user.web.dto.UserRegisterRequest;
import user.web.dto.UserSessionRequest;
import user.web.dto.UserSessionResponse;
import user.web.dto.UserUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CookieValue;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "User registration, login, and profile management.")
public class UserController {
    private static final String SESSION_COOKIE = "CV_SESSION";
    private final UserService userService;
    private final GoogleOAuthProperties googleOAuthProperties;

    public UserController(UserService userService, GoogleOAuthProperties googleOAuthProperties) {
        this.userService = userService;
        this.googleOAuthProperties = googleOAuthProperties;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register with username/password",
            description = "Creates a new user using username/password and personal info. Returns the created profile."
    )
    public UserProfileResponse register(@RequestBody UserRegisterRequest request) {
        UserProfile profile = userService.registerWithPassword(
                request.username(),
                request.password(),
                request.personalInfo()
        );
        return new UserProfileResponse(profile);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login with username/password",
            description = "Authenticates credentials and returns a session + profile. Also sets the session cookie."
    )
    public ResponseEntity<UserSessionResponse> login(@RequestBody UserLoginRequest request) {
        UserSession session = userService.loginWithPassword(request.username(), request.password());
        UserProfile profile = userService.getProfile(session.sessionId());
        return ResponseEntity.ok()
                .header("Set-Cookie", buildSessionCookie(session.sessionId()).toString())
                .body(new UserSessionResponse(session, profile));
    }

    @GetMapping("/oauth/authorize")
    @Operation(
            summary = "Start OAuth authorization",
            description = "Builds the provider authorization URL and returns a state value to validate the callback."
    )
    public OAuthAuthorizeResponse oauthAuthorize(@RequestParam("providerId") String providerId,
                                                 @RequestParam(value = "redirectUri", required = false) String redirectUri) {
        String resolvedRedirectUri = resolveRedirectUri(providerId, redirectUri, null);
        OAuthStartResult result = userService.startOAuthLogin(providerId, resolvedRedirectUri);
        return new OAuthAuthorizeResponse(result.authorizationUrl(), result.state());
    }

    @GetMapping("/oauth/google/callback")
    @Operation(
            summary = "Google OAuth callback",
            description = "Consumes code/state from Google, completes login or signup, and returns a session + profile. Also sets the session cookie."
    )
    public ResponseEntity<UserSessionResponse> oauthGoogleCallback(@RequestParam("code") String code,
                                                                   @RequestParam("state") String state,
                                                                   @RequestParam(value = "redirectUri", required = false) String redirectUri,
                                                                   HttpServletRequest httpRequest) {
        String resolvedRedirectUri = resolveRedirectUri("google", redirectUri, httpRequest);
        UserSession session = userService.loginWithOAuth("google", code, state, resolvedRedirectUri);
        UserProfile profile = userService.getProfile(session.sessionId());
        return ResponseEntity.ok()
                .header("Set-Cookie", buildSessionCookie(session.sessionId()).toString())
                .body(new UserSessionResponse(session, profile));
    }

    @PostMapping("/profile")
    @Operation(
            summary = "Get current profile",
            description = "Returns the profile of the current session. Uses cookie session when present, otherwise request body."
    )
    public UserProfileResponse profile(@RequestBody UserSessionRequest request,
                                       @CookieValue(name = SESSION_COOKIE, required = false) String cookieSessionId) {
        String sessionId = resolveSessionId(request.sessionId(), cookieSessionId);
        return new UserProfileResponse(userService.getProfile(sessionId));
    }

    @PostMapping("/update")
    @Operation(
            summary = "Update personal info",
            description = "Updates name/email/profile image for the current session. Uses cookie session when present."
    )
    public UserProfileResponse update(@RequestBody UserUpdateRequest request,
                                      @CookieValue(name = SESSION_COOKIE, required = false) String cookieSessionId) {
        String sessionId = resolveSessionId(request.sessionId(), cookieSessionId);
        PersonalInfoUpdate update = new PersonalInfoUpdate(
                request.personalInfo().name(),
                request.personalInfo().email(),
                request.personalInfo().profileImageUrl()
        );
        return new UserProfileResponse(userService.updateProfile(sessionId, update));
    }

    @PostMapping("/withdraw")
    @Operation(
            summary = "Withdraw user",
            description = "Soft-deletes the current user and clears the session cookie."
    )
    public ResponseEntity<Void> withdraw(@RequestBody UserSessionRequest request,
                                         @CookieValue(name = SESSION_COOKIE, required = false) String cookieSessionId) {
        String sessionId = resolveSessionId(request.sessionId(), cookieSessionId);
        userService.withdraw(sessionId);
        return ResponseEntity.ok()
                .header("Set-Cookie", clearSessionCookie().toString())
                .build();
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout",
            description = "Removes the current session and clears the session cookie."
    )
    public ResponseEntity<Void> logout(@RequestBody UserSessionRequest request,
                                       @CookieValue(name = SESSION_COOKIE, required = false) String cookieSessionId) {
        String sessionId = resolveSessionId(request.sessionId(), cookieSessionId);
        userService.logout(sessionId);
        return ResponseEntity.ok()
                .header("Set-Cookie", clearSessionCookie().toString())
                .build();
    }

    @PostMapping("/admin/update")
    @Operation(
            summary = "Admin update user",
            description = "Allows an admin session to update a normal user's personal info or role."
    )
    public UserProfileResponse adminUpdate(@RequestBody AdminUserUpdateRequest request) {
        PersonalInfoUpdate update = new PersonalInfoUpdate(
                request.personalInfo().name(),
                request.personalInfo().email(),
                request.personalInfo().profileImageUrl()
        );
        UserProfile profile = userService.adminUpdateUser(
                request.adminSessionId(),
                request.targetUserId(),
                update,
                request.role()
        );
        return new UserProfileResponse(profile);
    }

    @PostMapping("/admin/deactivate")
    @Operation(
            summary = "Admin deactivate user",
            description = "Allows an admin session to soft-delete a normal user and revoke their session."
    )
    public ResponseEntity<Void> adminDeactivate(@RequestBody AdminUserDeactivateRequest request) {
        userService.adminDeactivateUser(request.adminSessionId(), request.targetUserId());
        return ResponseEntity.ok().build();
    }

    private String resolveSessionId(String bodySessionId, String cookieSessionId) {
        if (cookieSessionId != null && !cookieSessionId.isBlank()) {
            return cookieSessionId;
        }
        if (bodySessionId != null && !bodySessionId.isBlank()) {
            return bodySessionId;
        }
        throw new IllegalStateException("Precondition violation: sessionId missing");
    }

    private ResponseCookie buildSessionCookie(String sessionId) {
        return ResponseCookie.from(SESSION_COOKIE, sessionId)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie clearSessionCookie() {
        return ResponseCookie.from(SESSION_COOKIE, "")
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
    }

    private String resolveRedirectUri(String providerId, String redirectUri, HttpServletRequest request) {
        if (redirectUri != null && !redirectUri.isBlank()) {
            return redirectUri;
        }
        if ("google".equals(providerId) && googleOAuthProperties.redirectUri() != null
                && !googleOAuthProperties.redirectUri().isBlank()) {
            return googleOAuthProperties.redirectUri();
        }
        if (request != null) {
            return request.getRequestURL().toString();
        }
        throw new IllegalStateException("Precondition violation: redirectUri missing");
    }
}
