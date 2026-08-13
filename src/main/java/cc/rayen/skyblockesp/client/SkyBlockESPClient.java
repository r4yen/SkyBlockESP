package cc.rayen.skyblockesp.client;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import cc.rayen.skyblockesp.client.config.SkyBlockESPConfigScreen;
import cc.rayen.skyblockesp.client.debug.DebugInspector;
import cc.rayen.skyblockesp.client.feature.FairySoulPositions;
import cc.rayen.skyblockesp.client.feature.categories.BackwaterBayouESP;
import cc.rayen.skyblockesp.client.feature.categories.CrimsonIsleESP;
import cc.rayen.skyblockesp.client.feature.categories.CrystalHollowsESP;
import cc.rayen.skyblockesp.client.feature.categories.DeepCavernsESP;
import cc.rayen.skyblockesp.client.feature.categories.DungeonHubESP;
import cc.rayen.skyblockesp.client.feature.categories.DwarvenMinesESP;
import cc.rayen.skyblockesp.client.feature.categories.GardenESP;
import cc.rayen.skyblockesp.client.feature.categories.GoldMineESP;
import cc.rayen.skyblockesp.client.feature.categories.HubESP;
import cc.rayen.skyblockesp.client.feature.categories.JerrysWorkshopESP;
import cc.rayen.skyblockesp.client.feature.categories.LotusAtollESP;
import cc.rayen.skyblockesp.client.feature.categories.MoongladeMarshESP;
import cc.rayen.skyblockesp.client.feature.categories.PrivateIslandESP;
import cc.rayen.skyblockesp.client.feature.categories.SafariESP;
import cc.rayen.skyblockesp.client.feature.categories.SpidersDenESP;
import cc.rayen.skyblockesp.client.feature.categories.TheEndESP;
import cc.rayen.skyblockesp.client.feature.categories.TheFarmingIslandsESP;
import cc.rayen.skyblockesp.client.feature.categories.TheParkESP;
import cc.rayen.skyblockesp.client.feature.categories.TheRiftESP;
import cc.rayen.skyblockesp.client.feature.categories.TorrhusCanyonESP;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
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
        FairySoulPositions.preload();

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
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> SkyBlockESPConfig.INSTANCE.saveNow());

        LevelRenderEvents.BEFORE_GIZMOS.register(context -> {
            Minecraft client = Minecraft.getInstance();
            HubESP.render(client);
            PrivateIslandESP.render(client);
            GardenESP.render(client);
            TheRiftESP.render(client);
            DungeonHubESP.render(client);
            TheFarmingIslandsESP.render(client);
            TheParkESP.render(client);
            MoongladeMarshESP.render(client);
            TorrhusCanyonESP.render(client);
            SafariESP.render(client);
            GoldMineESP.render(client);
            DeepCavernsESP.render(client);
            DwarvenMinesESP.render(client);
            CrystalHollowsESP.render(client);
            SpidersDenESP.render(client);
            TheEndESP.render(client);
            CrimsonIsleESP.render(client);
            BackwaterBayouESP.render(client);
            LotusAtollESP.render(client);
            JerrysWorkshopESP.render(client);
        });
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            SafariESP.handleUseBlock(level, hitResult);
            return InteractionResult.PASS;
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> SafariESP.handleChatMessage(message.getString()));
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> SafariESP.handleChatMessage(message.getString()));
    }

    private static int openConfigScreen() {
        openConfigScreen = true;
        return 1;
    }
}
