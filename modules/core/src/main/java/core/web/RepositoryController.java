package core.web;

import api.common.RepositoryMeta;
import core.repository.RepositoryAvailability;
import core.repository.RepositoryCatalog;
import core.repository.RepositoryService;
import core.web.dto.RepositoryIdRequest;
import core.web.dto.RepositoryRegistrationResponse;
import core.web.dto.RepositorySummaryResponse;
import core.web.dto.RepositoryUnregisterResponse;
import core.web.dto.RepositoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/repositories")
@Tag(name = "Repository", description = "Repository registration and availability checks.")
public class RepositoryController {
    private final RepositoryService repositoryService;
    private final RepositoryCatalog repositoryCatalog;

    public RepositoryController(RepositoryService repositoryService, RepositoryCatalog repositoryCatalog) {
        this.repositoryService = repositoryService;
        this.repositoryCatalog = repositoryCatalog;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register repository",
            description = "Registers a repository descriptor for a user. This only stores the registration metadata; it does not validate plugin availability or access."
    )
    public RepositoryRegistrationResponse register(@RequestBody RepositoryRequest request) {
        repositoryCatalog.register(request.toRegistration());
        return new RepositoryRegistrationResponse(request.userId(), request.repositoryId(), request.type());
    }

    @GetMapping
    @Operation(
            summary = "List repositories by user",
            description = "Returns repository registrations for the provided userId."
    )
    public List<RepositorySummaryResponse> listByUser(@RequestParam String userId) {
        return repositoryCatalog.listByUserId(userId).stream()
                .map(RepositorySummaryResponse::from)
                .toList();
    }

    @PostMapping("/availability")
    @Operation(
            summary = "Check availability by descriptor",
            description = "Checks plugin availability for the provided repository descriptor. This performs a live plugin availability check."
    )
    public RepositoryAvailability availability(@RequestBody RepositoryRequest request) {
        return repositoryService.checkAvailability(request.toDescriptor());
    }

    @PostMapping("/availability-by-id")
    @Operation(
            summary = "Check availability by registered id",
            description = "Loads the repository descriptor from the registry (userId + repositoryId) and checks plugin availability."
    )
    public RepositoryAvailability availabilityById(@RequestBody RepositoryIdRequest request) {
        return repositoryService.checkAvailability(repositoryCatalog.getOrThrow(request.toUserRef()));
    }

    @PostMapping("/open")
    @Operation(
            summary = "Open repository by descriptor",
            description = "Opens a repository connection using the descriptor and returns repository metadata. Fails when plugin is unavailable."
    )
    public RepositoryMeta open(@RequestBody RepositoryRequest request) {
        return repositoryService.openRepository(request.toDescriptor()).meta();
    }

    @PostMapping("/open-by-id")
    @Operation(
            summary = "Open repository by registered id",
            description = "Loads descriptor from the registry and opens the repository connection to return metadata."
    )
    public RepositoryMeta openById(@RequestBody RepositoryIdRequest request) {
        return repositoryService.openRepository(repositoryCatalog.getOrThrow(request.toUserRef())).meta();
    }

    @PostMapping("/unregister")
    @Operation(
            summary = "Unregister repository",
            description = "Removes repository registration for the given userId + repositoryId."
    )
    public RepositoryUnregisterResponse unregister(@RequestBody RepositoryIdRequest request) {
        boolean removed = repositoryCatalog.unregister(request.toUserRef());
        return new RepositoryUnregisterResponse(request.userId(), request.repositoryId(), removed);
    }
}
