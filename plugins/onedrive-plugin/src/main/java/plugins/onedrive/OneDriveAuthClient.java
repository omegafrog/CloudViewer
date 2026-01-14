package plugins.onedrive;

public interface OneDriveAuthClient {
    DeviceCodeResponse requestDeviceCode(String clientId, String tenant, String scope);

    TokenResponse pollToken(String clientId, String tenant, String deviceCode, int intervalSeconds, int expiresInSeconds);
}
