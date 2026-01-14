package plugins.onedrive;

import api.common.DownloadStream;

import java.util.List;

public interface OneDriveApiClient {
    void validateAccess();

    OneDriveItem getItem(String itemId);

    List<OneDriveItem> listChildren(String path);

    DownloadStream download(String itemId);
}
