package core.web;

import api.common.DownloadStream;
import api.common.FileNode;
import core.file.FileService;
import core.web.dto.FileByIdRequest;
import core.web.dto.FileListByIdRequest;
import core.web.dto.FileListRequest;
import core.web.dto.FileRequest;
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
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/get")
    public FileNode get(@RequestBody FileRequest request) {
        return fileService.getFile(request.descriptor(), request.toNodeId());
    }

    @PostMapping("/get-by-id")
    public FileNode getById(@RequestBody FileByIdRequest request) {
        return fileService.getFileById(request.repositoryId(), request.toNodeId());
    }

    @PostMapping("/list")
    public List<FileNode> list(@RequestBody FileListRequest request) {
        return fileService.listFiles(request.descriptor(),
                java.nio.file.Path.of(request.path()),
                request.page());
    }

    @PostMapping("/list-by-id")
    public List<FileNode> listById(@RequestBody FileListByIdRequest request) {
        return fileService.listFilesById(request.repositoryId(),
                java.nio.file.Path.of(request.path()),
                request.page());
    }

    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestBody FileRequest request) {
        DownloadStream stream = fileService.download(request.descriptor(), request.toNodeId());
        MediaType contentType = MediaType.parseMediaType(stream.contentType());
        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(stream.length())
                .body(new InputStreamResource(stream.stream()));
    }

    @PostMapping("/download-by-id")
    public ResponseEntity<InputStreamResource> downloadById(@RequestBody FileByIdRequest request) {
        DownloadStream stream = fileService.downloadById(request.repositoryId(), request.toNodeId());
        MediaType contentType = MediaType.parseMediaType(stream.contentType());
        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(stream.length())
                .body(new InputStreamResource(stream.stream()));
    }
}
