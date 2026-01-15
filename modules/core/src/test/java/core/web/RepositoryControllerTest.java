package core.web;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import api.common.RepositoryMeta;
import api.common.RepositoryRegistration;
import api.repository.RepositoryHandle;
import core.repository.RepositoryAvailability;
import core.repository.RepositoryCatalog;
import core.repository.RepositoryService;
import core.web.dto.RepositoryRequest;

@WebMvcTest(RepositoryController.class)
@Import(TestWebConfiguration.class)
@ContextConfiguration(classes = {TestWebConfiguration.class, RepositoryController.class})
class RepositoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryService repositoryService;

    @MockBean
    private RepositoryCatalog repositoryCatalog;

    @Test
    void availabilityReturnsWrappedStatus() throws Exception {
        RepositoryRequest request = new RepositoryRequest("user-1", "repo-1", "TEST", Map.of());
        RepositoryAvailability availability = new RepositoryAvailability(
                RepositoryAvailability.Status.AVAILABLE, null, null);

        when(repositoryService.checkAvailability(request.toDescriptor())).thenReturn(availability);

        mockMvc.perform(post("/repositories/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\",\"type\":\"TEST\",\"config\":{}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void openReturnsRepositoryMeta() throws Exception {
        RepositoryRequest request = new RepositoryRequest("user-1", "repo-1", "TEST", Map.of());
        RepositoryMeta meta = new RepositoryMeta("repo-1", "TEST", "AVAILABLE");

        RepositoryHandle handle = mock(RepositoryHandle.class);
        when(handle.meta()).thenReturn(meta);
        when(repositoryService.openRepository(request.toDescriptor())).thenReturn(handle);

        mockMvc.perform(post("/repositories/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\",\"type\":\"TEST\",\"config\":{}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositoryId").value("repo-1"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void listByUserReturnsRegistrations() throws Exception {
        RepositoryRegistration registration = new RepositoryRegistration(
                "user-1", "repo-1", "TEST", Map.of("region", "us-east-1"));
        when(repositoryCatalog.listByUserId("user-1")).thenReturn(List.of(registration));

        mockMvc.perform(get("/repositories")
                        .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user-1"))
                .andExpect(jsonPath("$[0].repositoryId").value("repo-1"))
                .andExpect(jsonPath("$[0].type").value("TEST"))
                .andExpect(jsonPath("$[0].config.region").value("us-east-1"));
    }

    @Test
    void unregisterReturnsResult() throws Exception {
        when(repositoryCatalog.unregister(new api.common.UserRepositoryRef("user-1", "repo-1"))).thenReturn(true);

        mockMvc.perform(post("/repositories/unregister")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user-1\",\"repositoryId\":\"repo-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.removed").value(true));
    }
}
