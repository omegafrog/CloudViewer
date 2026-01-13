package api.common;

import java.util.Map;

public record RepositoryDescriptor(String repositoryId, String type, Map<String, String> config) {}
