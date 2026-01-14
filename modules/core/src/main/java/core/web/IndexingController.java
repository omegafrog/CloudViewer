package core.web;

import core.indexing.IndexSnapshot;
import core.indexing.IndexStatus;
import core.indexing.IndexingService;
import core.web.dto.IndexRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indexing")
@Tag(name = "Indexing", description = "Index scheduling and snapshot access.")
public class IndexingController {
    private final IndexingService indexingService;

    public IndexingController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @PostMapping("/schedule")
    @Operation(
            summary = "Schedule indexing",
            description = "Schedules indexing for the repository descriptor. Updates index status to scheduled/Indexed/Deferred."
    )
    public IndexStatus schedule(@RequestBody IndexRequest request) {
        indexingService.scheduleSync(request.descriptor());
        return indexingService.status(request.descriptor());
    }

    @PostMapping("/defer")
    @Operation(
            summary = "Defer indexing",
            description = "Explicitly defers indexing for the repository descriptor."
    )
    public IndexStatus defer(@RequestBody IndexRequest request) {
        indexingService.deferSync(request.descriptor());
        return indexingService.status(request.descriptor());
    }

    @PostMapping("/status")
    @Operation(
            summary = "Get indexing status",
            description = "Returns current indexing status for the repository descriptor."
    )
    public IndexStatus status(@RequestBody IndexRequest request) {
        return indexingService.status(request.descriptor());
    }

    @PostMapping("/snapshot")
    @Operation(
            summary = "Get latest snapshot",
            description = "Returns the latest index snapshot for the repository descriptor or 404 when missing."
    )
    public ResponseEntity<IndexSnapshot> snapshot(@RequestBody IndexRequest request) {
        return indexingService.latestSnapshot(request.descriptor())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
