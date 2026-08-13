package cc.rayen.skyblockesp.client.feature.categories;

import cc.rayen.skyblockesp.client.feature.ESPMarkers;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.island.CurrentIsland;
import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PrivateIslandESP {
    private static final float TITLE_SCALE = 0.035f;

    private PrivateIslandESP() {
    }

    public static void render(Minecraft client) {
        SkyBlockESPConfig.PrivateIsland config = SkyBlockESPConfig.INSTANCE.privateIsland;
        if (client.level == null || client.player == null || !hasAnyPrivateIslandESPEnabled(config)) {
            return;
        }
        if (!CurrentIsland.isIsland("Private Island")) {
            return;
        }

        try (var ignored = client.levelRenderer.collectPerFrameGizmos()) {
            float partialTick = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
            for (Entity entity : client.level.entitiesForRendering()) {
                if (config.minions && isMinion(entity)) {
                    renderEntityMarker(entity, partialTick, "Minion");
                }

                String label = entityLabel(entity, config);
                if (label != null) {
                    renderEntityMarker(entity, partialTick, label);
                }
            }
        }
    }

    private static boolean hasAnyPrivateIslandESPEnabled(SkyBlockESPConfig.PrivateIsland config) {
        return config.minions
                || config.bat
                || config.creeper
                || config.enderman
                || config.skeleton
                || config.slime
                || config.spider
                || config.witch
                || config.zombie;
    }

    private static String entityLabel(Entity entity, SkyBlockESPConfig.PrivateIsland config) {
        EntityType<?> type = entity.getType();
        if (config.bat && type == EntityType.BAT) return "Bat";
        if (config.creeper && type == EntityType.CREEPER) return "Creeper";
        if (config.enderman && type == EntityType.ENDERMAN) return "Enderman";
        if (config.skeleton && type == EntityType.SKELETON) return "Skeleton";
        if (config.slime && type == EntityType.SLIME) return "Slime";
        if (config.spider && type == EntityType.SPIDER) return "Spider";
        if (config.witch && type == EntityType.WITCH) return "Witch";
        if (config.zombie && type == EntityType.ZOMBIE) return "Zombie";
        return null;
    }

    private static boolean isMinion(Entity entity) {
        if (!(entity instanceof ArmorStand armorStand) || armorStand.isInvisible()) {
            return false;
        }

        AABB box = entity.getBoundingBox();
        return closeTo(box.getXsize(), 0.25)
                && closeTo(box.getYsize(), 0.988)
                && closeTo(box.getZsize(), 0.25);
    }

    private static void renderEntityMarker(Entity entity, float partialTick, String label) {
        Vec3 position = entity.getPosition(partialTick);
        AABB box = AABB.ofSize(position.add(0.0, entity.getBbHeight() / 2.0, 0.0), entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
        renderBoxMarker(box, box.getCenter(), label);
    }

    private static void renderBoxMarker(AABB box, Vec3 labelPosition, String label) {
        Gizmos.cuboid(box, GizmoStyle.strokeAndFill(ESPMarkers.colorB(), 2.0f, ESPMarkers.colorBFill()))
                .setAlwaysOnTop()
                .persistForMillis(75);
        renderTitle(label, labelPosition);
    }

    private static void renderTitle(String label, Vec3 position) {
        if (!SkyBlockESPConfig.INSTANCE.debug.showTitles) {
            return;
        }

        Gizmos.billboardText(label, position, TextGizmo.Style.forColorAndCentered(ESPMarkers.colorB()).withScale(TITLE_SCALE))
                .setAlwaysOnTop()
                .persistForMillis(75);
    }

    private static boolean closeTo(double actual, double expected) {
        return Math.abs(actual - expected) < 0.01;
    }

}
