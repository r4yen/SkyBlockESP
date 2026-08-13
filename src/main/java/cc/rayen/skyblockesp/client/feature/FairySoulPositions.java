package cc.rayen.skyblockesp.client.feature;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FairySoulPositions {
    private static final String RESOURCE_PATH = "/assets/skyblockesp/fairy_souls.json";
    private static final Gson GSON = new Gson();
    private static Map<String, List<BlockPos>> positionsByIsland;

    private FairySoulPositions() {
    }

    public static void preload() {
        positionsByIsland();
    }

    public static List<BlockPos> forIsland(String island) {
        return positionsByIsland().getOrDefault(dataIslandName(island), Collections.emptyList());
    }

    private static String dataIslandName(String island) {
        return switch (island) {
            case "Galatea" -> "Moonglade Marsh";
            case "Winter" -> "Jerry's Workshop";
            default -> island;
        };
    }

    private static Map<String, List<BlockPos>> positionsByIsland() {
        if (positionsByIsland == null) {
            positionsByIsland = loadPositions();
        }
        return positionsByIsland;
    }

    private static Map<String, List<BlockPos>> loadPositions() {
        Type type = new TypeToken<Map<String, List<String>>>() {
        }.getType();

        try (Reader reader = new InputStreamReader(FairySoulPositions.class.getResourceAsStream(RESOURCE_PATH), StandardCharsets.UTF_8)) {
            Map<String, List<String>> rawPositions = GSON.fromJson(reader, type);
            Map<String, List<BlockPos>> positions = new HashMap<>();
            rawPositions.forEach((island, rawList) -> {
                List<BlockPos> parsed = new ArrayList<>();
                for (String raw : rawList) {
                    parsed.add(parsePosition(raw));
                }
                positions.put(island, parsed);
            });
            return positions;
        } catch (RuntimeException | java.io.IOException exception) {
            return Collections.emptyMap();
        }
    }

    private static BlockPos parsePosition(String raw) {
        String[] parts = raw.split(":");
        return new BlockPos(
                (int) Double.parseDouble(parts[0]),
                (int) Double.parseDouble(parts[1]),
                (int) Double.parseDouble(parts[2])
        );
    }
}
