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
        if (config.general == null) {
            config.general = new SkyBlockESPConfig.General();
        }
        if (config.general.colorA == null || config.general.colorA.isBlank()) {
            config.general.colorA = "0:255:64:160:255";
        }
        if (config.general.colorB == null || config.general.colorB.isBlank()) {
            config.general.colorB = "0:255:255:85:85";
        }
        if (config.general.colorC == null || config.general.colorC.isBlank()) {
            config.general.colorC = "0:255:255:85:85";
        }
        if (config.privateIsland == null) {
            config.privateIsland = new SkyBlockESPConfig.PrivateIsland();
        }
        if (config.garden == null) {
            config.garden = new SkyBlockESPConfig.EmptyIsland();
        }
        if (config.hub == null) {
            config.hub = new SkyBlockESPConfig.Hub();
        }
        if (config.theRift == null) {
            config.theRift = new SkyBlockESPConfig.EmptyIsland();
        }
        if (config.dungeonHub == null) {
            config.dungeonHub = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.theFarmingIslands == null) {
            config.theFarmingIslands = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.thePark == null) {
            config.thePark = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.moongladeMarsh == null) {
            config.moongladeMarsh = new SkyBlockESPConfig.FairySoulFloorDropIsland();
        }
        if (config.torrhusCanyon == null) {
            config.torrhusCanyon = new SkyBlockESPConfig.FairySoulFloorDropIsland();
        }
        if (config.desert == null) {
            config.desert = new SkyBlockESPConfig.Desert();
        }
        if (config.goldMine == null) {
            config.goldMine = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.deepCaverns == null) {
            config.deepCaverns = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.dwarvenMines == null) {
            config.dwarvenMines = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.crystalHollows == null) {
            config.crystalHollows = new SkyBlockESPConfig.EmptyIsland();
        }
        if (config.spidersDen == null) {
            config.spidersDen = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.theEnd == null) {
            config.theEnd = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.crimsonIsle == null) {
            config.crimsonIsle = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.backwaterBayou == null) {
            config.backwaterBayou = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.lotusAtoll == null) {
            config.lotusAtoll = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.jerrysWorkshop == null) {
            config.jerrysWorkshop = new SkyBlockESPConfig.FairySoulIsland();
        }
        if (config.debug == null) {
            config.debug = new SkyBlockESPConfig.Debug();
        }
    }

    public static void save(SkyBlockESPConfig config) {
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
