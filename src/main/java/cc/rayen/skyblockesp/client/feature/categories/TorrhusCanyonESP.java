package cc.rayen.skyblockesp.client.feature.categories;

import cc.rayen.skyblockesp.client.feature.ESPMarkers;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.island.CurrentIsland;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TorrhusCanyonESP {
    private static final int FLOOR_DROP_REQUIRED_DISPLAY_COUNT = 3;
    private static final int FLOOR_DROP_DISPLAY_LINK_BLOCK_DISTANCE = 2;
    private static final int FLOOR_DROP_SCAN_RADIUS = 96;
    private static final int FLOOR_DROP_SCAN_VERTICAL_RADIUS = 48;

    private TorrhusCanyonESP() {
    }

    public static void render(Minecraft client) {
        SkyBlockESPConfig.FairySoulFloorDropIsland config = SkyBlockESPConfig.INSTANCE.torrhusCanyon;
        if (client.level == null || client.player == null || !hasAnyTorrhusCanyonESPEnabled(config)) {
            return;
        }
        if (!CurrentIsland.isIsland("Torrhus Canyon")) {
            return;
        }

        try (var ignored = client.levelRenderer.collectPerFrameGizmos()) {
            if (config.fairySouls) {
                for (BlockPos pos : FairySoulPositions.forIsland("Torrhus Canyon")) {
                    ESPMarkers.renderBlockMarker(pos, "Fairy Soul", ESPMarkers.colorC(), ESPMarkers.colorCFill(), ESPMarkers.colorC());
                }
            }

            if (config.floorDrops) {
                for (AABB box : findFloorDrops(client)) {
                    ESPMarkers.renderBoxMarker(box, box.getCenter(), "Floor Drop", ESPMarkers.colorC(), ESPMarkers.colorCFill(), ESPMarkers.colorC());
                }
            }
        }
    }

    private static boolean hasAnyTorrhusCanyonESPEnabled(SkyBlockESPConfig.FairySoulFloorDropIsland config) {
        return config.fairySouls || config.floorDrops;
    }

    private static List<AABB> findFloorDrops(Minecraft client) {
        BlockPos center = client.player.blockPosition();
        AABB scanBox = new AABB(
                center.getX() - FLOOR_DROP_SCAN_RADIUS,
                center.getY() - FLOOR_DROP_SCAN_VERTICAL_RADIUS,
                center.getZ() - FLOOR_DROP_SCAN_RADIUS,
                center.getX() + FLOOR_DROP_SCAN_RADIUS,
                center.getY() + FLOOR_DROP_SCAN_VERTICAL_RADIUS,
                center.getZ() + FLOOR_DROP_SCAN_RADIUS
        );

        List<Display.ItemDisplay> displays = new ArrayList<>();
        for (Entity entity : client.level.getEntities(client.player, scanBox, TorrhusCanyonESP::isFloorDropDisplay)) {
            displays.add((Display.ItemDisplay) entity);
        }

        List<AABB> floorDrops = new ArrayList<>();
        for (List<Display.ItemDisplay> group : groupFloorDropDisplays(displays)) {
            if (group.size() == FLOOR_DROP_REQUIRED_DISPLAY_COUNT) {
                floorDrops.add(floorDropBox(group));
            }
        }

        return floorDrops;
    }

    private static boolean isFloorDropDisplay(Entity entity) {
        return entity instanceof Display.ItemDisplay itemDisplay && itemDisplay.getItemStack().is(Items.STRING);
    }

    private static List<List<Display.ItemDisplay>> groupFloorDropDisplays(List<Display.ItemDisplay> displays) {
        List<List<Display.ItemDisplay>> groups = new ArrayList<>();
        Set<Integer> visitedEntityIds = new HashSet<>();

        for (Display.ItemDisplay seed : displays) {
            if (visitedEntityIds.contains(seed.getId())) {
                continue;
            }

            List<Display.ItemDisplay> group = new ArrayList<>();
            group.add(seed);
            visitedEntityIds.add(seed.getId());

            boolean changed;
            do {
                changed = false;
                for (Display.ItemDisplay candidate : displays) {
                    if (visitedEntityIds.contains(candidate.getId())) {
                        continue;
                    }
                    if (isSameFloorDropCluster(candidate, group)) {
                        group.add(candidate);
                        visitedEntityIds.add(candidate.getId());
                        changed = true;
                    }
                }
            } while (changed);

            groups.add(group);
        }

        return groups;
    }

    private static boolean isSameFloorDropCluster(Display.ItemDisplay candidate, List<Display.ItemDisplay> group) {
        BlockPos candidatePos = candidate.blockPosition();
        for (Display.ItemDisplay display : group) {
            BlockPos pos = display.blockPosition();
            if (Math.abs(candidatePos.getX() - pos.getX()) <= FLOOR_DROP_DISPLAY_LINK_BLOCK_DISTANCE
                    && Math.abs(candidatePos.getY() - pos.getY()) <= FLOOR_DROP_DISPLAY_LINK_BLOCK_DISTANCE
                    && Math.abs(candidatePos.getZ() - pos.getZ()) <= FLOOR_DROP_DISPLAY_LINK_BLOCK_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    private static AABB floorDropBox(List<Display.ItemDisplay> displays) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Display.ItemDisplay display : displays) {
            BlockPos pos = display.blockPosition();
            minX = Math.min(minX, pos.getX());
            maxX = Math.max(maxX, pos.getX());
            minZ = Math.min(minZ, pos.getZ());
            maxZ = Math.max(maxZ, pos.getZ());
            minY = Math.min(minY, display.getY());
            maxY = Math.max(maxY, display.getY() + 0.5);
        }

        return new AABB(minX, minY, minZ, maxX + 1.0, maxY, maxZ + 1.0);
    }
}
