package core.web;

import api.common.DownloadStream;
import api.common.FileNode;
import api.common.NodeId;
import api.common.PageRequest;
import api.common.UserRepositoryRef;
import core.file.FileService;
import core.web.dto.FileListRequest;
import core.web.dto.FileRequest;
import core.web.dto.RepositoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
@Import(TestWebConfiguration.class)
@ContextConfiguration(classes = {TestWebConfiguration.class, FileController.class})
class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void getReturnsFileNode() throws Exception {
        RepositoryRequest repo = new RepositoryRequest("user-1", "repo-1", "TEST", Map.of());
        FileRequest request = new FileRequest(repo, "n1");
        FileNode node = new FileNode(new NodeId("n1"), "/", "n1", true, Map.of());

        when(fileService.getFile(request.descriptor(), request.toNodeId())).thenReturn(node);

        mockMvc.perform(post("/files/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repository\":{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\",\"type\":\"TEST\",\"config\":{}},\"nodeId\":\"n1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("n1"));
    }

    @Test
    void listReturnsNodes() throws Exception {
        RepositoryRequest repo = new RepositoryRequest("user-1", "repo-1", "TEST", Map.of());
        FileListRequest request = new FileListRequest(repo, "/", new PageRequest(null, 10));
        List<FileNode> nodes = List.of();

        when(fileService.listFiles(request.descriptor(), Path.of(request.path()), request.page()))
                .thenReturn(nodes);

        mockMvc.perform(post("/files/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repository\":{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\",\"type\":\"TEST\",\"config\":{}},\"path\":\"/\",\"page\":{\"cursor\":null,\"limit\":10}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void downloadReturnsStreamMetadata() throws Exception {
        RepositoryRequest repo = new RepositoryRequest("user-1", "repo-1", "TEST", Map.of());
        FileRequest request = new FileRequest(repo, "n1");
        DownloadStream stream = new DownloadStream(new ByteArrayInputStream(new byte[0]),
                "application/octet-stream", 0);

        when(fileService.download(request.descriptor(), request.toNodeId())).thenReturn(stream);

        mockMvc.perform(post("/files/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repository\":{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\",\"type\":\"TEST\",\"config\":{}},\"nodeId\":\"n1\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/octet-stream"))
                .andExpect(header().string("Content-Length", "0"));
    }

    @Test
    void createByIdReturnsCreatedNode() throws Exception {
        UserRepositoryRef ref = new UserRepositoryRef("user-1", "repo-1");
        FileNode node = new FileNode(new NodeId("n-created"), "/new.txt", "new.txt", false, Map.of());

        when(fileService.createFileById(eq(ref), eq(Path.of("/new.txt")), eq(false)))
                .thenReturn(node);

        mockMvc.perform(post("/files/create-by-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\",\"path\":\"/new.txt\",\"directory\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.value").value("n-created"))
                .andExpect(jsonPath("$.name").value("new.txt"));
    }

    @Test
    void deleteByIdReturnsDeleteResponse() throws Exception {
        UserRepositoryRef ref = new UserRepositoryRef("user-1", "repo-1");

        when(fileService.deleteFileById(eq(ref), eq(new NodeId("n1"))))
                .thenReturn(true);

        mockMvc.perform(post("/files/delete-by-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\",\"nodeId\":\"n1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    void moveByIdReturnsUpdatedNode() throws Exception {
        UserRepositoryRef ref = new UserRepositoryRef("user-1", "repo-1");
        FileNode node = new FileNode(new NodeId("n1"), "/moved.txt", "moved.txt", false, Map.of());

        when(fileService.moveFileById(eq(ref), eq(new NodeId("n1")), eq(Path.of("/moved.txt"))))
                .thenReturn(node);

        mockMvc.perform(post("/files/move-by-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\",\"nodeId\":\"n1\",\"targetPath\":\"/moved.txt\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("/moved.txt"))
                .andExpect(jsonPath("$.name").value("moved.txt"));
    }
}
