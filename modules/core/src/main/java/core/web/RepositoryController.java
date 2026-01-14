package core.web;

import api.common.RepositoryMeta;
import core.repository.RepositoryAvailability;
import core.repository.RepositoryCatalog;
import core.repository.RepositoryService;
import core.web.dto.RepositoryIdRequest;
import core.web.dto.RepositoryRegistrationResponse;
import core.web.dto.RepositoryRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
public class RepositoryController {
    private final RepositoryService repositoryService;
    private final RepositoryCatalog repositoryCatalog;

    public RepositoryController(RepositoryService repositoryService, RepositoryCatalog repositoryCatalog) {
        this.repositoryService = repositoryService;
        this.repositoryCatalog = repositoryCatalog;
    }

    @PostMapping("/register")
    public RepositoryRegistrationResponse register(@RequestBody RepositoryRequest request) {
        repositoryCatalog.register(request.toRegistration());
        return new RepositoryRegistrationResponse(request.userId(), request.repositoryId(), request.type());
    }

    @PostMapping("/availability")
    public RepositoryAvailability availability(@RequestBody RepositoryRequest request) {
        return repositoryService.checkAvailability(request.toDescriptor());
    }

    @PostMapping("/availability-by-id")
    public RepositoryAvailability availabilityById(@RequestBody RepositoryIdRequest request) {
        return repositoryService.checkAvailability(repositoryCatalog.getOrThrow(request.toUserRef()));
    }

    @PostMapping("/open")
    public RepositoryMeta open(@RequestBody RepositoryRequest request) {
        return repositoryService.openRepository(request.toDescriptor()).meta();
    }

    @PostMapping("/open-by-id")
    public RepositoryMeta openById(@RequestBody RepositoryIdRequest request) {
        return repositoryService.openRepository(repositoryCatalog.getOrThrow(request.toUserRef())).meta();
    }
}
