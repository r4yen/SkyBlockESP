package cc.rayen.skyblockesp.client.feature.categories;

import cc.rayen.skyblockesp.client.feature.ESPMarkers;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.island.CurrentIsland;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SafariESP {
    private static final int SAFARI_MIN_Y = 28;
    private static final float TITLE_SCALE = 0.035f;
    private static final double SHYWORM_DUPLICATE_POSITION_TOLERANCE = 0.01;
    private static final int FLOOR_DROP_REQUIRED_DISPLAY_COUNT = 3;
    private static final int FLOOR_DROP_DISPLAY_LINK_BLOCK_DISTANCE = 2;
    private static final int FLOOR_DROP_SCAN_RADIUS = 96;
    private static final int FLOOR_DROP_SCAN_VERTICAL_RADIUS = 48;
    private static final int BEE_NEST_SCAN_RADIUS = 96;
    private static final int BEE_NEST_SCAN_VERTICAL_RADIUS = 48;
    private static final int BEE_NEST_SCAN_BLOCKS_PER_FRAME = 4096;
    private static final long BEE_NEST_SCAN_INTERVAL_MILLIS = 5000L;
    private static final long BEE_NEST_EMPTY_MESSAGE_WINDOW_MILLIS = 1000L;
    private static final long BED_MESSAGE_WINDOW_MILLIS = 1000L;
    private static final int THROW_LINE_MAX_STEPS = 80;
    private static final double THROW_LINE_STEP_SECONDS = 0.05;
    private static final double THROW_LINE_INITIAL_SPEED = 6.85;
    private static final double THROW_LINE_HORIZONTAL_MULTIPLIER = 1.75;
    private static final double THROW_LINE_GRAVITY = 0.0125;
    private static final double THROW_LINE_DRAG = 0.99;
    private static final BlockPos GEMZIE_SWITCH_POS = new BlockPos(-143, 62, 51);
    private static final BlockPos BIRDFEEDER_POS = new BlockPos(-2, 89, 44);
    private static final BlockPos SHINING_COIN_WATER_WEST_FROM = new BlockPos(-14, 58, -44);
    private static final BlockPos SHINING_COIN_WATER_WEST_TO = new BlockPos(6, 58, -56);
    private static final BlockPos SHINING_COIN_WATER_EAST_FROM = new BlockPos(7, 58, -48);
    private static final BlockPos SHINING_COIN_WATER_EAST_TO = new BlockPos(22, 57, -62);
    private static final BlockPos[] DOOMSPIRAL_CANDLE_POSITIONS = {
            new BlockPos(-11, 47, -25),
            new BlockPos(-6, 47, -20),
            new BlockPos(-1, 47, -25),
            new BlockPos(-6, 47, -30)
    };
    private static final BlockPos[] GEMZIE_FIXED_POSITIONS = {
            new BlockPos(-162, 64, 61),
            new BlockPos(-169, 64, 53),
            new BlockPos(-163, 64, 45)
    };
    private static final BlockPos[] BELL_POSITIONS = {
            new BlockPos(-50, 81, 0),
            new BlockPos(-68, 67, -43),
            new BlockPos(-96, 46, -57),
            new BlockPos(-4, 95, -42),
            new BlockPos(47, 55, -7),
            new BlockPos(-30, 125, 59),
            new BlockPos(-90, 109, 16)
    };
    private static final List<BlockPos> beeNestPositions = new ArrayList<>();
    private static final List<BlockPos> pendingBeeNestPositions = new ArrayList<>();
    private static final Set<BlockPos> suppressedBeeNests = new HashSet<>();
    private static long lastBeeNestScanMillis;
    private static boolean beeNestScanInProgress;
    private static int beeNestScanMinX;
    private static int beeNestScanMaxX;
    private static int beeNestScanMinY;
    private static int beeNestScanMaxY;
    private static int beeNestScanMinZ;
    private static int beeNestScanMaxZ;
    private static int beeNestScanX;
    private static int beeNestScanY;
    private static int beeNestScanZ;
    private static BlockPos lastInteractedBeeNest;
    private static long lastBeeNestInteractMillis;
    private static HauntedBed lastInteractedBed;
    private static long lastBedInteractMillis;
    private static HauntedBed activeSleepingBed;
    private static boolean wasSleepingInHauntedBed;
    private static final Set<HauntedBed> suppressedHauntedBeds = new HashSet<>();
    private static boolean hideyhoHiding;

    private SafariESP() {
    }

    private static int bestiaryStrokeColor() {
        return ESPMarkers.colorA();
    }

    private static int bestiaryFillColor() {
        return ESPMarkers.colorAFill();
    }

    private static int bestiaryTextColor() {
        return ESPMarkers.colorA();
    }

    private static int generalEntityStrokeColor() {
        return ESPMarkers.colorB();
    }

    private static int generalEntityFillColor() {
        return ESPMarkers.colorBFill();
    }

    private static int generalEntityTextColor() {
        return ESPMarkers.colorB();
    }

    private static int otherStrokeColor() {
        return ESPMarkers.colorC();
    }

    private static int otherFillColor() {
        return ESPMarkers.colorCFill();
    }

    private static int otherTextColor() {
        return ESPMarkers.colorC();
    }

    public static void render(Minecraft client) {
        SkyBlockESPConfig.Desert config = SkyBlockESPConfig.INSTANCE.desert;
        if (client.level == null || client.player == null || !hasAnySafariESPEnabled(config)) {
            return;
        }
        if (!CurrentIsland.isIsland("Safari")) {
            clearSafariState();
            return;
        }

        try (var ignored = client.levelRenderer.collectPerFrameGizmos()) {
            if (hideyhoHiding) {
                if (config.hideyho) {
                    renderHauntedESP(client, config);
                }
                return;
            }

            if (config.bells) {
                renderBells();
            }
            if (config.throwLine) {
                renderThrowLine(client, config);
            }
            if (config.fairySouls) {
                renderSafariFairySouls();
            }
            if (hasAnyFloorDropsEnabled(config)) {
                renderFloorDrops(client, config);
            }
            if (hasAnyCavernESPEnabled(config)) {
                renderCavernESP(client, config);
            }
            if (hasAnyForestESPEnabled(config)) {
                renderForestESP(client, config);
            }
            if (hasAnyHauntedESPEnabled(config)) {
                renderHauntedESP(client, config);
            }
            if (hasAnyIcyESPEnabled(config)) {
                renderIcyESP(client, config);
            }
        }
    }

    public static void handleUseBlock(Level level, BlockHitResult hit) {
        if (!level.isClientSide() || !CurrentIsland.isIsland("Safari")) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.BEE_NEST)) {
            lastInteractedBeeNest = hit.getBlockPos().immutable();
            lastBeeNestInteractMillis = System.currentTimeMillis();
        }

        if (state.getBlock() instanceof BedBlock) {
            HauntedBed bed = hauntedBedAt(pos);
            if (bed != null) {
                lastInteractedBed = bed;
                lastBedInteractMillis = System.currentTimeMillis();
            }
        }
    }

    public static void handleChatMessage(String message) {
        if (!CurrentIsland.isIsland("Safari")) {
            return;
        }

        String normalizedMessage = normalizeDisplayName(message);
        if (normalizedMessage.contains("[MOB] Hideyho: Hehe, ok!")) {
            hideyhoHiding = true;
        } else if (normalizedMessage.contains("[MOB] Hideyho: Aah! You found me!")) {
            hideyhoHiding = false;
        }

        if (lastInteractedBeeNest != null
                && System.currentTimeMillis() - lastBeeNestInteractMillis <= BEE_NEST_EMPTY_MESSAGE_WINDOW_MILLIS
                && message.contains("Looks like the hive is empty now...")) {
            suppressedBeeNests.add(lastInteractedBeeNest);
            lastInteractedBeeNest = null;
            lastBeeNestInteractMillis = 0L;
        }

        if (lastInteractedBed != null
                && System.currentTimeMillis() - lastBedInteractMillis <= BED_MESSAGE_WINDOW_MILLIS
                && normalizedMessage.contains("You don't feel like sleeping here again.")) {
            suppressHauntedBed(lastInteractedBed);
            lastInteractedBed = null;
            lastBedInteractMillis = 0L;
        }
    }

    private static void clearSafariState() {
        beeNestPositions.clear();
        pendingBeeNestPositions.clear();
        suppressedBeeNests.clear();
        lastBeeNestScanMillis = 0L;
        beeNestScanInProgress = false;
        lastInteractedBeeNest = null;
        lastBeeNestInteractMillis = 0L;
        lastInteractedBed = null;
        lastBedInteractMillis = 0L;
        activeSleepingBed = null;
        wasSleepingInHauntedBed = false;
        suppressedHauntedBeds.clear();
        hideyhoHiding = false;
    }

    private static boolean hasAnySafariESPEnabled(SkyBlockESPConfig.Desert config) {
        return config.fairySouls
                || config.bells
                || config.throwLine
                || hasAnyFloorDropsEnabled(config)
                || hasAnyCavernESPEnabled(config)
                || hasAnyForestESPEnabled(config)
                || hasAnyHauntedESPEnabled(config)
                || hasAnyIcyESPEnabled(config);
    }

    private static boolean hasAnyFloorDropsEnabled(SkyBlockESPConfig.Desert config) {
        return config.cavernFloorDrops
                || config.forestFloorDrops
                || config.hauntedFloorDrops
                || config.icyFloorDrops;
    }

    private static boolean hasAnyCavernESPEnabled(SkyBlockESPConfig.Desert config) {
        return config.cavernfish
                || config.flitter
                || config.shyworm
                || config.driftling
                || config.chuckwalla
                || config.rockmite
                || config.rockmiteHome
                || config.scrappy
                || config.snoozle
                || config.gemzie;
    }

    private static boolean hasAnyForestESPEnabled(SkyBlockESPConfig.Desert config) {
        return config.forestBeeNest
                || config.forestBirdfeeder
                || config.foxtrot
                || config.bluebird
                || config.honeybug
                || config.treefrog
                || config.woodchucker
                || config.fluffling
                || config.hideonfloor
                || config.paraket
                || config.macaw;
    }

    private static boolean hasAnyHauntedESPEnabled(SkyBlockESPConfig.Desert config) {
        return config.hauntedBeds
                || config.doomspiralCandles
                || config.shiningCoinWater
                || hasAnyHauntedEntityESPEnabled(config);
    }

    private static boolean hasAnyHauntedEntityESPEnabled(SkyBlockESPConfig.Desert config) {
        return config.areita
                || config.bloodbat
                || config.duplico
                || config.gazer
                || config.litterbug
                || config.solsnatcher
                || config.gimmiegold
                || config.hideonwall
                || config.hideyho
                || config.doomspiral;
    }

    private static boolean hasAnyIcyESPEnabled(SkyBlockESPConfig.Desert config) {
        return config.strongarm
                || config.tepid
                || config.polaris
                || config.shuddersquid
                || config.billygoat
                || config.mantisShrimp
                || config.nozzlenose
                || config.troodon
                || config.wumpa;
    }

    private static void renderBells() {
        for (BlockPos pos : BELL_POSITIONS) {
            renderMarker(pos, "Bell", otherStrokeColor(), otherFillColor(), otherTextColor());
        }
    }

    private static void renderSafariFairySouls() {
        for (BlockPos pos : FairySoulPositions.forIsland("Safari")) {
            renderMarker(pos, "Fairy Soul", otherStrokeColor(), otherFillColor(), otherTextColor());
        }
    }

    private static void renderFloorDrops(Minecraft client, SkyBlockESPConfig.Desert config) {
        Region currentRegion = regionOf(client.player.blockPosition());
        if (currentRegion == null || !isFloorDropEnabledForRegion(currentRegion, config)) {
            return;
        }

        for (AABB box : findFloorDrops(client)) {
            if (regionOf(BlockPos.containing(box.getCenter())) != currentRegion) {
                continue;
            }
            renderBoxMarker(box, "Floor Drop", otherStrokeColor(), otherFillColor(), otherTextColor());
        }
    }

    private static void renderCavernESP(Minecraft client, SkyBlockESPConfig.Desert config) {
        if (!isCavernPosition(client.player.blockPosition())) {
            return;
        }

        float partialTick = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        List<Vec3> renderedShywormPositions = new ArrayList<>();

        for (Entity entity : client.level.entitiesForRendering()) {
            if (!isCavernPosition(entity.blockPosition())) {
                continue;
            }

            if (config.cavernfish && isCavernfish(entity)) {
                renderEntityMarker(entity, partialTick, "Cavernfish", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.flitter && entity.getType() == EntityType.BAT) {
                renderEntityMarker(entity, partialTick, "Flitter", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.shyworm && entity.getType() == EntityType.SLIME) {
                if (!isDuplicatePosition(entity.position(), renderedShywormPositions, SHYWORM_DUPLICATE_POSITION_TOLERANCE)) {
                    renderedShywormPositions.add(entity.position());
                    renderEntityMarker(entity, partialTick, "Shyworm", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
                }
            }
            if (config.driftling && isDriftling(entity)) {
                renderUpperQuarterMarker(entity, partialTick, "Driftling", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.chuckwalla && entity.getType() == EntityType.SILVERFISH && entity.isInvisible()) {
                renderEntityMarker(entity, partialTick, "Chuckwalla", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.rockmite && entity.getType() == EntityType.SILVERFISH && !entity.isInvisible()) {
                renderEntityMarker(entity, partialTick, "Rockmite", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.rockmiteHome && isRockmiteHome(entity)) {
                renderEntityMarker(entity, partialTick, "Rockmite's Home", otherStrokeColor(), otherFillColor(), otherTextColor());
            }
            if (config.scrappy && entity.getType() == EntityType.ARMADILLO) {
                renderEntityMarker(entity, partialTick, "Scrappy", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.snoozle && entity.getType() == EntityType.SNIFFER) {
                renderEntityMarker(entity, partialTick, "Snoozle", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.gemzie && client.level.getBlockState(GEMZIE_SWITCH_POS).isAir() && entity.getType() == EntityType.VEX) {
                renderEntityMarker(entity, partialTick, "Gemzie", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
        }

        if (config.gemzie && !client.level.getBlockState(GEMZIE_SWITCH_POS).isAir()) {
            for (BlockPos pos : GEMZIE_FIXED_POSITIONS) {
                renderMarker(pos, "Gemzie", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
        }
    }

    private static void renderForestESP(Minecraft client, SkyBlockESPConfig.Desert config) {
        if (!isForestPosition(client.player.blockPosition())) {
            return;
        }

        if (config.forestBirdfeeder && hasInventoryItemNamed(client, "Wriggleworm", "Yogi Berry", "Bag of Seeds")) {
            renderTallMarker(BIRDFEEDER_POS, 2.0, "Birdfeeder", otherStrokeColor(), otherFillColor(), otherTextColor());
        }

        if (config.forestBeeNest) {
            renderBeeNests(client);
        }

        if (!hasAnyForestEntityESPEnabled(config)) {
            return;
        }

        float partialTick = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        for (Entity entity : client.level.entitiesForRendering()) {
            if (!isForestPosition(entity.blockPosition())) {
                continue;
            }

            if (config.foxtrot && entity.getType() == EntityType.FOX) {
                renderEntityMarker(entity, partialTick, "Foxtrot", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.bluebird && isParrotVariant(entity, Parrot.Variant.BLUE)) {
                renderEntityMarker(entity, partialTick, "Bluebird", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.honeybug && entity.getType() == EntityType.BEE) {
                renderEntityMarker(entity, partialTick, "Honeybug", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.treefrog && entity.getType() == EntityType.FROG) {
                renderEntityMarker(entity, partialTick, "Treefrog", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.woodchucker && entity.getType() == EntityType.CREAKING) {
                renderEntityMarker(entity, partialTick, "Woodchucker", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.fluffling && entity.getType() == EntityType.PANDA) {
                renderEntityMarker(entity, partialTick, "Fluffling", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.hideonfloor && entity.getType() == EntityType.SHULKER) {
                renderEntityMarker(entity, partialTick, "Hideonfloor", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.paraket && isParrotVariant(entity, Parrot.Variant.GREEN)) {
                renderEntityMarker(entity, partialTick, "Paraket", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.macaw && isParrotVariant(entity, Parrot.Variant.RED_BLUE)) {
                renderEntityMarker(entity, partialTick, "Macaw", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
        }
    }

    private static void renderHauntedESP(Minecraft client, SkyBlockESPConfig.Desert config) {
        if (!isHauntedPosition(client.player.blockPosition())) {
            return;
        }

        if (!hideyhoHiding && config.hauntedBeds) {
            renderHauntedBeds(client);
        }
        if (!hideyhoHiding && config.doomspiralCandles && hasInventoryItemNamed(client, "Soothing Incense")) {
            renderDoomspiralCandles(client);
        }
        if (!hideyhoHiding && config.shiningCoinWater && hasInventoryItemNamed(client, "Shining Coin")) {
            renderShiningCoinWater(client);
        }
        if (!hasAnyHauntedEntityESPEnabled(config)) {
            return;
        }

        float partialTick = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        for (Entity entity : client.level.entitiesForRendering()) {
            if (!isHauntedPosition(entity.blockPosition())) {
                continue;
            }

            if (config.hideyho && isHideyho(entity)) {
                renderEntityMarker(entity, partialTick, "Hideyho", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }

            if (hideyhoHiding) {
                continue;
            }

            if (config.areita && entity.getType() == EntityType.CAVE_SPIDER) {
                renderEntityMarker(entity, partialTick, "Areita", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.bloodbat && entity.getType() == EntityType.BAT) {
                renderEntityMarker(entity, partialTick, "Bloodbat", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.duplico && isSizedEntity(entity, EntityType.INTERACTION, 1.1, 1.1, 1.1)) {
                renderSizedEntityMarker(entity, partialTick, 1.0, 1.0, 1.0, "Duplico", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.gazer && isGazer(entity)) {
                renderUpperQuarterMarker(entity, partialTick, "Gazer", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.litterbug && entity.getType() == EntityType.ENDERMITE) {
                renderEntityMarker(entity, partialTick, "Litterbug", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.solsnatcher && entity.getType() == EntityType.PHANTOM) {
                renderEntityMarker(entity, partialTick, "Solsnatcher", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.gimmiegold && entity.getType() == EntityType.TROPICAL_FISH) {
                renderEntityMarker(entity, partialTick, "Gimmiegold", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.hideonwall && isHideonwall(entity)) {
                renderEntityMarker(entity, partialTick, "Hideonwall", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.doomspiral && entity.getType() == EntityType.WARDEN) {
                renderEntityMarker(entity, partialTick, "Doomspiral", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
        }
    }

    private static void renderIcyESP(Minecraft client, SkyBlockESPConfig.Desert config) {
        if (!isIcyPosition(client.player.blockPosition())) {
            return;
        }

        float partialTick = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        for (Entity entity : client.level.entitiesForRendering()) {
            if (!isIcyPosition(entity.blockPosition())) {
                continue;
            }

            if (config.strongarm && entity.getType() == EntityType.SNOW_GOLEM) {
                renderEntityMarker(entity, partialTick, "Strongarm", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.tepid && isSizedEntity(entity, EntityType.TROPICAL_FISH, 0.5, 0.4, 0.5)) {
                renderEntityMarker(entity, partialTick, "Tepid", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.polaris && entity.getType() == EntityType.POLAR_BEAR) {
                renderEntityMarker(entity, partialTick, "Polaris", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.shuddersquid && entity.getType() == EntityType.GLOW_SQUID) {
                renderEntityMarker(entity, partialTick, "Shuddersquid", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.billygoat && entity.getType() == EntityType.GOAT) {
                renderEntityMarker(entity, partialTick, "Billygoat", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.mantisShrimp && isSizedEntity(entity, EntityType.TROPICAL_FISH, 0.7, 0.56, 0.7)) {
                renderEntityMarker(entity, partialTick, "Mantis Shrimp", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.nozzlenose && entity.getType() == EntityType.DOLPHIN) {
                renderEntityMarker(entity, partialTick, "Nozzlenose", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.troodon && entity.getType() == EntityType.SILVERFISH) {
                renderEntityMarker(entity, partialTick, "Troodon", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
            if (config.wumpa && entity.getType() == EntityType.RAVAGER) {
                renderEntityMarker(entity, partialTick, "Wumpa", bestiaryStrokeColor(), bestiaryFillColor(), bestiaryTextColor());
            }
        }
    }

    private static boolean hasAnyForestEntityESPEnabled(SkyBlockESPConfig.Desert config) {
        return config.foxtrot
                || config.bluebird
                || config.honeybug
                || config.treefrog
                || config.woodchucker
                || config.fluffling
                || config.hideonfloor
                || config.paraket
                || config.macaw;
    }

    private static void renderThrowLine(Minecraft client, SkyBlockESPConfig.Desert config) {
        if (!isCritterCapsule(client.player.getItemInHand(InteractionHand.MAIN_HAND))
                && !isCritterCapsule(client.player.getItemInHand(InteractionHand.OFF_HAND))) {
            return;
        }

        float partialTick = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        Vec3 previous = client.player.getEyePosition(partialTick);
        Vec3 velocity = initialThrowLineVelocity(client.player.getViewVector(partialTick).normalize());
        int color = otherStrokeColor();

        for (int step = 0; step < THROW_LINE_MAX_STEPS; step++) {
            Vec3 next = previous.add(velocity.scale(THROW_LINE_STEP_SECONDS));
            HitResult blockHit = client.level.clip(new ClipContext(previous, next, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, client.player));
            if (blockHit.getType() != HitResult.Type.MISS) {
                next = blockHit.getLocation();
                renderThrowSegment(previous, next, color);
                return;
            }

            EntityHit coloredEntityHit = findThrowLineEntityHit(client, config, previous, next);
            if (coloredEntityHit != null) {
                color = coloredEntityHit.color;
                next = coloredEntityHit.location;
                renderThrowSegment(previous, next, color);
                return;
            }

            renderThrowSegment(previous, next, color);
            previous = next;
            velocity = velocity.scale(THROW_LINE_DRAG).add(0.0, -THROW_LINE_GRAVITY, 0.0);
        }
    }

    private static void renderThrowSegment(Vec3 previous, Vec3 next, int color) {
        Gizmos.line(previous, next, color, 2.0f)
                .setAlwaysOnTop()
                .persistForMillis(75);
    }

    private static Vec3 initialThrowLineVelocity(Vec3 viewVector) {
        return new Vec3(
                viewVector.x * THROW_LINE_INITIAL_SPEED * THROW_LINE_HORIZONTAL_MULTIPLIER,
                viewVector.y * THROW_LINE_INITIAL_SPEED,
                viewVector.z * THROW_LINE_INITIAL_SPEED * THROW_LINE_HORIZONTAL_MULTIPLIER
        );
    }

    private static EntityHit findThrowLineEntityHit(Minecraft client, SkyBlockESPConfig.Desert config, Vec3 previous, Vec3 next) {
        AABB segmentBox = new AABB(previous, next).inflate(0.35);
        EntityHit bestHit = null;
        double bestDistance = Double.MAX_VALUE;
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity == client.player || !entity.isAlive() || !entity.isPickable()) {
                continue;
            }

            int color = espColorForEntity(entity, config);
            if (color == 0) {
                continue;
            }

            AABB box = entity.getBoundingBox().inflate(0.35);
            if (!box.intersects(segmentBox)) {
                continue;
            }

            Vec3 hit = box.clip(previous, next).orElse(null);
            if (hit == null) {
                continue;
            }

            double distance = previous.distanceToSqr(hit);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestHit = new EntityHit(hit, color);
            }
        }
        return bestHit;
    }

    private static void renderBeeNests(Minecraft client) {
        updateBeeNestCache(client);
        for (BlockPos pos : beeNestPositions) {
            if (!suppressedBeeNests.contains(pos) && isForestPosition(pos)) {
                renderMarker(pos, "Bee Nest", otherStrokeColor(), otherFillColor(), otherTextColor());
            }
        }
    }

    private static void renderHauntedBeds(Minecraft client) {
        updateHauntedBedSleepState(client);

        for (HauntedBed bed : HauntedBed.values()) {
            if (!suppressedHauntedBeds.contains(bed)) {
                renderHauntedBedBox(bed);
            }
        }
    }

    private static void updateHauntedBedSleepState(Minecraft client) {
        boolean isSleeping = client.player.getSleepTimer() > 0;
        long now = System.currentTimeMillis();
        if (isSleeping && lastInteractedBed != null && now - lastBedInteractMillis <= 3000L) {
            activeSleepingBed = lastInteractedBed;
            wasSleepingInHauntedBed = true;
            return;
        }

        if (!isSleeping && wasSleepingInHauntedBed && activeSleepingBed != null) {
            suppressHauntedBed(activeSleepingBed);
            activeSleepingBed = null;
            wasSleepingInHauntedBed = false;
        }
    }

    private static void renderHauntedBedBox(HauntedBed bed) {
        AABB box = inclusiveBlockBox(bed.first, bed.second);
        renderBoxMarker(box, "Bed", otherStrokeColor(), otherFillColor(), otherTextColor());
    }

    private static void renderDoomspiralCandles(Minecraft client) {
        for (BlockPos pos : DOOMSPIRAL_CANDLE_POSITIONS) {
            BlockState state = client.level.getBlockState(pos);
            if (state.hasProperty(BlockStateProperties.LIT) && !state.getValue(BlockStateProperties.LIT)) {
                renderMarker(pos, "Doomspiral Candle", otherStrokeColor(), otherFillColor(), otherTextColor());
            }
        }
    }

    private static void renderShiningCoinWater(Minecraft client) {
        renderBoxMarker(inclusiveBlockBox(SHINING_COIN_WATER_WEST_FROM, SHINING_COIN_WATER_WEST_TO), "Shining Coin Water", otherStrokeColor(), otherFillColor(), otherTextColor());
        renderBoxMarker(inclusiveBlockBox(SHINING_COIN_WATER_EAST_FROM, SHINING_COIN_WATER_EAST_TO), "Shining Coin Water", otherStrokeColor(), otherFillColor(), otherTextColor());
    }

    private static AABB inclusiveBlockBox(BlockPos first, BlockPos second) {
        int minX = Math.min(first.getX(), second.getX());
        int minY = Math.min(first.getY(), second.getY());
        int minZ = Math.min(first.getZ(), second.getZ());
        int maxX = Math.max(first.getX(), second.getX()) + 1;
        int maxY = Math.max(first.getY(), second.getY()) + 1;
        int maxZ = Math.max(first.getZ(), second.getZ()) + 1;
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static HauntedBed hauntedBedAt(BlockPos pos) {
        for (HauntedBed bed : HauntedBed.values()) {
            if (bed.contains(pos)) {
                return bed;
            }
        }
        return null;
    }

    private static void suppressHauntedBed(HauntedBed bed) {
        suppressedHauntedBeds.add(bed);
    }

    private static void updateBeeNestCache(Minecraft client) {
        long now = System.currentTimeMillis();
        if (!beeNestScanInProgress && now - lastBeeNestScanMillis < BEE_NEST_SCAN_INTERVAL_MILLIS) {
            return;
        }

        if (!beeNestScanInProgress) {
            beginBeeNestScan(client);
        }

        int checkedBlocks = 0;
        while (beeNestScanInProgress && checkedBlocks < BEE_NEST_SCAN_BLOCKS_PER_FRAME) {
            BlockPos pos = new BlockPos(beeNestScanX, beeNestScanY, beeNestScanZ);
            if (isSafariHeight(pos) && isForestPosition(pos) && client.level.getBlockState(pos).is(Blocks.BEE_NEST)) {
                pendingBeeNestPositions.add(pos.immutable());
            }
            advanceBeeNestScan();
            checkedBlocks++;
        }

        if (!beeNestScanInProgress) {
            beeNestPositions.clear();
            beeNestPositions.addAll(pendingBeeNestPositions);
            pendingBeeNestPositions.clear();
        }
    }

    private static void beginBeeNestScan(Minecraft client) {
        lastBeeNestScanMillis = System.currentTimeMillis();
        pendingBeeNestPositions.clear();

        BlockPos center = client.player.blockPosition();
        beeNestScanMinX = center.getX() - BEE_NEST_SCAN_RADIUS;
        beeNestScanMaxX = center.getX() + BEE_NEST_SCAN_RADIUS;
        beeNestScanMinY = Math.max(client.level.getMinY(), center.getY() - BEE_NEST_SCAN_VERTICAL_RADIUS);
        beeNestScanMaxY = Math.min(client.level.getMaxY(), center.getY() + BEE_NEST_SCAN_VERTICAL_RADIUS);
        beeNestScanMinZ = center.getZ() - BEE_NEST_SCAN_RADIUS;
        beeNestScanMaxZ = center.getZ() + BEE_NEST_SCAN_RADIUS;
        beeNestScanX = beeNestScanMinX;
        beeNestScanY = beeNestScanMinY;
        beeNestScanZ = beeNestScanMinZ;
        beeNestScanInProgress = true;
    }

    private static void advanceBeeNestScan() {
        if (++beeNestScanZ <= beeNestScanMaxZ) {
            return;
        }
        beeNestScanZ = beeNestScanMinZ;

        if (++beeNestScanY <= beeNestScanMaxY) {
            return;
        }
        beeNestScanY = beeNestScanMinY;

        if (++beeNestScanX <= beeNestScanMaxX) {
            return;
        }
        beeNestScanInProgress = false;
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
        for (Entity entity : client.level.getEntities(client.player, scanBox, SafariESP::isFloorDropDisplay)) {
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

    private static boolean isFloorDropEnabledForRegion(Region region, SkyBlockESPConfig.Desert config) {
        return switch (region) {
            case FOREST -> config.forestFloorDrops;
            case HAUNTED -> config.hauntedFloorDrops;
            case CAVERN -> config.cavernFloorDrops;
            case ICY -> config.icyFloorDrops;
        };
    }

    private static boolean isCavernPosition(BlockPos pos) {
        return isSafariHeight(pos) && pos.getX() < -50 && pos.getZ() > 0;
    }

    private static boolean isForestPosition(BlockPos pos) {
        return isSafariHeight(pos) && pos.getX() > -50 && pos.getZ() > 0;
    }

    private static boolean isHauntedPosition(BlockPos pos) {
        return isSafariHeight(pos) && pos.getX() > -50 && pos.getZ() < 0;
    }

    private static boolean isIcyPosition(BlockPos pos) {
        return isSafariHeight(pos) && pos.getX() < -50 && pos.getZ() < 0;
    }

    private static Region regionOf(BlockPos pos) {
        if (!isSafariHeight(pos)) {
            return null;
        }
        if (pos.getX() > -50 && pos.getZ() > 0) {
            return Region.FOREST;
        }
        if (pos.getX() > -50 && pos.getZ() < 0) {
            return Region.HAUNTED;
        }
        if (pos.getX() < -50 && pos.getZ() > 0) {
            return Region.CAVERN;
        }
        if (pos.getX() < -50 && pos.getZ() < 0) {
            return Region.ICY;
        }
        return null;
    }

    private static boolean isSafariHeight(BlockPos pos) {
        return pos.getY() >= SAFARI_MIN_Y;
    }

    private static boolean isSafariHeight(AABB box) {
        return box.maxY >= SAFARI_MIN_Y;
    }

    private static boolean isParrotVariant(Entity entity, Parrot.Variant variant) {
        return entity instanceof Parrot parrot && parrot.getVariant() == variant;
    }

    private static boolean isSizedEntity(Entity entity, EntityType<?> type, double xSize, double ySize, double zSize) {
        if (entity.getType() != type) {
            return false;
        }

        AABB box = entity.getBoundingBox();
        return closeTo(box.getXsize(), xSize)
                && closeTo(box.getYsize(), ySize)
                && closeTo(box.getZsize(), zSize);
    }

    private static boolean isHideyho(Entity entity) {
        if (entity.getType() != EntityType.PLAYER) {
            return false;
        }

        return normalizeDisplayName(entity.getDisplayName().getString()).equals("Hideyho");
    }

    private static boolean isGazer(Entity entity) {
        return isHeadItemArmorStand(entity) && hasMaxHealth(entity, 1.0);
    }

    private static boolean isHeadItemArmorStand(Entity entity) {
        if (!(entity instanceof ArmorStand armorStand)
                || !isSizedEntity(entity, EntityType.ARMOR_STAND, 0.5, 1.975, 0.5)) {
            return false;
        }

        return !armorStand.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
    }

    private static boolean hasMaxHealth(Entity entity, double maxHealth) {
        return entity instanceof LivingEntity living && closeTo(living.getMaxHealth(), maxHealth);
    }

    private static boolean isHideonwall(Entity entity) {
        return entity.getType() == EntityType.SHULKER
                || entity.getType() == EntityType.FALLING_BLOCK
                || entity.getType() == EntityType.BLOCK_DISPLAY;
    }

    private static boolean hasInventoryItemNamed(Minecraft client, String... names) {
        return client.player.getInventory().contains(stack -> hasAnyName(stack, names));
    }

    private static boolean hasAnyName(ItemStack stack, String... names) {
        if (stack.isEmpty()) {
            return false;
        }

        String stackName = normalizeDisplayName(stack.getHoverName().getString());
        for (String name : names) {
            if (stackName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCritterCapsule(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        String name = normalizeDisplayName(stack.getHoverName().getString()).toLowerCase();
        return name.equals("critter capsule") || name.equals("masterful critter capsule");
    }

    private static int espColorForEntity(Entity entity, SkyBlockESPConfig.Desert config) {
        if (isCavernPosition(entity.blockPosition()) && !hideyhoHiding) {
            if (config.cavernfish && isCavernfish(entity)) return bestiaryStrokeColor();
            if (config.flitter && entity.getType() == EntityType.BAT) return bestiaryStrokeColor();
            if (config.shyworm && entity.getType() == EntityType.SLIME) return bestiaryStrokeColor();
            if (config.driftling && isDriftling(entity)) return bestiaryStrokeColor();
            if (config.chuckwalla && entity.getType() == EntityType.SILVERFISH && entity.isInvisible()) return bestiaryStrokeColor();
            if (config.rockmite && entity.getType() == EntityType.SILVERFISH && !entity.isInvisible()) return bestiaryStrokeColor();
            if (config.rockmiteHome && isRockmiteHome(entity)) return otherStrokeColor();
            if (config.scrappy && entity.getType() == EntityType.ARMADILLO) return bestiaryStrokeColor();
            if (config.snoozle && entity.getType() == EntityType.SNIFFER) return bestiaryStrokeColor();
            if (config.gemzie && entity.getType() == EntityType.VEX) return bestiaryStrokeColor();
        }

        if (isForestPosition(entity.blockPosition())) {
            if (config.foxtrot && entity.getType() == EntityType.FOX) return bestiaryStrokeColor();
            if (config.bluebird && isParrotVariant(entity, Parrot.Variant.BLUE)) return bestiaryStrokeColor();
            if (config.honeybug && entity.getType() == EntityType.BEE) return bestiaryStrokeColor();
            if (config.treefrog && entity.getType() == EntityType.FROG) return bestiaryStrokeColor();
            if (config.woodchucker && entity.getType() == EntityType.CREAKING) return bestiaryStrokeColor();
            if (config.fluffling && entity.getType() == EntityType.PANDA) return bestiaryStrokeColor();
            if (config.hideonfloor && entity.getType() == EntityType.SHULKER) return bestiaryStrokeColor();
            if (config.paraket && isParrotVariant(entity, Parrot.Variant.GREEN)) return bestiaryStrokeColor();
            if (config.macaw && isParrotVariant(entity, Parrot.Variant.RED_BLUE)) return bestiaryStrokeColor();
        }

        if (isHauntedPosition(entity.blockPosition())) {
            if (config.hideyho && isHideyho(entity)) return bestiaryStrokeColor();
            if (!hideyhoHiding) {
                if (config.areita && entity.getType() == EntityType.CAVE_SPIDER) return bestiaryStrokeColor();
                if (config.bloodbat && entity.getType() == EntityType.BAT) return bestiaryStrokeColor();
                if (config.duplico && isSizedEntity(entity, EntityType.INTERACTION, 1.1, 1.1, 1.1)) return bestiaryStrokeColor();
                if (config.gazer && isGazer(entity)) return bestiaryStrokeColor();
                if (config.litterbug && entity.getType() == EntityType.ENDERMITE) return bestiaryStrokeColor();
                if (config.solsnatcher && entity.getType() == EntityType.PHANTOM) return bestiaryStrokeColor();
                if (config.gimmiegold && entity.getType() == EntityType.TROPICAL_FISH) return bestiaryStrokeColor();
                if (config.hideonwall && isHideonwall(entity)) return bestiaryStrokeColor();
                if (config.doomspiral && entity.getType() == EntityType.WARDEN) return bestiaryStrokeColor();
            }
        }

        if (isIcyPosition(entity.blockPosition())) {
            if (config.strongarm && entity.getType() == EntityType.SNOW_GOLEM) return bestiaryStrokeColor();
            if (config.tepid && isSizedEntity(entity, EntityType.TROPICAL_FISH, 0.5, 0.4, 0.5)) return bestiaryStrokeColor();
            if (config.polaris && entity.getType() == EntityType.POLAR_BEAR) return bestiaryStrokeColor();
            if (config.shuddersquid && entity.getType() == EntityType.GLOW_SQUID) return bestiaryStrokeColor();
            if (config.billygoat && entity.getType() == EntityType.GOAT) return bestiaryStrokeColor();
            if (config.mantisShrimp && isSizedEntity(entity, EntityType.TROPICAL_FISH, 0.7, 0.56, 0.7)) return bestiaryStrokeColor();
            if (config.nozzlenose && entity.getType() == EntityType.DOLPHIN) return bestiaryStrokeColor();
            if (config.troodon && entity.getType() == EntityType.SILVERFISH) return bestiaryStrokeColor();
            if (config.wumpa && entity.getType() == EntityType.RAVAGER) return bestiaryStrokeColor();
        }

        return 0;
    }

    private static boolean isCavernfish(Entity entity) {
        if (entity.getType() != EntityType.TROPICAL_FISH) {
            return false;
        }

        AABB box = entity.getBoundingBox();
        return closeTo(box.getXsize(), 0.5)
                && closeTo(box.getYsize(), 0.4)
                && closeTo(box.getZsize(), 0.5);
    }

    private static boolean isRockmiteHome(Entity entity) {
        if (entity.getY() >= 70.0) {
            return false;
        }

        Vec3 movement = entity.getDeltaMovement();
        Vec3 position = entity.position();
        return entity.getType() == EntityType.INTERACTION
                && closeTo(movement.x, 0.0)
                && closeTo(movement.y, 0.0)
                && closeTo(movement.z, 0.0)
                && fractionalPartCloseTo(position.x, 0.5)
                && fractionalPartCloseTo(position.y, 0.0)
                && fractionalPartCloseTo(position.z, 0.5);
    }

    private static boolean isDriftling(Entity entity) {
        if (!(entity instanceof ArmorStand armorStand) || !armorStand.isInvisible()) {
            return false;
        }

        AABB box = armorStand.getBoundingBox();
        return closeTo(box.getXsize(), 0.625)
                && closeTo(box.getYsize(), 2.469)
                && closeTo(box.getZsize(), 0.625);
    }

    private static String normalizedName(Entity entity) {
        StringBuilder name = new StringBuilder(entity.getName().getString().toLowerCase());
        if (entity.getCustomName() != null) {
            name.append(' ').append(entity.getCustomName().getString().toLowerCase());
        }
        return name.toString();
    }

    private static String normalizeDisplayName(String name) {
        return name.replaceAll("§.", "").trim().replaceAll("\\s+", " ");
    }

    private static boolean closeTo(double actual, double expected) {
        return Math.abs(actual - expected) < 0.01;
    }

    private static boolean fractionalPartCloseTo(double actual, double expected) {
        return closeTo(actual - Math.floor(actual), expected);
    }

    private static void renderMarker(BlockPos pos, String label, int strokeColor, int fillColor, int textColor) {
        if (!isSafariHeight(pos)) {
            return;
        }

        Gizmos.cuboid(pos, GizmoStyle.strokeAndFill(strokeColor, 2.0f, fillColor))
                .setAlwaysOnTop()
                .persistForMillis(75);
        renderTitle(label, Vec3.atCenterOf(pos), textColor);
    }

    private static void renderTallMarker(BlockPos pos, double height, String label, int strokeColor, int fillColor, int textColor) {
        if (!isSafariHeight(pos)) {
            return;
        }

        AABB box = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + height, pos.getZ() + 1.0);
        renderBoxMarker(box, box.getCenter(), label, strokeColor, fillColor, textColor);
    }

    private static void renderEntityMarker(Entity entity, float partialTick, String label, int strokeColor, int fillColor, int textColor) {
        Vec3 position = entity.getPosition(partialTick);
        AABB box = entityBoxAt(entity, position);
        renderBoxMarker(box, box.getCenter(), label, strokeColor, fillColor, textColor);
    }

    private static void renderUpperQuarterMarker(Entity entity, float partialTick, String label, int strokeColor, int fillColor, int textColor) {
        Vec3 position = entity.getPosition(partialTick);
        AABB fullBox = entityBoxAt(entity, position);
        double upperQuarterMinY = fullBox.maxY - fullBox.getYsize() * 0.25;
        AABB box = new AABB(fullBox.minX, upperQuarterMinY, fullBox.minZ, fullBox.maxX, fullBox.maxY, fullBox.maxZ);
        renderBoxMarker(box, box.getCenter(), label, strokeColor, fillColor, textColor);
    }

    private static void renderSizedEntityMarker(Entity entity, float partialTick, double xSize, double ySize, double zSize, String label, int strokeColor, int fillColor, int textColor) {
        Vec3 position = entity.getPosition(partialTick);
        AABB box = AABB.ofSize(position.add(0.0, ySize / 2.0, 0.0), xSize, ySize, zSize);
        renderBoxMarker(box, box.getCenter(), label, strokeColor, fillColor, textColor);
    }

    private static boolean isDuplicatePosition(Vec3 position, List<Vec3> positions, double tolerance) {
        double toleranceSquared = tolerance * tolerance;
        for (Vec3 existingPosition : positions) {
            if (position.distanceToSqr(existingPosition) <= toleranceSquared) {
                return true;
            }
        }
        return false;
    }

    private static AABB entityBoxAt(Entity entity, Vec3 position) {
        return AABB.ofSize(position.add(0.0, entity.getBbHeight() / 2.0, 0.0), entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
    }

    private static void renderBoxMarker(AABB box, String label, int strokeColor, int fillColor, int textColor) {
        renderBoxMarker(box, box.getCenter(), label, strokeColor, fillColor, textColor);
    }

    private static void renderBoxMarker(AABB box, Vec3 labelPosition, String label, int strokeColor, int fillColor, int textColor) {
        if (!isSafariHeight(box)) {
            return;
        }

        Gizmos.cuboid(box, GizmoStyle.strokeAndFill(strokeColor, 2.0f, fillColor))
                .setAlwaysOnTop()
                .persistForMillis(75);
        renderTitle(label, labelPosition, textColor);
    }

    private static void renderTitle(String label, Vec3 position, int textColor) {
        if (!SkyBlockESPConfig.INSTANCE.debug.showTitles) {
            return;
        }

        Gizmos.billboardText(label, position, TextGizmo.Style.forColorAndCentered(textColor).withScale(TITLE_SCALE))
                .setAlwaysOnTop()
                .persistForMillis(75);
    }

    private record EntityHit(Vec3 location, int color) {
    }

    private enum Region {
        FOREST,
        HAUNTED,
        CAVERN,
        ICY
    }

    private enum HauntedBed {
        WEST_NORTH(new BlockPos(-26, 77, -64), new BlockPos(-25, 77, -64)),
        WEST_SOUTH(new BlockPos(-26, 77, -69), new BlockPos(-25, 77, -69)),
        EAST_WEST(new BlockPos(8, 77, -81), new BlockPos(8, 77, -80)),
        EAST_EAST(new BlockPos(13, 77, -81), new BlockPos(13, 77, -80));

        private final BlockPos first;
        private final BlockPos second;

        HauntedBed(BlockPos first, BlockPos second) {
            this.first = first;
            this.second = second;
        }

        private boolean contains(BlockPos pos) {
            return pos.getY() == first.getY()
                    && pos.getX() >= Math.min(first.getX(), second.getX())
                    && pos.getX() <= Math.max(first.getX(), second.getX())
                    && pos.getZ() >= Math.min(first.getZ(), second.getZ())
                    && pos.getZ() <= Math.max(first.getZ(), second.getZ());
        }
    }
}
