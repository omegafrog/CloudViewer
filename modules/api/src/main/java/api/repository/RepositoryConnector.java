package api.repository;

import api.common.RepositoryDescriptor;

public interface RepositoryConnector {
    RepositoryHandle open(RepositoryDescriptor repo);
}
