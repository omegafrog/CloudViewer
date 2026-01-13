package core.web;

import api.common.DownloadStream;
import api.common.FileNode;
import core.file.FileService;
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

    @PostMapping("/list")
    public List<FileNode> list(@RequestBody FileListRequest request) {
        return fileService.listFiles(request.descriptor(),
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
}
