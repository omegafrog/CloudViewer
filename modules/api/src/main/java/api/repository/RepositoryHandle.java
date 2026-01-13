package api.repository;

import api.common.RepositoryMeta;

public interface RepositoryHandle {
    FileHandle fileHandle();
    RepositoryMeta meta();
}
