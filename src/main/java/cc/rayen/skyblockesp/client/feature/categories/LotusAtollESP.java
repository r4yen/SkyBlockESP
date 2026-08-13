package cc.rayen.skyblockesp.client.feature.categories;

import cc.rayen.skyblockesp.client.feature.ESPMarkers;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.island.CurrentIsland;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class LotusAtollESP {
    private LotusAtollESP() {
    }

    public static void render(Minecraft client) {
        if (client.level == null || client.player == null || !SkyBlockESPConfig.INSTANCE.lotusAtoll.fairySouls) {
            return;
        }
        if (!CurrentIsland.isIsland("Lotus Atoll")) {
            return;
        }

        try (var ignored = client.levelRenderer.collectPerFrameGizmos()) {
            for (BlockPos pos : FairySoulPositions.forIsland("Lotus Atoll")) {
                ESPMarkers.renderBlockMarker(pos, "Fairy Soul", ESPMarkers.colorC(), ESPMarkers.colorCFill(), ESPMarkers.colorC());
            }
        }
    }
}
