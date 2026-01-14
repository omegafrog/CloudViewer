package user.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Component
public class GoogleOAuthProvider implements OAuthProvider {
    private static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GoogleOAuthProperties properties;

    public GoogleOAuthProvider(GoogleOAuthProperties properties) {
        this.properties = Objects.requireNonNull(properties, "properties");
    }

    @Override
    public String providerId() {
        return "google";
    }

    @Override
    public String defaultScope() {
        return "openid email profile";
    }

    @Override
    public String buildAuthorizationUrl(OAuthAuthorizationRequest request) {
        Objects.requireNonNull(request, "request");
        String clientId = requireProperty(properties.clientId(), "oauth.google.client-id");
        return AUTH_ENDPOINT + "?client_id=" + encode(clientId)
                + "&redirect_uri=" + encode(request.redirectUri())
                + "&response_type=code"
                + "&scope=" + encode(request.scope())
                + "&state=" + encode(request.state())
                + "&access_type=offline";
    }

    @Override
    public OAuthUserInfo exchange(OAuthCallbackRequest request) {
        Objects.requireNonNull(request, "request");
        String clientId = requireProperty(properties.clientId(), "oauth.google.client-id");
        String clientSecret = requireProperty(properties.clientSecret(), "oauth.google.client-secret");
        String body = "code=" + encode(request.code())
                + "&client_id=" + encode(clientId)
                + "&client_secret=" + encode(clientSecret)
                + "&redirect_uri=" + encode(request.redirectUri())
                + "&grant_type=authorization_code";
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_ENDPOINT))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        String tokenResponse = send(httpRequest);
        Map<?, ?> tokenMap = readJson(tokenResponse);
        Object accessToken = tokenMap.get("access_token");
        if (accessToken == null) {
            throw new IllegalStateException("OAuth token response missing access_token");
        }

        HttpRequest userInfoRequest = HttpRequest.newBuilder()
                .uri(URI.create(USERINFO_ENDPOINT))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        String userInfoResponse = send(userInfoRequest);
        Map<?, ?> userInfo = readJson(userInfoResponse);
        return new OAuthUserInfo(
                requireField(userInfo, "sub"),
                requireField(userInfo, "email"),
                requireField(userInfo, "name"),
                requireField(userInfo, "picture")
        );
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("OAuth HTTP request failed: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("OAuth HTTP request failed", ex);
        }
    }

    private Map<?, ?> readJson(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (IOException ex) {
            throw new IllegalStateException("OAuth JSON parsing failed", ex);
        }
    }

    private String requireField(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalStateException("OAuth response missing field: " + key);
        }
        return value.toString();
    }

    private String requireProperty(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Precondition violation: missing config " + name);
        }
        return value;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
