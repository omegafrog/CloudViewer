package core.plugin;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

class StubMultipartFile implements MultipartFile {
    private final String originalFilename;
    private final Path source;

    StubMultipartFile(String originalFilename, Path source) {
        this.originalFilename = originalFilename;
        this.source = source;
    }

    @Override
    public String getName() {
        return originalFilename;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return "application/java-archive";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        try {
            return Files.size(source);
        } catch (IOException ex) {
            return 0;
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        return Files.readAllBytes(source);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(source);
    }

    @Override
    public void transferTo(Path dest) throws IOException {
        Files.copy(source, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException {
        transferTo(dest.toPath());
    }
}
