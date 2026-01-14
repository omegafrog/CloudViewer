package core.web;

import core.plugin.PluginManager;
import core.plugin.PluginRecord;
import core.web.dto.PluginSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/plugins")
@Tag(name = "Plugin", description = "Plugin management (upload, enable/disable, remove).")
public class PluginController {
    private final PluginManager pluginManager;

    public PluginController(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @GetMapping
    @Operation(
            summary = "List plugins",
            description = "Returns the list of plugins known to the system with status and jar metadata."
    )
    public List<PluginSummaryResponse> list() {
        return pluginManager.list().stream()
                .map(PluginSummaryResponse::fromRecord)
                .toList();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload plugin jar",
            description = "Uploads a plugin jar, loads it, registers it, and returns the plugin summary."
    )
    public PluginSummaryResponse upload(@RequestPart("file") MultipartFile file) {
        PluginRecord record = pluginManager.upload(file);
        return PluginSummaryResponse.fromRecord(record);
    }

    @PostMapping("/{pluginId}/enable")
    @Operation(
            summary = "Enable plugin",
            description = "Marks the plugin as enabled and registers it if not loaded."
    )
    public PluginSummaryResponse enable(@PathVariable String pluginId) {
        PluginRecord record = pluginManager.enable(pluginId);
        return PluginSummaryResponse.fromRecord(record);
    }

    @PostMapping("/{pluginId}/disable")
    @Operation(
            summary = "Disable plugin",
            description = "Marks the plugin as disabled. Disabled plugins cannot be used for repository operations."
    )
    public PluginSummaryResponse disable(@PathVariable String pluginId) {
        PluginRecord record = pluginManager.disable(pluginId);
        return PluginSummaryResponse.fromRecord(record);
    }

    @DeleteMapping("/{pluginId}")
    @Operation(
            summary = "Remove plugin",
            description = "Deletes the plugin jar and disables it. Already loaded plugins remain in memory but are blocked."
    )
    public ResponseEntity<Void> remove(@PathVariable String pluginId) {
        pluginManager.remove(pluginId);
        return ResponseEntity.noContent().build();
    }
}
