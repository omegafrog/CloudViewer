package plugins.onedrive;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.common.RepositoryDescriptor;
import api.plugin.PluginAvailability;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OneDrivePluginTest {
    private final OneDriveApiClientFactory clientFactory = new FakeClientFactory();
    private final OneDriveTokenProvider tokenProvider = new OneDriveTokenProvider(new FakeAuthClient());

    @Test
    void availabilityAvailableWhenRootExists() {
        OneDrivePlugin plugin = new OneDrivePlugin(clientFactory, tokenProvider);
        RepositoryDescriptor descriptor = new RepositoryDescriptor("repo-1", "ONEDRIVE",
                Map.of("accessToken", "token"));

        PluginAvailability availability = plugin.availability(descriptor);

        assertEquals(PluginAvailability.Status.AVAILABLE, availability.getStatus());
    }

    @Test
    void availabilityUnavailableWhenRootMissing() {
        OneDrivePlugin plugin = new OneDrivePlugin(clientFactory, tokenProvider);
        RepositoryDescriptor descriptor = new RepositoryDescriptor("repo-1", "ONEDRIVE",
                Map.of("accessToken", ""));

        PluginAvailability availability = plugin.availability(descriptor);

        assertEquals(PluginAvailability.Status.UNAVAILABLE, availability.getStatus());
        assertEquals("CONFIG_INVALID", availability.getReasonCode());
    }

    @Test
    void fileHandleReadsLocalFiles() {
        RepositoryDescriptor descriptor = new RepositoryDescriptor("repo-1", "ONEDRIVE",
                Map.of("accessToken", "token", "rootPath", "root"));

        OneDriveRepositoryHandle handle = (OneDriveRepositoryHandle)
                new OneDriveRepositoryConnector(clientFactory, tokenProvider).open(descriptor);
        OneDriveFileHandle fileHandle = (OneDriveFileHandle) handle.fileHandle();

        FileNode node = fileHandle.get(new NodeId("item-1"));
        assertEquals("report.txt", node.name());
        assertTrue(node.isFile());

        List<FileNode> nodes = fileHandle.list(Path.of("."), new PageRequest(null, 10));
        assertTrue(nodes.size() >= 2);

        DownloadStream stream = fileHandle.download(new NodeId("item-1"));
        assertEquals(5, stream.length());
    }

    @Test
    void fileHandleBuildsIndexSnapshot() {
        RepositoryDescriptor descriptor = new RepositoryDescriptor("repo-1", "ONEDRIVE",
                Map.of("accessToken", "token", "rootPath", "root"));

        OneDriveRepositoryHandle handle = (OneDriveRepositoryHandle)
                new OneDriveRepositoryConnector(clientFactory, tokenProvider).open(descriptor);
        OneDriveFileHandle fileHandle = (OneDriveFileHandle) handle.fileHandle();

        api.common.IndexNode root = fileHandle.index(Path.of("/"), 1);
        assertEquals("root", root.name());
        assertTrue(root.children().size() >= 2);
    }

    private static class FakeClientFactory implements OneDriveApiClientFactory {
        @Override
        public OneDriveApiClient create(String accessToken, String driveId) {
            return new FakeOneDriveClient();
        }
    }

    private static class FakeAuthClient implements OneDriveAuthClient {
        @Override
        public DeviceCodeResponse requestDeviceCode(String clientId, String tenant, String scope) {
            return new DeviceCodeResponse("device", "user", "https://example.com", 1, 60, null);
        }

        @Override
        public TokenResponse pollToken(String clientId, String tenant, String deviceCode, int intervalSeconds,
                                       int expiresInSeconds) {
            return new TokenResponse("token", null, 3600);
        }
    }

    private static class FakeOneDriveClient implements OneDriveApiClient {
        @Override
        public void validateAccess() {
        }

        @Override
        public OneDriveItem getItem(String itemId) {
            return new OneDriveItem(itemId, "report.txt", "root/report.txt", true, Map.of());
        }

        @Override
        public List<OneDriveItem> listChildren(String path) {
            return List.of(
                    new OneDriveItem("item-1", "report.txt", "root/report.txt", true, Map.of()),
                    new OneDriveItem("item-2", "docs", "root/docs", false, Map.of())
            );
        }

        @Override
        public DownloadStream download(String itemId) {
            return new DownloadStream(new ByteArrayInputStream("hello".getBytes()),
                    "text/plain", 5);
        }
    }
}
