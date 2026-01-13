package core.web;

import api.common.RepositoryMeta;
import core.repository.RepositoryAvailability;
import core.repository.RepositoryService;
import core.web.dto.RepositoryRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
public class RepositoryController {
    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostMapping("/availability")
    public RepositoryAvailability availability(@RequestBody RepositoryRequest request) {
        return repositoryService.checkAvailability(request.toDescriptor());
    }

    @PostMapping("/open")
    public RepositoryMeta open(@RequestBody RepositoryRequest request) {
        return repositoryService.openRepository(request.toDescriptor()).meta();
    }
}
