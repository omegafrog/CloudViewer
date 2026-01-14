package core.plugin;

import api.plugin.RepositoryPlugin;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import plugin.runtime.PluginLoader;
import plugin.runtime.PluginRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PluginManager {
    private final PluginRegistry registry;
    private final PluginLoader loader;
    private final Path pluginsDir;
    private volatile Path resolvedPluginsDir;
    private final Map<String, PluginRecord> byId = new ConcurrentHashMap<>();
    private final Map<String, String> byType = new ConcurrentHashMap<>();
    private final Set<String> disabledTypes = ConcurrentHashMap.newKeySet();

    public PluginManager(PluginRegistry registry, PluginLoader loader, Path pluginsDir) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.loader = Objects.requireNonNull(loader, "loader");
        this.pluginsDir = Objects.requireNonNull(pluginsDir, "pluginsDir");
    }

    public void bootstrap() {
        Path resolved = resolvePluginsDirForRead();
        if (resolved == null) {
            return;
        }
        resolvedPluginsDir = resolved;
        try (var stream = Files.list(resolved)) {
            stream.filter(path -> path.toString().endsWith(".jar"))
                    .forEach(this::registerFromJar);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to scan plugins directory: " + resolved, ex);
        }
    }

    public List<PluginRecord> list() {
        return byId.values().stream()
                .sorted(Comparator.comparing(PluginRecord::pluginId))
                .collect(Collectors.toList());
    }

    public PluginRecord upload(MultipartFile file) {
        Objects.requireNonNull(file, "file");
        Path targetDir;
        try {
            targetDir = ensurePluginsDirForWrite();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to prepare plugins directory", ex);
        }
        String originalName = Optional.ofNullable(file.getOriginalFilename()).orElse("plugin.jar");
        if (!originalName.endsWith(".jar")) {
            throw preconditionViolation("Only .jar files are supported: " + originalName);
        }
        Path temp = null;
        try {
            temp = Files.createTempFile(targetDir, "upload-", ".jar");
            file.transferTo(temp);
            RepositoryPlugin plugin = loader.load(temp);
            validateUnique(plugin);
            Path target = targetDir.resolve(plugin.pluginId() + ".jar");
            if (Files.exists(target)) {
                throw preconditionViolation("Plugin jar already exists: " + target.getFileName());
            }
            Files.move(temp, target);
            registerLoadedPlugin(plugin, target.getFileName().toString());
            return byId.get(plugin.pluginId());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store plugin jar", ex);
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ignored) {
                }
            }
        }
    }

    public PluginRecord enable(String pluginId) {
        PluginRecord record = getOrThrow(pluginId);
        disabledTypes.remove(record.repositoryType());
        if (registry.findByType(record.repositoryType()).isEmpty()) {
            Path jarPath = resolvePluginsDirForReadOrWrite().resolve(record.jarName());
            RepositoryPlugin plugin = loader.load(jarPath);
            registry.register(plugin);
        }
        return updateStatus(record, PluginStatus.ENABLED);
    }

    public PluginRecord disable(String pluginId) {
        PluginRecord record = getOrThrow(pluginId);
        disabledTypes.add(record.repositoryType());
        return updateStatus(record, PluginStatus.DISABLED);
    }

    public void remove(String pluginId) {
        PluginRecord record = getOrThrow(pluginId);
        disabledTypes.add(record.repositoryType());
        Path jarPath = resolvePluginsDirForReadOrWrite().resolve(record.jarName());
        try {
            Files.deleteIfExists(jarPath);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete plugin jar: " + jarPath.getFileName(), ex);
        }
        byId.remove(pluginId);
        byType.remove(record.repositoryType());
    }

    public boolean isDisabled(String repositoryType) {
        return disabledTypes.contains(repositoryType);
    }

    private PluginRecord updateStatus(PluginRecord record, PluginStatus status) {
        PluginRecord updated = record.withStatus(status);
        byId.put(updated.pluginId(), updated);
        return updated;
    }

    private void registerFromJar(Path jarPath) {
        RepositoryPlugin plugin = loader.load(jarPath);
        validateUnique(plugin);
        registry.register(plugin);
        registerLoadedPlugin(plugin, jarPath.getFileName().toString());
    }

    private void registerLoadedPlugin(RepositoryPlugin plugin, String jarName) {
        PluginStatus status = disabledTypes.contains(plugin.repositoryType())
                ? PluginStatus.DISABLED
                : PluginStatus.ENABLED;
        PluginRecord record = new PluginRecord(plugin.pluginId(), plugin.repositoryType(), jarName, status);
        byId.put(plugin.pluginId(), record);
        byType.put(plugin.repositoryType(), plugin.pluginId());
    }

    private void validateUnique(RepositoryPlugin plugin) {
        if (byId.containsKey(plugin.pluginId())) {
            throw preconditionViolation("Duplicate pluginId: " + plugin.pluginId());
        }
        if (byType.containsKey(plugin.repositoryType())) {
            throw preconditionViolation("Duplicate repositoryType: " + plugin.repositoryType());
        }
    }

    private PluginRecord getOrThrow(String pluginId) {
        PluginRecord record = byId.get(pluginId);
        if (record == null) {
            throw preconditionViolation("Plugin not found: " + pluginId);
        }
        return record;
    }

    private Path resolvePluginsDirForRead() {
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

    private Path ensurePluginsDirForWrite() throws IOException {
        if (resolvedPluginsDir != null) {
            return resolvedPluginsDir;
        }
        Path base = pluginsDir.isAbsolute()
                ? pluginsDir
                : Path.of(System.getProperty("user.dir")).resolve(pluginsDir).normalize();
        if (!Files.exists(base)) {
            Files.createDirectories(base);
        }
        resolvedPluginsDir = base;
        return base;
    }

    private Path resolvePluginsDirForReadOrWrite() {
        if (resolvedPluginsDir != null) {
            return resolvedPluginsDir;
        }
        Path resolved = resolvePluginsDirForRead();
        if (resolved != null) {
            resolvedPluginsDir = resolved;
            return resolved;
        }
        try {
            return ensurePluginsDirForWrite();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to prepare plugins directory", ex);
        }
    }

    private IllegalStateException preconditionViolation(String message) {
        return new IllegalStateException("Precondition violation: " + message);
    }
}
