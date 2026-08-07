package cc.rayen.skyblockesp.client.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import io.github.notenoughupdates.moulconfig.annotations.ConfigAccordionId;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorAccordion;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;

public class SkyBlockESPConfig extends Config {
    public static final SkyBlockESPConfig INSTANCE = SkyBlockESPConfigStore.load();
    private static final int ENABLE_ALL_CAVERN_ENTITIES = 1;
    private static final int DISABLE_ALL_CAVERN_ENTITIES = 2;
    private static final int ENABLE_ALL_FOREST_ENTITIES = 3;
    private static final int DISABLE_ALL_FOREST_ENTITIES = 4;
    private static final int ENABLE_ALL_HAUNTED_ENTITIES = 5;
    private static final int DISABLE_ALL_HAUNTED_ENTITIES = 6;
    private static final int ENABLE_ALL_ICY_ENTITIES = 7;
    private static final int DISABLE_ALL_ICY_ENTITIES = 8;

    @Expose
    @Category(name = "General", desc = "General ESP settings")
    public General general = new General();

    @Expose
    @Category(name = "Safari", desc = "Safari ESP settings")
    public Desert desert = new Desert();

    @Expose
    @Category(name = "Debug", desc = "Debug settings")
    public Debug debug = new Debug();

    @Override
    public StructuredText getTitle() {
        return StructuredText.of("SkyBlockESP");
    }

    @Override
    public void executeRunnable(int runnableId) {
        switch (runnableId) {
            case ENABLE_ALL_CAVERN_ENTITIES -> desert.setAllCavernEntities(true);
            case DISABLE_ALL_CAVERN_ENTITIES -> desert.setAllCavernEntities(false);
            case ENABLE_ALL_FOREST_ENTITIES -> desert.setAllForestEntities(true);
            case DISABLE_ALL_FOREST_ENTITIES -> desert.setAllForestEntities(false);
            case ENABLE_ALL_HAUNTED_ENTITIES -> desert.setAllHauntedEntities(true);
            case DISABLE_ALL_HAUNTED_ENTITIES -> desert.setAllHauntedEntities(false);
            case ENABLE_ALL_ICY_ENTITIES -> desert.setAllIcyEntities(true);
            case DISABLE_ALL_ICY_ENTITIES -> desert.setAllIcyEntities(false);
            default -> {
                return;
            }
        }
        saveNow();
    }

    public static class General {
        @ConfigOption(name = "Debug Functions", desc = "Enable debug-only options and keybinds.")
        @ConfigEditorAccordion(id = 1)
        public boolean debugFunctions = false;

        @Expose
        @ConfigOption(name = "Show Titles", desc = "Show labels on ESP markers.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 1)
        public boolean showTitles = false;
    }

    public static class Desert {
        @Expose
        @ConfigOption(name = "Fairy Souls", desc = "Show ESP markers for Safari Fairy Souls.")
        @ConfigEditorBoolean
        public boolean fairySouls = false;

        @Expose
        @ConfigOption(name = "Bells", desc = "Show ESP markers for Safari bells.")
        @ConfigEditorBoolean
        public boolean bells = false;

        @Expose
        @ConfigOption(name = "Throw Line", desc = "Show the Critter Capsule throw trajectory.")
        @ConfigEditorBoolean
        public boolean throwLine = false;

        @ConfigOption(name = "Cavern Biome", desc = "")
        @ConfigEditorAccordion(id = 1)
        public boolean cavernBiome = false;

