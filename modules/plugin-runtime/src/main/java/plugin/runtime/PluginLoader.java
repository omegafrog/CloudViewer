package plugin.runtime;

import api.plugin.RepositoryPlugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ServiceLoader;

public class PluginLoader {
    public RepositoryPlugin load(Path jarPath) {
        try {
            URL jarUrl = jarPath.toUri().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, RepositoryPlugin.class.getClassLoader());
            ServiceLoader<RepositoryPlugin> loader = ServiceLoader.load(RepositoryPlugin.class, classLoader);
            Iterator<RepositoryPlugin> iterator = loader.iterator();
            if (!iterator.hasNext()) {
                throw new IllegalStateException("No RepositoryPlugin implementation found in " + jarPath);
            }
            RepositoryPlugin plugin = iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalStateException("Multiple RepositoryPlugin implementations found in " + jarPath);
            }
            return plugin;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load plugin from " + jarPath, ex);
        }
    }
}
