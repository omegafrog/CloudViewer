package plugins.onedrive;

import api.common.DownloadStream;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OneDriveHttpApiClient implements OneDriveApiClient {
    private static final String GRAPH_BASE = "https://graph.microsoft.com/v1.0";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String accessToken;
    private final String driveId;

    public OneDriveHttpApiClient(String accessToken, String driveId) {
        this.accessToken = accessToken;
        this.driveId = driveId;
    }

    @Override
    public void validateAccess() {
        String url = driveId == null || driveId.isBlank()
                ? GRAPH_BASE + "/me/drive"
                : GRAPH_BASE + "/drives/" + encodePathSegment(driveId);
        JsonNode node = getJson(url);
        if (node.get("id") == null) {
            throw new IllegalStateException("Drive access check failed");
        }
    }

    @Override
    public OneDriveItem getItem(String itemId) {
        String base = driveBase();
        JsonNode node = getJson(base + "/items/" + encodePathSegment(itemId));
        return toItem(node);
    }

    @Override
    public List<OneDriveItem> listChildren(String path) {
        String base = driveBase();
        String url;
        if (path == null || path.isBlank() || ".".equals(path) || "/".equals(path)) {
            url = base + "/root/children";
        } else {
            String encodedPath = encodePath(path);
            url = base + "/root:/" + encodedPath + ":/children";
        }
        JsonNode node = getJson(url);
        List<OneDriveItem> items = new ArrayList<>();
        JsonNode values = node.get("value");
        if (values != null && values.isArray()) {
            for (JsonNode item : values) {
                items.add(toItem(item));
            }
        }
        return items;
    }

    @Override
    public DownloadStream download(String itemId) {
        try {
            URI current = URI.create(driveBase() + "/items/" + encodePathSegment(itemId) + "/content");
            boolean includeAuth = true;
            for (int redirects = 0; redirects < 3; redirects++) {
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(current)
                        .GET();
                if (includeAuth) {
                    builder.header("Authorization", "Bearer " + accessToken);
                }
                HttpResponse<InputStream> response = httpClient.send(builder.build(),
                        HttpResponse.BodyHandlers.ofInputStream());
                int status = response.statusCode();
                if (status / 100 == 2) {
                    String contentType = response.headers().firstValue("Content-Type")
                            .orElse("application/octet-stream");
                    long length = response.headers().firstValue("Content-Length")
                            .map(Long::parseLong).orElse(0L);
                    return new DownloadStream(response.body(), contentType, length);
                }
                if (status / 100 == 3) {
                    String location = response.headers().firstValue("Location")
                            .orElseThrow(() -> new IllegalStateException("Download redirect missing Location header"));
                    current = URI.create(location);
                    includeAuth = false;
                    continue;
                }
                throw new IllegalStateException("Download failed with status " + status);
            }
            throw new IllegalStateException("Download failed due to too many redirects");
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Download failed", ex);
        }
    }

    private JsonNode getJson(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException("Request failed with status " + response.statusCode());
            }
            return objectMapper.readTree(response.body());
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Request failed", ex);
        }
    }

    private OneDriveItem toItem(JsonNode node) {
        String id = node.path("id").asText();
        String name = node.path("name").asText();
        String parentPath = node.path("parentReference").path("path").asText();
        String cleanedParent = cleanupParentPath(parentPath);
        String fullPath = cleanedParent.isEmpty() ? name : cleanedParent + "/" + name;
        boolean isFile = node.has("file");
        Map<String, String> metadata = Map.of(
                "size", node.path("size").asText("0")
        );
        return new OneDriveItem(id, name, fullPath, isFile, metadata);
    }

    private String driveBase() {
        if (driveId == null || driveId.isBlank()) {
            return GRAPH_BASE + "/me/drive";
        }
        return GRAPH_BASE + "/drives/" + encodePathSegment(driveId);
    }

    private String cleanupParentPath(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String prefix = "/drive/root:";
        if (raw.startsWith(prefix)) {
            String trimmed = raw.substring(prefix.length());
            if (trimmed.startsWith("/")) {
                return trimmed.substring(1);
            }
            return trimmed;
        }
        return raw;
    }

    private String encodePath(String path) {
        String[] parts = path.split("/");
        List<String> encoded = new ArrayList<>();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            encoded.add(encodePathSegment(part));
        }
        return String.join("/", encoded);
    }

    private String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
