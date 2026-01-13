package api.common;

import java.io.InputStream;

public record DownloadStream(InputStream stream, String contentType, long length) {}
