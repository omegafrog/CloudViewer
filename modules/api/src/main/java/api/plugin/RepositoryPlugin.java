package api.plugin;

import api.common.RepositoryDescriptor;
import api.repository.RepositoryConnector;

public interface RepositoryPlugin {
    String pluginId();
    String repositoryType();
    PluginAvailability availability(RepositoryDescriptor repo);
    RepositoryConnector connector(RepositoryDescriptor repo);
}
