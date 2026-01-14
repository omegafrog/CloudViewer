package coretestplugin;

import api.common.RepositoryDescriptor;
import api.plugin.PluginAvailability;
import api.plugin.RepositoryPlugin;
import api.repository.RepositoryConnector;

public class TestPlugin implements RepositoryPlugin {
    @Override
    public String pluginId() {
        return "test-plugin";
    }

    @Override
    public String repositoryType() {
        return "TEST";
    }

    @Override
    public PluginAvailability availability(RepositoryDescriptor repo) {
        return new PluginAvailability(PluginAvailability.Status.AVAILABLE, null, null);
    }

    @Override
    public RepositoryConnector connector(RepositoryDescriptor repo) {
        return new TestRepositoryConnector();
    }
}
