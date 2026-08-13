package cc.rayen.skyblockesp.client.feature.categories;

import cc.rayen.skyblockesp.client.feature.ESPMarkers;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.island.CurrentIsland;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class HubESP {
    private static final float TITLE_SCALE = 0.035f;

    private HubESP() {
    }

    public static void render(Minecraft client) {
        SkyBlockESPConfig.Hub config = SkyBlockESPConfig.INSTANCE.hub;
        if (client.level == null || client.player == null || !hasAnyHubESPEnabled(config)) {
            return;
        }
        if (!CurrentIsland.isIsland("Hub")) {
            return;
        }

        try (var ignored = client.levelRenderer.collectPerFrameGizmos()) {
            if (config.fairySouls) {
                renderFairySouls();
            }

            if (!hasAnyBestiaryEntityEnabled(config)) {
                return;
            }

            float partialTick = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
            for (Entity entity : client.level.entitiesForRendering()) {
                String label = entityLabel(entity, config);
                if (label != null) {
                    renderEntityMarker(entity, partialTick, label);
                }
            }
        }
    }

    private static boolean hasAnyHubESPEnabled(SkyBlockESPConfig.Hub config) {
        return config.fairySouls || hasAnyBestiaryEntityEnabled(config);
    }

    private static boolean hasAnyBestiaryEntityEnabled(SkyBlockESPConfig.Hub config) {
        return config.cryptGhoul
                || config.gholdenGhoul
                || config.graveyardZombie
                || config.oldWolf
                || config.shinyPig
                || config.wolf
                || config.zombieVillager;
    }

    private static String entityLabel(Entity entity, SkyBlockESPConfig.Hub config) {
        if (entity instanceof LivingEntity living) {
            if (config.cryptGhoul && entity.getType() == EntityType.ZOMBIE && living.getMainHandItem().is(Items.IRON_SWORD)) return "Crypt Ghoul";
            if (config.gholdenGhoul && entity.getType() == EntityType.ZOMBIE && living.getMainHandItem().is(Items.GOLDEN_SWORD)) return "Gholden Ghoul";
            if (config.graveyardZombie && entity.getType() == EntityType.ZOMBIE && living.getMainHandItem().isEmpty()) return "Graveyard Zombie";
            if (config.oldWolf && entity.getType() == EntityType.WOLF && closeTo(living.getMaxHealth(), 1024.0)) return "Old Wolf";
            if (config.wolf && entity.getType() == EntityType.WOLF && !closeTo(living.getMaxHealth(), 1024.0)) return "Wolf";
        }

        if (config.shinyPig && entity.getType() == EntityType.PIG) return "Shiny Pig";
        if (config.zombieVillager && entity.getType() == EntityType.ZOMBIE_VILLAGER) return "Zombie Villager";
        return null;
    }

    private static void renderFairySouls() {
        for (BlockPos pos : FairySoulPositions.forIsland("Hub")) {
            ESPMarkers.renderBlockMarker(pos, "Fairy Soul", ESPMarkers.colorC(), ESPMarkers.colorCFill(), ESPMarkers.colorC());
        }
    }

    private static void renderEntityMarker(Entity entity, float partialTick, String label) {
        Vec3 position = entity.getPosition(partialTick);
        AABB box = AABB.ofSize(position.add(0.0, entity.getBbHeight() / 2.0, 0.0), entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
        Gizmos.cuboid(box, GizmoStyle.strokeAndFill(ESPMarkers.colorA(), 2.0f, ESPMarkers.colorAFill()))
                .setAlwaysOnTop()
                .persistForMillis(75);
        renderTitle(label, box.getCenter());
    }

    private static void renderTitle(String label, Vec3 position) {
        if (!SkyBlockESPConfig.INSTANCE.debug.showTitles) {
            return;
        }

        Gizmos.billboardText(label, position, TextGizmo.Style.forColorAndCentered(ESPMarkers.colorA()).withScale(TITLE_SCALE))
                .setAlwaysOnTop()
                .persistForMillis(75);
    }

    private static boolean closeTo(double actual, double expected) {
        return Math.abs(actual - expected) < 0.01;
    }
}
