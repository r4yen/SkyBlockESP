package cc.rayen.skyblockesp.client.feature.categories;

import cc.rayen.skyblockesp.client.feature.ESPMarkers;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.island.CurrentIsland;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class DwarvenMinesESP {
    private DwarvenMinesESP() {
    }

    public static void render(Minecraft client) {
        if (client.level == null || client.player == null || !SkyBlockESPConfig.INSTANCE.dwarvenMines.fairySouls) {
            return;
        }
        if (!CurrentIsland.isIsland("Dwarven Mines")) {
            return;
        }

        try (var ignored = client.levelRenderer.collectPerFrameGizmos()) {
            for (BlockPos pos : FairySoulPositions.forIsland("Dwarven Mines")) {
                ESPMarkers.renderBlockMarker(pos, "Fairy Soul", ESPMarkers.colorC(), ESPMarkers.colorCFill(), ESPMarkers.colorC());
            }
        }
    }
}