        @Expose
        @ConfigOption(name = "Floor Drops", desc = "Show ESP markers for floor drops in the Cavern biome.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 1)
        public boolean cavernFloorDrops = false;

        @Expose
        @ConfigOption(name = "Rockmite's Home", desc = "Show ESP markers for Rockmite homes.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 1)
        public boolean rockmiteHome = false;

        @ConfigOption(name = "Entities", desc = "")
        @ConfigEditorAccordion(id = 10)
        @ConfigAccordionId(id = 1)
        public boolean cavernEntities = false;

        @ConfigOption(name = "All Entities", desc = "Enable every Cavern biome entity ESP.")
        @ConfigEditorButton(runnableId = ENABLE_ALL_CAVERN_ENTITIES, buttonText = "Enable")
        @ConfigAccordionId(id = 10)
        public int enableAllCavernEntities = 0;

        @ConfigOption(name = "All Entities", desc = "Disable every Cavern biome entity ESP.")
        @ConfigEditorButton(runnableId = DISABLE_ALL_CAVERN_ENTITIES, buttonText = "Disable")
        @ConfigAccordionId(id = 10)
        public int disableAllCavernEntities = 0;

        @Expose
        @ConfigOption(name = "Cavernfish", desc = "Show ESP markers for Cavernfish.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean cavernfish = false;

        @Expose
        @ConfigOption(name = "Flitter", desc = "Show ESP markers for Flitters.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean flitter = false;

        @Expose
        @ConfigOption(name = "Shyworm", desc = "Show ESP markers for Shyworms.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean shyworm = false;

        @Expose
        @ConfigOption(name = "Driftling", desc = "Show ESP markers for Driftlings.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean driftling = false;

        @Expose
        @ConfigOption(name = "Chuckwalla", desc = "Show ESP markers for invisible Chuckwallas.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean chuckwalla = false;

        @Expose
        @ConfigOption(name = "Rockmite", desc = "Show ESP markers for Rockmites.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean rockmite = false;

        @Expose
        @ConfigOption(name = "Scrappy", desc = "Show ESP markers for Scrappies.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean scrappy = false;

        @Expose
        @ConfigOption(name = "Snoozle", desc = "Show ESP markers for Snoozles.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean snoozle = false;

        @Expose
        @ConfigOption(name = "Gemzie", desc = "Show ESP markers for Gemzies.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 10)
        public boolean gemzie = false;

        @ConfigOption(name = "Forest Biome", desc = "")
        @ConfigEditorAccordion(id = 2)
        public boolean forestBiome = false;

        @Expose
        @ConfigOption(name = "Floor Drops", desc = "Show ESP markers for floor drops in the Forest biome.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 2)
        public boolean forestFloorDrops = false;

        @Expose
        @ConfigOption(name = "Bee Nest", desc = "Show ESP markers for non-empty Bee Nests.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 2)
        public boolean forestBeeNest = false;

        @Expose
        @ConfigOption(name = "Birdfeeder", desc = "Show the Forest Birdfeeder location.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 2)
        public boolean forestBirdfeeder = false;

        @ConfigOption(name = "Entities", desc = "")
        @ConfigEditorAccordion(id = 20)
        @ConfigAccordionId(id = 2)
        public boolean forestEntities = false;

        @ConfigOption(name = "All Entities", desc = "Enable every Forest biome entity ESP.")
        @ConfigEditorButton(runnableId = ENABLE_ALL_FOREST_ENTITIES, buttonText = "Enable")
        @ConfigAccordionId(id = 20)
        public int enableAllForestEntities = 0;

        @ConfigOption(name = "All Entities", desc = "Disable every Forest biome entity ESP.")
        @ConfigEditorButton(runnableId = DISABLE_ALL_FOREST_ENTITIES, buttonText = "Disable")
        @ConfigAccordionId(id = 20)
        public int disableAllForestEntities = 0;

        @Expose
        @ConfigOption(name = "Foxtrot", desc = "Show ESP markers for Foxtrots.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean foxtrot = false;

        @Expose
        @ConfigOption(name = "Bluebird", desc = "Show ESP markers for blue parrots.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean bluebird = false;

        @Expose
        @ConfigOption(name = "Honeybug", desc = "Show ESP markers for Honeybugs.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean honeybug = false;

        @Expose
        @ConfigOption(name = "Treefrog", desc = "Show ESP markers for Treefrogs.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean treefrog = false;

        @Expose
        @ConfigOption(name = "Woodchucker", desc = "Show ESP markers for Woodchuckers.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean woodchucker = false;

        @Expose
        @ConfigOption(name = "Fluffling", desc = "Show ESP markers for Flufflings.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean fluffling = false;

        @Expose
        @ConfigOption(name = "Hideonfloor", desc = "Show ESP markers for Hideonfloors.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean hideonfloor = false;

        @Expose
        @ConfigOption(name = "Paraket", desc = "Show ESP markers for green parrots.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean paraket = false;

        @Expose
        @ConfigOption(name = "Macaw", desc = "Show ESP markers for red parrots.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 20)
        public boolean macaw = false;

        @ConfigOption(name = "Haunted Biome", desc = "")
        @ConfigEditorAccordion(id = 3)
        public boolean hauntedBiome = false;

        @Expose
        @ConfigOption(name = "Floor Drops", desc = "Show ESP markers for floor drops in the Haunted biome.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 3)
        public boolean hauntedFloorDrops = false;

        @Expose
        @ConfigOption(name = "Beds", desc = "Show ESP markers for Haunted beds.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 3)
        public boolean hauntedBeds = false;

        @Expose
        @ConfigOption(name = "Doomspiral Candles", desc = "Show ESP markers for unlit Doomspiral candles.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 3)
        public boolean doomspiralCandles = false;

        @Expose
        @ConfigOption(name = "Shining Coin Water", desc = "Show ESP markers for Haunted water while carrying a Shining Coin.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 3)
        public boolean shiningCoinWater = false;

        @ConfigOption(name = "Entities", desc = "")
        @ConfigEditorAccordion(id = 30)
        @ConfigAccordionId(id = 3)
        public boolean hauntedEntities = false;

        @ConfigOption(name = "All Entities", desc = "Enable every Haunted biome entity ESP.")
        @ConfigEditorButton(runnableId = ENABLE_ALL_HAUNTED_ENTITIES, buttonText = "Enable")
        @ConfigAccordionId(id = 30)
        public int enableAllHauntedEntities = 0;

        @ConfigOption(name = "All Entities", desc = "Disable every Haunted biome entity ESP.")
        @ConfigEditorButton(runnableId = DISABLE_ALL_HAUNTED_ENTITIES, buttonText = "Disable")
        @ConfigAccordionId(id = 30)
        public int disableAllHauntedEntities = 0;

        @Expose
        @ConfigOption(name = "Areita", desc = "Show ESP markers for Areitas.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean areita = false;

        @Expose
        @ConfigOption(name = "Bloodbat", desc = "Show ESP markers for Bloodbats.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean bloodbat = false;

        @Expose
        @ConfigOption(name = "Duplico", desc = "Show ESP markers for Duplicos.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean duplico = false;

        @Expose
        @ConfigOption(name = "Gazer", desc = "Show ESP markers for Gazers.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean gazer = false;

        @Expose
        @ConfigOption(name = "Litterbug", desc = "Show ESP markers for Litterbugs.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean litterbug = false;

        @Expose
        @ConfigOption(name = "Solsnatcher", desc = "Show ESP markers for Solsnatchers.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean solsnatcher = false;

        @Expose
        @ConfigOption(name = "Gimmiegold", desc = "Show ESP markers for Gimmiegolds.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean gimmiegold = false;

        @Expose
        @ConfigOption(name = "Hideonwall", desc = "Show ESP markers for Hideonwalls.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean hideonwall = false;

        @Expose
        @ConfigOption(name = "Hideyho", desc = "Show ESP markers for Hideyho.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean hideyho = false;

        @Expose
        @ConfigOption(name = "Doomspiral", desc = "Show ESP markers for Doomspirals.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 30)
        public boolean doomspiral = false;

        @ConfigOption(name = "Icy Biome", desc = "")
        @ConfigEditorAccordion(id = 4)
        public boolean icyBiome = false;

        @Expose
        @ConfigOption(name = "Floor Drops", desc = "Show ESP markers for floor drops in the Icy biome.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 4)
        public boolean icyFloorDrops = false;

        @ConfigOption(name = "Entities", desc = "")
        @ConfigEditorAccordion(id = 40)
        @ConfigAccordionId(id = 4)
        public boolean icyEntities = false;

        @ConfigOption(name = "All Entities", desc = "Enable every Icy biome entity ESP.")
        @ConfigEditorButton(runnableId = ENABLE_ALL_ICY_ENTITIES, buttonText = "Enable")
        @ConfigAccordionId(id = 40)
        public int enableAllIcyEntities = 0;

        @ConfigOption(name = "All Entities", desc = "Disable every Icy biome entity ESP.")
        @ConfigEditorButton(runnableId = DISABLE_ALL_ICY_ENTITIES, buttonText = "Disable")
        @ConfigAccordionId(id = 40)
        public int disableAllIcyEntities = 0;

        @Expose
        @ConfigOption(name = "Strongarm", desc = "Show ESP markers for Strongarms.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean strongarm = false;

        @Expose
        @ConfigOption(name = "Tepid", desc = "Show ESP markers for Tepids.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean tepid = false;

        @Expose
        @ConfigOption(name = "Polaris", desc = "Show ESP markers for Polaris.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean polaris = false;

        @Expose
        @ConfigOption(name = "Shuddersquid", desc = "Show ESP markers for Shuddersquids.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean shuddersquid = false;

        @Expose
        @ConfigOption(name = "Billygoat", desc = "Show ESP markers for Billygoats.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean billygoat = false;

        @Expose
        @ConfigOption(name = "Mantis Shrimp", desc = "Show ESP markers for Mantis Shrimps.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean mantisShrimp = false;

        @Expose
        @ConfigOption(name = "Nozzlenose", desc = "Show ESP markers for Nozzlenoses.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean nozzlenose = false;

        @Expose
        @ConfigOption(name = "Troodon", desc = "Show ESP markers for Troodons.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean troodon = false;

        @Expose
        @ConfigOption(name = "Wumpa", desc = "Show ESP markers for Wumpas.")
        @ConfigEditorBoolean
        @ConfigAccordionId(id = 40)
        public boolean wumpa = false;

        private void setAllCavernEntities(boolean enabled) {
            cavernfish = enabled;
            flitter = enabled;
            shyworm = enabled;
            driftling = enabled;
            chuckwalla = enabled;
            rockmite = enabled;
            scrappy = enabled;
            snoozle = enabled;
            gemzie = enabled;
        }

        private void setAllForestEntities(boolean enabled) {
            foxtrot = enabled;
            bluebird = enabled;
            honeybug = enabled;
            treefrog = enabled;
            woodchucker = enabled;
            fluffling = enabled;
            hideonfloor = enabled;
            paraket = enabled;
            macaw = enabled;
        }

        private void setAllHauntedEntities(boolean enabled) {
            areita = enabled;
            bloodbat = enabled;
            duplico = enabled;
            gazer = enabled;
            litterbug = enabled;
            solsnatcher = enabled;
            gimmiegold = enabled;
            hideonwall = enabled;
            hideyho = enabled;
            doomspiral = enabled;
        }

        private void setAllIcyEntities(boolean enabled) {
            strongarm = enabled;
            tepid = enabled;
            polaris = enabled;
            shuddersquid = enabled;
            billygoat = enabled;
            mantisShrimp = enabled;
            nozzlenose = enabled;
            troodon = enabled;
            wumpa = enabled;
        }
    }

    public static class Debug {
        @Expose
        @ConfigOption(name = "Block Inspect Key", desc = "Print debug data for the targeted block.")
        @ConfigEditorKeybind(defaultKey = -1)
        public int blockInspectKey = -1;

        @Expose
        @ConfigOption(name = "Entity Inspect Key", desc = "Print debug data for the targeted entity.")
        @ConfigEditorKeybind(defaultKey = -1)
        public int entityInspectKey = -1;

        @Expose
        @ConfigOption(name = "Ghost Target Key", desc = "Hide the targeted block or entity clientside for 5 seconds.")
        @ConfigEditorKeybind(defaultKey = -1)
        public int ghostTargetKey = -1;
    }
}
