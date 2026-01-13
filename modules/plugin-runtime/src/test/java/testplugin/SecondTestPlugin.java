package testplugin;

import api.common.RepositoryDescriptor;
import api.plugin.PluginAvailability;
import api.plugin.RepositoryPlugin;
import api.repository.RepositoryConnector;

public class SecondTestPlugin implements RepositoryPlugin {
    @Override
    public String pluginId() {
        return "test-plugin-2";
    }

    @Override
    public String repositoryType() {
        return "TEST-2";
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
