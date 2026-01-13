package core.web;

import api.common.RepositoryDescriptor;
import core.indexing.IndexNode;
import core.indexing.IndexSnapshot;
import core.indexing.IndexStatus;
import core.indexing.IndexingService;
import core.web.dto.IndexRequest;
import core.web.dto.RepositoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndexingController.class)
class IndexingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IndexingService indexingService;

    @Test
    void scheduleReturnsStatus() throws Exception {
        RepositoryRequest repo = new RepositoryRequest("repo-1", "TEST", Map.of());
        IndexRequest request = new IndexRequest(repo);

        when(indexingService.status(request.descriptor())).thenReturn(IndexStatus.SCHEDULED);

        mockMvc.perform(post("/indexing/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repository\":{\"repositoryId\":\"repo-1\",\"type\":\"TEST\",\"config\":{}}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("SCHEDULED"));
    }

    @Test
    void snapshotReturnsNotFoundWhenMissing() throws Exception {
        RepositoryRequest repo = new RepositoryRequest("repo-1", "TEST", Map.of());
        IndexRequest request = new IndexRequest(repo);

        when(indexingService.latestSnapshot(request.descriptor())).thenReturn(Optional.empty());

        mockMvc.perform(post("/indexing/snapshot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repository\":{\"repositoryId\":\"repo-1\",\"type\":\"TEST\",\"config\":{}}}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void snapshotReturnsPayload() throws Exception {
        RepositoryRequest repo = new RepositoryRequest("repo-1", "TEST", Map.of());
        RepositoryDescriptor descriptor = repo.toDescriptor();
        IndexSnapshot snapshot = new IndexSnapshot(descriptor,
                new IndexNode("/root", "root", false, java.util.List.of()), 1L);

        when(indexingService.latestSnapshot(descriptor)).thenReturn(Optional.of(snapshot));

        mockMvc.perform(post("/indexing/snapshot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repository\":{\"repositoryId\":\"repo-1\",\"type\":\"TEST\",\"config\":{}}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.root.path").value("/root"));
    }
}
