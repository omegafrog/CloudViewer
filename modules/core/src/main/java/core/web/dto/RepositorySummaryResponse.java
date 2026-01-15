package core.web.dto;

import api.common.RepositoryRegistration;

import java.util.Map;

public record RepositorySummaryResponse(String userId, String repositoryId, String type, Map<String, String> config) {
    public static RepositorySummaryResponse from(RepositoryRegistration registration) {
        return new RepositorySummaryResponse(
                registration.userId(),
                registration.repositoryId(),
                registration.type(),
                registration.config());
    }
}
