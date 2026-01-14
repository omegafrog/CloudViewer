package core.web;

import core.plugin.PluginManager;
import core.plugin.PluginRecord;
import core.plugin.PluginStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PluginController.class)
@Import(TestWebConfiguration.class)
@ContextConfiguration(classes = {TestWebConfiguration.class, PluginController.class})
class PluginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PluginManager pluginManager;

    @Test
    void listReturnsPlugins() throws Exception {
        when(pluginManager.list()).thenReturn(List.of(
                new PluginRecord("plugin-1", "TEST", "plugin-1.jar", PluginStatus.ENABLED)
        ));

        mockMvc.perform(get("/plugins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pluginId").value("plugin-1"));
    }

    @Test
    void uploadReturnsPluginSummary() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "plugin.jar", "application/java-archive", new byte[]{0x1});
        when(pluginManager.upload(org.mockito.ArgumentMatchers.any())).thenReturn(
                new PluginRecord("plugin-1", "TEST", "plugin-1.jar", PluginStatus.ENABLED)
        );

        mockMvc.perform(multipart("/plugins").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pluginId").value("plugin-1"));
    }

    @Test
    void enableReturnsPluginSummary() throws Exception {
        when(pluginManager.enable("plugin-1")).thenReturn(
                new PluginRecord("plugin-1", "TEST", "plugin-1.jar", PluginStatus.ENABLED)
        );

        mockMvc.perform(post("/plugins/plugin-1/enable").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENABLED"));
    }

    @Test
    void disableReturnsPluginSummary() throws Exception {
        when(pluginManager.disable("plugin-1")).thenReturn(
                new PluginRecord("plugin-1", "TEST", "plugin-1.jar", PluginStatus.DISABLED)
        );

        mockMvc.perform(post("/plugins/plugin-1/disable").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISABLED"));
    }

    @Test
    void removeReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/plugins/plugin-1"))
                .andExpect(status().isNoContent());
    }
}
