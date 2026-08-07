package cc.rayen.skyblockesp.client;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfigScreen;
import cc.rayen.skyblockesp.client.debug.DebugInspector;
import cc.rayen.skyblockesp.client.feature.SafariEsp;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;

public class SkyBlockESPClient implements ClientModInitializer {
    private static boolean openConfigScreen;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("esp")
                    .executes(context -> openConfigScreen()));
            dispatcher.register(ClientCommands.literal("skyblockesp")
                    .executes(context -> openConfigScreen()));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigScreen) {
                openConfigScreen = false;
                client.setScreen(SkyBlockESPConfigScreen.create(client.screen));
            }
            DebugInspector.tick(client);
        });

        LevelRenderEvents.BEFORE_GIZMOS.register(context -> SafariEsp.render(Minecraft.getInstance()));
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            SafariEsp.handleUseBlock(level, hitResult);
            return InteractionResult.PASS;
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> SafariEsp.handleChatMessage(message.getString()));
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> SafariEsp.handleChatMessage(message.getString()));
    }

    private static int openConfigScreen() {
        openConfigScreen = true;
        return 1;
    }
}
