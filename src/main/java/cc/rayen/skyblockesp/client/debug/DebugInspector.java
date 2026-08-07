package cc.rayen.skyblockesp.client.debug;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class DebugInspector {
    private static final double INSPECT_DISTANCE = 100.0;
    private static final long GHOST_DURATION_MILLIS = 5000L;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<GhostedBlock> GHOSTED_BLOCKS = new ArrayList<>();
    private static final List<GhostedEntity> GHOSTED_ENTITIES = new ArrayList<>();
    private static boolean wasBlockInspectKeyDown;
    private static boolean wasEntityInspectKeyDown;
    private static boolean wasGhostTargetKeyDown;

    private DebugInspector() {
    }

    public static void tick(Minecraft client) {
        if (!SkyBlockESPConfig.INSTANCE.general.debugFunctions
                || client.player == null
                || client.level == null
                || client.screen != null) {
            wasBlockInspectKeyDown = false;
            wasEntityInspectKeyDown = false;
            wasGhostTargetKeyDown = false;
            return;
        }

        restoreExpiredGhosts(client);

        SkyBlockESPConfig.Debug debug = SkyBlockESPConfig.INSTANCE.debug;
        boolean isBlockKeyDown = isKeyDown(client, debug.blockInspectKey);
        boolean isEntityKeyDown = isKeyDown(client, debug.entityInspectKey);
        boolean isGhostTargetKeyDown = isKeyDown(client, debug.ghostTargetKey);

        if (isBlockKeyDown && !wasBlockInspectKeyDown) {
            inspectBlock(client, INSPECT_DISTANCE);
        }
        if (isEntityKeyDown && !wasEntityInspectKeyDown) {
            inspectEntity(client, INSPECT_DISTANCE);
        }
        if (isGhostTargetKeyDown && !wasGhostTargetKeyDown) {
            ghostTarget(client, INSPECT_DISTANCE);
        }

        wasBlockInspectKeyDown = isBlockKeyDown;
        wasEntityInspectKeyDown = isEntityKeyDown;
        wasGhostTargetKeyDown = isGhostTargetKeyDown;
    }

    private static boolean isKeyDown(Minecraft client, int key) {
        if (key < 0) {
            return false;
        }
        if (key <= GLFW.GLFW_MOUSE_BUTTON_LAST) {
            return GLFW.glfwGetMouseButton(client.getWindow().handle(), key) == GLFW.GLFW_PRESS;
        }
        return InputConstants.isKeyDown(client.getWindow(), key);
    }

    private static void inspectBlock(Minecraft client, double distance) {
        HitResult hit = client.player.pick(distance, 0.0f, false);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            sendInfo(client, "No block in range.");
            return;
        }

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = client.level.getBlockState(pos);
        BlockEntity blockEntity = client.level.getBlockEntity(pos);

        JsonObject json = new JsonObject();
        json.addProperty("target", "block");
        json.add("position", blockPosJson(pos));
        json.addProperty("distance", round(client.player.getEyePosition().distanceTo(hit.getLocation())));
        json.addProperty("side", blockHit.getDirection().getName());
        json.add("hitLocation", vecJson(hit.getLocation()));
        json.add("blockState", blockStateJson(state));

        if (blockEntity != null) {
            json.add("blockEntity", blockEntityJson(client, blockEntity));
        }

        sendJson(client, "Block", json);
    }

    private static void inspectEntity(Minecraft client, double distance) {
        EntityHitResult entityHit = findTargetEntity(client, distance);

        if (entityHit == null) {
            sendInfo(client, "No entity in range.");
            return;
        }

        JsonObject json = entityJson(entityHit.getEntity());
        json.addProperty("distance", round(client.player.getEyePosition().distanceTo(entityHit.getLocation())));
        json.add("hitLocation", vecJson(entityHit.getLocation()));

        sendJson(client, "Entity", json);
    }

    private static void ghostTarget(Minecraft client, double distance) {
        EntityHitResult entityHit = findTargetEntity(client, distance);
        if (entityHit != null) {
            ghostEntity(client, entityHit.getEntity());
            return;
        }

        HitResult hit = client.player.pick(distance, 0.0f, false);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            sendInfo(client, "No block or entity in range.");
            return;
        }

        ghostBlock(client, blockHit.getBlockPos());
    }

    private static EntityHitResult findTargetEntity(Minecraft client, double distance) {
        Vec3 start = client.player.getEyePosition();
        Vec3 end = start.add(client.player.getViewVector(0.0f).scale(distance));

        HitResult blockHit = client.player.pick(distance, 0.0f, false);
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            end = blockHit.getLocation();
        }

        AABB searchBox = client.player.getBoundingBox()
                .expandTowards(end.subtract(start))
                .inflate(1.0);
        return ProjectileUtil.getEntityHitResult(
                client.level,
                client.player,
                start,
                end,
                searchBox,
                entity -> entity != client.player && entity.isAlive() && entity.isPickable(),
                0.3f
        );
    }

    private static void ghostBlock(Minecraft client, BlockPos pos) {
        BlockState state = client.level.getBlockState(pos);
        if (state.isAir()) {
            sendInfo(client, "Target block is already air.");
            return;
        }

        BlockEntity blockEntity = client.level.getBlockEntity(pos);
        long restoreAt = System.currentTimeMillis() + GHOST_DURATION_MILLIS;
        GHOSTED_BLOCKS.add(new GhostedBlock(pos.immutable(), state, blockEntity, restoreAt));
        client.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 18);
        sendInfo(client, "Ghosted block clientside for 5 seconds.");
    }

    private static void ghostEntity(Minecraft client, Entity entity) {
        long restoreAt = System.currentTimeMillis() + GHOST_DURATION_MILLIS;
        GHOSTED_ENTITIES.add(new GhostedEntity(entity.getId(), entity.getUUID(), entity.isInvisible(), entity.isCustomNameVisible(), restoreAt));
        entity.setInvisible(true);
        entity.setCustomNameVisible(false);
        sendInfo(client, "Ghosted entity clientside for 5 seconds.");
    }

    private static void restoreExpiredGhosts(Minecraft client) {
        long now = System.currentTimeMillis();

        Iterator<GhostedBlock> blockIterator = GHOSTED_BLOCKS.iterator();
        while (blockIterator.hasNext()) {
            GhostedBlock block = blockIterator.next();
            if (block.restoreAt > now) {
                continue;
            }

            client.level.setBlock(block.pos, block.state, 18);
            if (block.blockEntity != null) {
                block.blockEntity.clearRemoved();
                block.blockEntity.setLevel(client.level);
                block.blockEntity.setBlockState(block.state);
                client.level.setBlockEntity(block.blockEntity);
            }
            blockIterator.remove();
        }

        Iterator<GhostedEntity> entityIterator = GHOSTED_ENTITIES.iterator();
        while (entityIterator.hasNext()) {
            GhostedEntity ghost = entityIterator.next();
            if (ghost.restoreAt > now) {
                continue;
            }

            Entity entity = client.level.getEntity(ghost.id);
            if (entity != null && entity.getUUID().equals(ghost.uuid)) {
                entity.setInvisible(ghost.wasInvisible);
                entity.setCustomNameVisible(ghost.wasCustomNameVisible);
            }
            entityIterator.remove();
        }
    }

    private static JsonObject blockStateJson(BlockState state) {
        JsonObject json = new JsonObject();
        json.addProperty("id", String.valueOf(BuiltInRegistries.BLOCK.getKey(state.getBlock())));
        json.addProperty("class", state.getBlock().getClass().getName());
        json.addProperty("string", state.toString());
        json.addProperty("air", state.isAir());
        json.addProperty("hasBlockEntity", state.hasBlockEntity());
        json.addProperty("lightEmission", state.getLightEmission());

        JsonObject properties = new JsonObject();
        state.getProperties().stream()
                .sorted(Comparator.comparing(Property::getName))
                .forEach(property -> addPropertyValue(properties, state, property));
        json.add("properties", properties);
        return json;
    }

    private static <T extends Comparable<T>> void addPropertyValue(JsonObject json, BlockState state, Property<T> property) {
        json.addProperty(property.getName(), property.getName(state.getValue(property)));
    }

    private static JsonObject blockEntityJson(Minecraft client, BlockEntity blockEntity) {
        JsonObject json = new JsonObject();
        json.addProperty("id", String.valueOf(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType())));
        json.addProperty("class", blockEntity.getClass().getName());
        json.addProperty("removed", blockEntity.isRemoved());
        json.add("components", stringJson(blockEntity.components().toString()));

        try {
            json.addProperty("nbt", blockEntity.saveWithFullMetadata(client.level.registryAccess()).toString());
        } catch (RuntimeException exception) {
            json.addProperty("nbtError", exception.getClass().getSimpleName() + ": " + exception.getMessage());
        }

        if (blockEntity instanceof SkullBlockEntity skull) {
            JsonObject skullJson = new JsonObject();
            skullJson.addProperty("noteBlockSound", String.valueOf(skull.getNoteBlockSound()));
            if (skull.getOwnerProfile() != null) {
                skullJson.addProperty("ownerProfile", skull.getOwnerProfile().toString());
                skullJson.addProperty("ownerName", skull.getOwnerProfile().name().orElse(null));
                skullJson.addProperty("partialProfile", skull.getOwnerProfile().partialProfile().toString());
            }
            json.add("skull", skullJson);
        }

        return json;
    }

    private static JsonObject entityJson(Entity entity) {
        JsonObject json = new JsonObject();
        json.addProperty("target", "entity");
        json.addProperty("id", entity.getId());
        json.addProperty("uuid", entity.getStringUUID());
        json.addProperty("type", String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())));
        json.addProperty("class", entity.getClass().getName());
        json.addProperty("name", entity.getName().getString());
        json.addProperty("displayName", entity.getDisplayName().getString());
        json.addProperty("customName", entity.getCustomName() == null ? null : entity.getCustomName().getString());
        json.add("position", vecJson(entity.position()));
        json.add("deltaMovement", vecJson(entity.getDeltaMovement()));
        json.add("boundingBox", aabbJson(entity.getBoundingBox()));
        json.addProperty("pose", entity.getPose().name());
        json.addProperty("alive", entity.isAlive());
        json.addProperty("onGround", entity.onGround());
        json.addProperty("silent", entity.isSilent());
        json.addProperty("noGravity", entity.isNoGravity());
        json.addProperty("invisible", entity.isInvisible());
        json.addProperty("pickable", entity.isPickable());
        json.addProperty("airSupply", entity.getAirSupply());
        json.addProperty("remainingFireTicks", entity.getRemainingFireTicks());
        json.addProperty("removalReason", String.valueOf(entity.getRemovalReason()));

        JsonArray tags = new JsonArray();
        entity.entityTags().stream().sorted().forEach(tags::add);
        json.add("tags", tags);

        if (entity instanceof LivingEntity living) {
            JsonObject livingJson = new JsonObject();
            livingJson.addProperty("health", round(living.getHealth()));
            livingJson.addProperty("maxHealth", round(living.getMaxHealth()));
            livingJson.addProperty("absorption", round(living.getAbsorptionAmount()));
            livingJson.addProperty("armor", living.getArmorValue());
            livingJson.addProperty("mainHandItem", living.getMainHandItem().toString());
            livingJson.addProperty("offHandItem", living.getOffhandItem().toString());
            json.add("living", livingJson);
        }

        return json;
    }

    private static JsonObject vecJson(Vec3 vec) {
        JsonObject json = new JsonObject();
        json.addProperty("x", round(vec.x));
        json.addProperty("y", round(vec.y));
        json.addProperty("z", round(vec.z));
        return json;
    }

    private static JsonObject blockPosJson(BlockPos pos) {
        JsonObject json = new JsonObject();
        json.addProperty("x", pos.getX());
        json.addProperty("y", pos.getY());
        json.addProperty("z", pos.getZ());
        return json;
    }

    private static JsonObject aabbJson(AABB box) {
        JsonObject json = new JsonObject();
        json.add("min", vecJson(new Vec3(box.minX, box.minY, box.minZ)));
        json.add("max", vecJson(new Vec3(box.maxX, box.maxY, box.maxZ)));
        json.addProperty("xSize", round(box.getXsize()));
        json.addProperty("ySize", round(box.getYsize()));
        json.addProperty("zSize", round(box.getZsize()));
        return json;
    }

    private static JsonObject stringJson(String value) {
        JsonObject json = new JsonObject();
        json.addProperty("string", value);
        return json;
    }

    private static double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private static void sendJson(Minecraft client, String label, JsonObject json) {
        String data = GSON.toJson(json);
        MutableComponent message = Component.literal("[SkyBlockESP] " + label + " data")
                .withStyle(ChatFormatting.AQUA)
                .append(Component.literal(" [hover/click to copy]").withStyle(ChatFormatting.GRAY));
        message.withStyle(style -> style
                .withHoverEvent(new HoverEvent.ShowText(Component.literal(data)))
                .withClickEvent(new ClickEvent.CopyToClipboard(data))
                .withInsertion(data));
        client.gui.getChat().addClientSystemMessage(message);
    }

    private static void sendInfo(Minecraft client, String text) {
        client.gui.getChat().addClientSystemMessage(Component.literal("[SkyBlockESP] " + text).withStyle(ChatFormatting.YELLOW));
    }

    private record GhostedBlock(BlockPos pos, BlockState state, BlockEntity blockEntity, long restoreAt) {
    }

    private record GhostedEntity(int id, UUID uuid, boolean wasInvisible, boolean wasCustomNameVisible, long restoreAt) {
    }
}
