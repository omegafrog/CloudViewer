package core.web;

import api.common.DownloadStream;
import api.common.FileNode;
import core.file.FileService;
import core.web.dto.FileByIdRequest;
import core.web.dto.FileListByIdRequest;
import core.web.dto.FileListRequest;
import core.web.dto.FileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/files")
@Tag(name = "File", description = "Read-only file access for a repository.")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/get")
    @Operation(
            summary = "Get file by descriptor",
            description = "Returns file metadata by repository descriptor and node id. Uses repository connection and file handle."
    )
    public FileNode get(@RequestBody FileRequest request) {
        return fileService.getFile(request.descriptor(), request.toNodeId());
    }

    @PostMapping("/get-by-id")
    @Operation(
            summary = "Get file by registered id",
            description = "Loads descriptor from registry (userId + repositoryId) and returns file metadata by node id."
    )
    public FileNode getById(@RequestBody FileByIdRequest request) {
        return fileService.getFileById(request.toUserRef(), request.toNodeId());
    }

    @PostMapping("/list")
    @Operation(
            summary = "List files by descriptor",
            description = "Lists files under the given path using repository descriptor and pagination cursor."
    )
    public List<FileNode> list(@RequestBody FileListRequest request) {
        return fileService.listFiles(request.descriptor(),
                java.nio.file.Path.of(request.path()),
                request.page());
    }

    @PostMapping("/list-by-id")
    @Operation(
            summary = "List files by registered id",
            description = "Loads descriptor from registry (userId + repositoryId) and lists files under the given path."
    )
    public List<FileNode> listById(@RequestBody FileListByIdRequest request) {
        return fileService.listFilesById(request.toUserRef(),
                java.nio.file.Path.of(request.path()),
                request.page());
    }

    @PostMapping("/download")
    @Operation(
            summary = "Download file by descriptor",
            description = "Streams file content for a node id and repository descriptor. Returns content type and length."
    )
    public ResponseEntity<InputStreamResource> download(@RequestBody FileRequest request) {
        DownloadStream stream = fileService.download(request.descriptor(), request.toNodeId());
        MediaType contentType = MediaType.parseMediaType(stream.contentType());
        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(stream.length())
                .body(new InputStreamResource(stream.stream()));
    }

    @PostMapping("/download-by-id")
    @Operation(
            summary = "Download file by registered id",
            description = "Loads descriptor from registry (userId + repositoryId) and streams file content by node id."
    )
    public ResponseEntity<InputStreamResource> downloadById(@RequestBody FileByIdRequest request) {
        DownloadStream stream = fileService.downloadById(request.toUserRef(), request.toNodeId());
        MediaType contentType = MediaType.parseMediaType(stream.contentType());
        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(stream.length())
                .body(new InputStreamResource(stream.stream()));
    }
}
