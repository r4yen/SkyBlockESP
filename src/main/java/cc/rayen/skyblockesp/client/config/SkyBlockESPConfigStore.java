package cc.rayen.skyblockesp.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SkyBlockESPConfigStore {
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("skyblockesp.json");

    private SkyBlockESPConfigStore() {
    }

    public static SkyBlockESPConfig load() {
        SkyBlockESPConfig config = readConfig();
        ensureDefaults(config);
        config.saveRunnables.add(() -> save(config));
        return config;
    }

    private static SkyBlockESPConfig readConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            return new SkyBlockESPConfig();
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            SkyBlockESPConfig config = GSON.fromJson(reader, SkyBlockESPConfig.class);
            return config == null ? new SkyBlockESPConfig() : config;
        } catch (IOException | RuntimeException exception) {
            return new SkyBlockESPConfig();
        }
    }

    private static void ensureDefaults(SkyBlockESPConfig config) {
        if (config.desert == null) {
            config.desert = new SkyBlockESPConfig.Desert();
        }
        if (config.debug == null) {
            config.debug = new SkyBlockESPConfig.Debug();
        }
    }

    private static void save(SkyBlockESPConfig config) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            // Config saving should not crash the client.
        }
    }
}
