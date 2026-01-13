package testsupport;

import api.common.RepositoryDescriptor;
import api.plugin.PluginAvailability;
import api.plugin.RepositoryPlugin;
import api.repository.RepositoryConnector;

public class TestRepositoryPlugin implements RepositoryPlugin {
    private final String pluginId;
    private final String repositoryType;
    private final PluginAvailability availability;
    private final RepositoryConnector connector;

    public TestRepositoryPlugin(String pluginId, String repositoryType,
                                PluginAvailability availability, RepositoryConnector connector) {
        this.pluginId = pluginId;
        this.repositoryType = repositoryType;
        this.availability = availability;
        this.connector = connector;
    }

    @Override
    public String pluginId() {
        return pluginId;
    }

    @Override
    public String repositoryType() {
        return repositoryType;
    }

    @Override
    public PluginAvailability availability(RepositoryDescriptor repo) {
        return availability;
    }

    @Override
    public RepositoryConnector connector(RepositoryDescriptor repo) {
        return connector;
    }
}
