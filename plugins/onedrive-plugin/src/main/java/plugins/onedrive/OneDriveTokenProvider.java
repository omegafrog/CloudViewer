package plugins.onedrive;

import api.common.RepositoryDescriptor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class OneDriveTokenProvider {
    private static final String DEFAULT_CLIENT_ID = "000000004C12AE6F";
    private static final String DEFAULT_TENANT = "consumers";
    private static final String DEFAULT_SCOPE = "offline_access User.Read Files.Read";

    private final OneDriveAuthClient authClient;
    private final Map<String, String> tokensByRepoId = new ConcurrentHashMap<>();

    public OneDriveTokenProvider(OneDriveAuthClient authClient) {
        this.authClient = authClient;
    }

    public String resolveAccessToken(RepositoryDescriptor descriptor, OneDriveConfig config) {
        String token = config.accessToken();
        if (token != null && !token.isBlank()) {
            return token;
        }
        String cached = tokensByRepoId.get(descriptor.repositoryId());
        if (cached != null && !cached.isBlank()) {
            return cached;
        }
        if (!config.useDeviceCode()) {
            throw new IllegalStateException("accessToken is required or useDeviceCode must be true");
        }
        String clientId = config.clientId() == null || config.clientId().isBlank()
                ? DEFAULT_CLIENT_ID
                : config.clientId();
        String tenant = config.tenant() == null || config.tenant().isBlank()
                ? DEFAULT_TENANT
                : config.tenant();
        String scope = config.scope() == null || config.scope().isBlank()
                ? DEFAULT_SCOPE
                : config.scope();

        DeviceCodeResponse deviceCode = authClient.requestDeviceCode(clientId, tenant, scope);
        if (deviceCode.message() != null && !deviceCode.message().isBlank()) {
            System.out.println(deviceCode.message());
        } else {
            System.out.println("Go to " + deviceCode.verificationUri() + " and enter code: " + deviceCode.userCode());
        }
        TokenResponse tokenResponse = authClient.pollToken(clientId, tenant, deviceCode.deviceCode(),
                deviceCode.intervalSeconds(), deviceCode.expiresInSeconds());
        Objects.requireNonNull(tokenResponse.accessToken(), "accessToken");
        tokensByRepoId.put(descriptor.repositoryId(), tokenResponse.accessToken());
        return tokenResponse.accessToken();
    }
}
