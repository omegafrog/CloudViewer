package plugins.onedrive;

import api.common.RepositoryDescriptor;
import api.repository.RepositoryConnector;
import api.repository.RepositoryHandle;

public class OneDriveRepositoryConnector implements RepositoryConnector {
    private final OneDriveApiClientFactory clientFactory;
    private final OneDriveTokenProvider tokenProvider;

    public OneDriveRepositoryConnector(OneDriveApiClientFactory clientFactory, OneDriveTokenProvider tokenProvider) {
        this.clientFactory = clientFactory;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public RepositoryHandle open(RepositoryDescriptor repo) {
        OneDriveConfig config = OneDriveConfig.from(repo);
        String token = tokenProvider.resolveAccessToken(repo, config);
        OneDriveApiClient client = clientFactory.create(token, config.driveId());
        return new OneDriveRepositoryHandle(repo, client, config.rootPath());
    }
}
