package cc.rayen.skyblockesp.client.island;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;

public final class CurrentIsland {
    private CurrentIsland() {
    }

    public static boolean inSkyBlock() {
        Objective sidebar = getScoreboard(DisplaySlot.SIDEBAR);
        return sidebar != null && sidebar.getDisplayName().getString().trim().equalsIgnoreCase("SKYBLOCK");
    }

    public static boolean isIsland(String islandName) {
        String currentIsland = getIsland();
        return currentIsland != null && currentIsland.equalsIgnoreCase(islandName);
    }

    public static String getIsland() {
        if (!inSkyBlock()) {
            return null;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.getConnection() == null) {
            return null;
        }

        for (PlayerInfo playerInfo : client.getConnection().getOnlinePlayers()) {
            Component displayName = playerInfo.getTabListDisplayName();
            if (displayName == null) {
                continue;
            }

            String text = displayName.getString();
            if (text.startsWith("Area:")) {
                String island = text.substring("Area:".length()).trim();
                if (!island.isEmpty()) {
                    return island;
                }
            }
        }

        return null;
    }

    private static Objective getScoreboard(DisplaySlot slot) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return null;
        }

        return client.level.getScoreboard().getDisplayObjective(slot);
    }
}
