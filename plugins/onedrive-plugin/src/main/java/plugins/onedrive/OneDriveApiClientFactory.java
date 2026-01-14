package plugins.onedrive;

public interface OneDriveApiClientFactory {
    OneDriveApiClient create(String accessToken, String driveId);

    class Default implements OneDriveApiClientFactory {
        @Override
        public OneDriveApiClient create(String accessToken, String driveId) {
            return new OneDriveHttpApiClient(accessToken, driveId);
        }
    }
}
