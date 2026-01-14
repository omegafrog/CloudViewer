package user.web;

import api.user.UserProfile;
import api.user.UserSession;
import user.UserService;
import user.UserService.PersonalInfoUpdate;
import user.UserService.OAuthStartResult;
import user.web.dto.OAuthAuthorizeResponse;
import user.web.dto.AdminUserDeactivateRequest;
import user.web.dto.AdminUserUpdateRequest;
import user.web.dto.UserLoginRequest;
import user.web.dto.UserProfileResponse;
import user.web.dto.UserRegisterRequest;
import user.web.dto.UserSessionRequest;
import user.web.dto.UserSessionResponse;
import user.web.dto.UserUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
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
public class UserController {
    private static final String SESSION_COOKIE = "CV_SESSION";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserProfileResponse register(@RequestBody UserRegisterRequest request) {
        UserProfile profile = userService.registerWithPassword(
                request.username(),
                request.password(),
                request.personalInfo()
        );
        return new UserProfileResponse(profile);
    }

    @PostMapping("/login")
    public ResponseEntity<UserSessionResponse> login(@RequestBody UserLoginRequest request) {
        UserSession session = userService.loginWithPassword(request.username(), request.password());
        UserProfile profile = userService.getProfile(session.sessionId());
        return ResponseEntity.ok()
                .header("Set-Cookie", buildSessionCookie(session.sessionId()).toString())
                .body(new UserSessionResponse(session, profile));
    }

    @GetMapping("/oauth/authorize")
    public OAuthAuthorizeResponse oauthAuthorize(@RequestParam String providerId,
                                                 @RequestParam String redirectUri) {
        OAuthStartResult result = userService.startOAuthLogin(providerId, redirectUri);
        return new OAuthAuthorizeResponse(result.authorizationUrl(), result.state());
    }

    @GetMapping("/oauth/google/callback")
    public ResponseEntity<UserSessionResponse> oauthGoogleCallback(@RequestParam String code,
                                                                   @RequestParam String state,
                                                                   @RequestParam(required = false) String redirectUri,
                                                                   HttpServletRequest httpRequest) {
        String resolvedRedirectUri = redirectUri == null || redirectUri.isBlank()
                ? httpRequest.getRequestURL().toString()
                : redirectUri;
        UserSession session = userService.loginWithOAuth("google", code, state, resolvedRedirectUri);
        UserProfile profile = userService.getProfile(session.sessionId());
        return ResponseEntity.ok()
                .header("Set-Cookie", buildSessionCookie(session.sessionId()).toString())
                .body(new UserSessionResponse(session, profile));
    }

    @PostMapping("/profile")
    public UserProfileResponse profile(@RequestBody UserSessionRequest request,
                                       @CookieValue(name = SESSION_COOKIE, required = false) String cookieSessionId) {
        String sessionId = resolveSessionId(request.sessionId(), cookieSessionId);
        return new UserProfileResponse(userService.getProfile(sessionId));
    }

    @PostMapping("/update")
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
    public ResponseEntity<Void> withdraw(@RequestBody UserSessionRequest request,
                                         @CookieValue(name = SESSION_COOKIE, required = false) String cookieSessionId) {
        String sessionId = resolveSessionId(request.sessionId(), cookieSessionId);
        userService.withdraw(sessionId);
        return ResponseEntity.ok()
                .header("Set-Cookie", clearSessionCookie().toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody UserSessionRequest request,
                                       @CookieValue(name = SESSION_COOKIE, required = false) String cookieSessionId) {
        String sessionId = resolveSessionId(request.sessionId(), cookieSessionId);
        userService.logout(sessionId);
        return ResponseEntity.ok()
                .header("Set-Cookie", clearSessionCookie().toString())
                .build();
    }

    @PostMapping("/admin/update")
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
}
