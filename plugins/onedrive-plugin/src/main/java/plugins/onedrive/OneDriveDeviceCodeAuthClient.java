package plugins.onedrive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class OneDriveDeviceCodeAuthClient implements OneDriveAuthClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DeviceCodeResponse requestDeviceCode(String clientId, String tenant, String scope) {
        String body = "client_id=" + encode(clientId) + "&scope=" + encode(scope);
        String url = "https://login.microsoftonline.com/" + encode(tenant) + "/oauth2/v2.0/devicecode";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException("Device code request failed with status "
                        + response.statusCode() + ": " + response.body());
            }
            JsonNode node = objectMapper.readTree(response.body());
            return new DeviceCodeResponse(
                    node.path("device_code").asText(),
                    node.path("user_code").asText(),
                    node.path("verification_uri").asText(),
                    node.path("interval").asInt(5),
                    node.path("expires_in").asInt(900),
                    node.path("message").asText(null)
            );
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Device code request failed", ex);
        }
    }

    @Override
    public TokenResponse pollToken(String clientId, String tenant, String deviceCode,
                                   int intervalSeconds, int expiresInSeconds) {
        long deadline = System.currentTimeMillis() + (expiresInSeconds * 1000L);
        int interval = intervalSeconds;
        while (System.currentTimeMillis() < deadline) {
            TokenResponse token = tryToken(clientId, tenant, deviceCode);
            if (token != null) {
                return token;
            }
            sleepSeconds(interval);
        }
        throw new IllegalStateException("Device code expired before authorization");
    }

    private TokenResponse tryToken(String clientId, String tenant, String deviceCode) {
        String body = "client_id=" + encode(clientId)
                + "&grant_type=" + encode("urn:ietf:params:oauth:grant-type:device_code")
                + "&device_code=" + encode(deviceCode);
        String url = "https://login.microsoftonline.com/" + encode(tenant) + "/oauth2/v2.0/token";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(response.body());
            if (response.statusCode() / 100 == 2 && node.has("access_token")) {
                return new TokenResponse(
                        node.path("access_token").asText(),
                        node.path("refresh_token").asText(null),
                        node.path("expires_in").asInt(3600)
                );
            }
            String error = node.path("error").asText();
            if ("authorization_pending".equals(error)) {
                return null;
            }
            if ("slow_down".equals(error)) {
                sleepSeconds(5);
                return null;
            }
            if (!error.isBlank()) {
                throw new IllegalStateException("Token request failed: " + error);
            }
            return null;
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Token request failed", ex);
        }
    }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Device code polling interrupted", ex);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
