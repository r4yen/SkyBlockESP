package cc.rayen.skyblockesp.client.feature.categories;

import cc.rayen.skyblockesp.client.feature.ESPMarkers;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.island.CurrentIsland;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class TheFarmingIslandsESP {
    private TheFarmingIslandsESP() {
    }

    public static void render(Minecraft client) {
        if (client.level == null || client.player == null || !SkyBlockESPConfig.INSTANCE.theFarmingIslands.fairySouls) {
            return;
        }
        if (!CurrentIsland.isIsland("The Farming Islands")) {
            return;
        }

        try (var ignored = client.levelRenderer.collectPerFrameGizmos()) {
            for (BlockPos pos : FairySoulPositions.forIsland("The Farming Islands")) {
                ESPMarkers.renderBlockMarker(pos, "Fairy Soul", ESPMarkers.colorC(), ESPMarkers.colorCFill(), ESPMarkers.colorC());
            }
        }
    }
}
