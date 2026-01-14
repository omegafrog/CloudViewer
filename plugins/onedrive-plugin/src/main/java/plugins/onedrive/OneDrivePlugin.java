package plugins.onedrive;

import api.common.RepositoryDescriptor;
import api.plugin.PluginAvailability;
import api.plugin.RepositoryPlugin;
import api.repository.RepositoryConnector;

public class OneDrivePlugin implements RepositoryPlugin {
    private final OneDriveApiClientFactory clientFactory;
    private final OneDriveTokenProvider tokenProvider;

    public OneDrivePlugin() {
        this(new OneDriveApiClientFactory.Default(),
                new OneDriveTokenProvider(new OneDriveDeviceCodeAuthClient()));
    }

    OneDrivePlugin(OneDriveApiClientFactory clientFactory, OneDriveTokenProvider tokenProvider) {
        this.clientFactory = clientFactory;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String pluginId() {
        return "onedrive-plugin";
    }

    @Override
    public String repositoryType() {
        return "ONEDRIVE";
    }

    @Override
    public PluginAvailability availability(RepositoryDescriptor repo) {
        OneDriveConfig config;
        try {
            config = OneDriveConfig.from(repo);
        } catch (IllegalArgumentException ex) {
            return new PluginAvailability(PluginAvailability.Status.UNAVAILABLE, "CONFIG_INVALID",
                    ex.getMessage());
        }

        try {
            String token = tokenProvider.resolveAccessToken(repo, config);
            OneDriveApiClient client = clientFactory.create(token, config.driveId());
            client.validateAccess();
            return new PluginAvailability(PluginAvailability.Status.AVAILABLE, null, null);
        } catch (IllegalStateException ex) {
            return new PluginAvailability(PluginAvailability.Status.UNAVAILABLE, "ACCESS_DENIED",
                    ex.getMessage());
        }
    }

    @Override
    public RepositoryConnector connector(RepositoryDescriptor repo) {
        return new OneDriveRepositoryConnector(clientFactory, tokenProvider);
    }
}
