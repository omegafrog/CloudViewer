package user.web;

import api.user.PersonalInfo;
import api.user.UserId;
import api.user.UserProfile;
import api.user.UserRole;
import api.user.UserSession;
import api.user.UserStatus;
import user.UserService;
import user.UserService.PersonalInfoUpdate;
import user.UserService.OAuthStartResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private user.oauth.GoogleOAuthProperties googleOAuthProperties;

    @Test
    void registerReturnsProfile() throws Exception {
        PersonalInfo info = new PersonalInfo("name", "email", "url");
        UserProfile profile = new UserProfile(new UserId("u1"), "user", info, UserRole.USER, UserStatus.ACTIVE);
        when(userService.registerWithPassword(eq("user"), eq("pw"), eq(info))).thenReturn(profile);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pw\",\"personalInfo\":{\"name\":\"name\",\"email\":\"email\",\"profileImageUrl\":\"url\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.userId.value").value("u1"));
    }

    @Test
    void loginReturnsSession() throws Exception {
        UserSession session = new UserSession("s1", new UserId("u1"));
        PersonalInfo info = new PersonalInfo("name", "email", "url");
        UserProfile profile = new UserProfile(new UserId("u1"), "user", info, UserRole.USER, UserStatus.ACTIVE);
        when(userService.loginWithPassword("user", "pw")).thenReturn(session);
        when(userService.getProfile("s1")).thenReturn(profile);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pw\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("CV_SESSION=s1")))
                .andExpect(jsonPath("$.session.sessionId").value("s1"))
                .andExpect(jsonPath("$.profile.username").value("user"));
    }

    @Test
    void oauthAuthorizeReturnsUrl() throws Exception {
        when(userService.startOAuthLogin("google", "https://app/callback"))
                .thenReturn(new OAuthStartResult("https://auth", "state-1"));

        mockMvc.perform(get("/users/oauth/authorize")
                        .param("providerId", "google")
                        .param("redirectUri", "https://app/callback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorizationUrl").value("https://auth"));
    }

    @Test
    void oauthGoogleCallbackReturnsSession() throws Exception {
        UserSession session = new UserSession("s1", new UserId("u1"));
        PersonalInfo info = new PersonalInfo("name", "email", "url");
        UserProfile profile = new UserProfile(new UserId("u1"), "user", info, UserRole.USER, UserStatus.ACTIVE);
        when(userService.loginWithOAuth(eq("google"), eq("code-1"), eq("state-1"), eq("https://app/callback")))
                .thenReturn(session);
        when(userService.getProfile("s1")).thenReturn(profile);

        mockMvc.perform(get("/users/oauth/google/callback")
                        .param("code", "code-1")
                        .param("state", "state-1")
                        .param("redirectUri", "https://app/callback"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("CV_SESSION=s1")))
                .andExpect(jsonPath("$.session.sessionId").value("s1"));
    }

    @Test
    void updateReturnsProfile() throws Exception {
        PersonalInfo info = new PersonalInfo("name2", "email2", "url2");
        UserProfile profile = new UserProfile(new UserId("u1"), "user", info, UserRole.USER, UserStatus.ACTIVE);
        when(userService.updateProfile(eq("s1"), any(PersonalInfoUpdate.class))).thenReturn(profile);

        mockMvc.perform(post("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"s1\",\"personalInfo\":{\"name\":\"name2\",\"email\":\"email2\",\"profileImageUrl\":\"url2\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.personalInfo.email").value("email2"));
    }
}
