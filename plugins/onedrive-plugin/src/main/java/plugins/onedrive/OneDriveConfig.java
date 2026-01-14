package plugins.onedrive;

import api.common.RepositoryDescriptor;

import java.util.Map;
public record OneDriveConfig(String accessToken, String driveId, String rootPath,
                             String clientId, String tenant, String scope, boolean useDeviceCode) {
    public static OneDriveConfig from(RepositoryDescriptor descriptor) {
        Map<String, String> config = descriptor.config();
        String token = config == null ? null : config.get("accessToken");
        String driveId = config == null ? null : config.get("driveId");
        String rootPath = config == null ? null : config.get("rootPath");
        String clientId = config == null ? null : config.get("clientId");
        String tenant = config == null ? null : config.get("tenant");
        String scope = config == null ? null : config.get("scope");
        boolean useDeviceCode = config != null && Boolean.parseBoolean(config.getOrDefault("useDeviceCode", "false"));
        if ((token == null || token.isBlank()) && !useDeviceCode) {
            throw new IllegalArgumentException("accessToken is required unless useDeviceCode=true");
        }
        return new OneDriveConfig(token, driveId, rootPath, clientId, tenant, scope, useDeviceCode);
    }
}
