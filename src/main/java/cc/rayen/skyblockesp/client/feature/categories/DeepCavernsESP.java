package cc.rayen.skyblockesp.client.feature.categories;

import cc.rayen.skyblockesp.client.feature.ESPMarkers;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.island.CurrentIsland;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class DeepCavernsESP {
    private DeepCavernsESP() {
    }

    public static void render(Minecraft client) {
        if (client.level == null || client.player == null || !SkyBlockESPConfig.INSTANCE.deepCaverns.fairySouls) {
            return;
        }
        if (!CurrentIsland.isIsland("Deep Caverns")) {
            return;
        }

        try (var ignored = client.levelRenderer.collectPerFrameGizmos()) {
            for (BlockPos pos : FairySoulPositions.forIsland("Deep Caverns")) {
                ESPMarkers.renderBlockMarker(pos, "Fairy Soul", ESPMarkers.colorC(), ESPMarkers.colorCFill(), ESPMarkers.colorC());
            }
        }
    }
}
