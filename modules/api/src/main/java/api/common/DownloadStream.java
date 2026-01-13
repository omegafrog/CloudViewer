package api.common;

import java.io.InputStream;
import java.util.Objects;

public record DownloadStream(InputStream stream, String contentType, long length) {
    public DownloadStream {
        Objects.requireNonNull(stream, "stream");
        Objects.requireNonNull(contentType, "contentType");
        if (length < 0) {
            throw new IllegalArgumentException("length must be >= 0");
        }
    }
}
