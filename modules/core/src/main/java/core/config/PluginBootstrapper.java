package core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugin.runtime.PluginLoader;
import plugin.runtime.PluginRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginBootstrapper {
    private static final Logger logger = LoggerFactory.getLogger(PluginBootstrapper.class);

    public void loadFromDirectory(Path pluginsDir, PluginRegistry registry, PluginLoader loader) {
        Path resolved = resolvePluginsDir(pluginsDir);
        if (resolved == null) {
            logger.info("Plugins directory not found; skipping plugin load.");
            return;
        }
        logger.info("Loading plugins from {}", resolved.toAbsolutePath());
        try (var stream = Files.list(resolved)) {
            stream.filter(path -> path.toString().endsWith(".jar"))
                    .forEach(path -> loadJar(path, registry, loader));
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to scan plugins directory: " + resolved, ex);
        }
    }

    private void loadJar(Path jarPath, PluginRegistry registry, PluginLoader loader) {
        logger.info("Loading plugin jar: {}", jarPath);
        registry.register(loader.load(jarPath));
    }

    private Path resolvePluginsDir(Path pluginsDir) {
        if (pluginsDir == null) {
            return null;
        }
        if (pluginsDir.isAbsolute()) {
            return Files.exists(pluginsDir) ? pluginsDir : null;
        }
        Path base = Path.of(System.getProperty("user.dir"));
        Path direct = base.resolve(pluginsDir).normalize();
        if (Files.exists(direct)) {
            return direct;
        }
        Path current = base;
        while (current != null) {
            Path candidate = current.resolve(pluginsDir).normalize();
            if (Files.exists(candidate)) {
                return candidate;
            }
            Path parent = current.getParent();
            if (parent == null || parent.equals(current)) {
                break;
            }
            current = parent;
        }
        return null;
    }
}
