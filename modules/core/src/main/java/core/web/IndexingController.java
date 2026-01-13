package core.web;

import core.indexing.IndexSnapshot;
import core.indexing.IndexStatus;
import core.indexing.IndexingService;
import core.web.dto.IndexRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indexing")
public class IndexingController {
    private final IndexingService indexingService;

    public IndexingController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @PostMapping("/schedule")
    public IndexStatus schedule(@RequestBody IndexRequest request) {
        indexingService.scheduleSync(request.descriptor());
        return indexingService.status(request.descriptor());
    }

    @PostMapping("/defer")
    public IndexStatus defer(@RequestBody IndexRequest request) {
        indexingService.deferSync(request.descriptor());
        return indexingService.status(request.descriptor());
    }

    @PostMapping("/status")
    public IndexStatus status(@RequestBody IndexRequest request) {
        return indexingService.status(request.descriptor());
    }

    @PostMapping("/snapshot")
    public ResponseEntity<IndexSnapshot> snapshot(@RequestBody IndexRequest request) {
        return indexingService.latestSnapshot(request.descriptor())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
