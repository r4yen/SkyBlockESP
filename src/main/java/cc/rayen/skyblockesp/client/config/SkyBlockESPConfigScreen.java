package cc.rayen.skyblockesp.client.config;

import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOrder;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.gui.GuiElementComponent;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.platform.MoulConfigScreenComponent;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;

public final class SkyBlockESPConfigScreen {
    private SkyBlockESPConfigScreen() {
    }

    public static Screen create(Screen previousScreen) {
        SkyBlockESPConfig config = SkyBlockESPConfig.INSTANCE;
        Config configView = config.general.debugFunctions ? new FullConfigView(config) : new BasicConfigView(config);
        MoulConfigProcessor<Config> processor = MoulConfigProcessor.withDefaults(configView);
        ConfigProcessorDriver driver = new ConfigProcessorDriver(processor);
        driver.processConfig(configView);

        MoulConfigEditor<Config> editor = new MoulConfigEditor<>(processor);
        editor.setWide(true);

        return new MoulConfigScreenComponent(
                Component.literal("SkyBlockESP"),
                new GuiContext(new GuiElementComponent(editor)),
                previousScreen
        ) {
            @Override
            public void removed() {
                config.saveNow();
                super.removed();
            }
        };
    }

    public static class BasicConfigView extends Config {
        private final SkyBlockESPConfig backing;

        @Category(name = "General", desc = "General ESP settings")
        @ConfigOrder(0)
        public SkyBlockESPConfig.General general;

        @Category(name = "Private Island", desc = "Private Island ESP settings")
        @ConfigOrder(1)
        public SkyBlockESPConfig.PrivateIsland privateIsland;

        @Category(name = "Garden", desc = "Garden ESP settings")
        @ConfigOrder(2)
        public SkyBlockESPConfig.EmptyIsland garden;

        @Category(name = "Hub", desc = "Hub ESP settings")
        @ConfigOrder(3)
        public SkyBlockESPConfig.Hub hub;

        @Category(name = "The Rift", desc = "The Rift ESP settings")
        @ConfigOrder(4)
        public SkyBlockESPConfig.EmptyIsland theRift;

        @Category(name = "Dungeon Hub", desc = "Dungeon Hub ESP settings")
        @ConfigOrder(5)
        public SkyBlockESPConfig.FairySoulIsland dungeonHub;

        @Category(name = "The Farming Islands", desc = "The Farming Islands ESP settings")
        @ConfigOrder(6)
        public SkyBlockESPConfig.FairySoulIsland theFarmingIslands;

        @Category(name = "The Park", desc = "The Park ESP settings")
        @ConfigOrder(7)
        public SkyBlockESPConfig.FairySoulIsland thePark;

        @Category(name = "Moonglade Marsh", desc = "Moonglade Marsh ESP settings")
        @ConfigOrder(8)
        public SkyBlockESPConfig.FairySoulFloorDropIsland moongladeMarsh;

        @Category(name = "Torrhus Canyon", desc = "Torrhus Canyon ESP settings")
        @ConfigOrder(9)
        public SkyBlockESPConfig.FairySoulFloorDropIsland torrhusCanyon;

        @Category(name = "Safari", desc = "Safari ESP settings")
        @ConfigOrder(10)
        public SkyBlockESPConfig.Desert desert;

        @Category(name = "Gold Mine", desc = "Gold Mine ESP settings")
        @ConfigOrder(11)
        public SkyBlockESPConfig.FairySoulIsland goldMine;

        @Category(name = "Deep Caverns", desc = "Deep Caverns ESP settings")
        @ConfigOrder(12)
        public SkyBlockESPConfig.FairySoulIsland deepCaverns;

        @Category(name = "Dwarven Mines", desc = "Dwarven Mines ESP settings")
        @ConfigOrder(13)
        public SkyBlockESPConfig.FairySoulIsland dwarvenMines;

        @Category(name = "Crystal Hollows", desc = "Crystal Hollows ESP settings")
        @ConfigOrder(14)
        public SkyBlockESPConfig.EmptyIsland crystalHollows;

        @Category(name = "Spider's Den", desc = "Spider's Den ESP settings")
        @ConfigOrder(15)
        public SkyBlockESPConfig.FairySoulIsland spidersDen;

        @Category(name = "The End", desc = "The End ESP settings")
        @ConfigOrder(16)
        public SkyBlockESPConfig.FairySoulIsland theEnd;

        @Category(name = "Crimson Isle", desc = "Crimson Isle ESP settings")
        @ConfigOrder(17)
        public SkyBlockESPConfig.FairySoulIsland crimsonIsle;

        @Category(name = "Backwater Bayou", desc = "Backwater Bayou ESP settings")
        @ConfigOrder(18)
        public SkyBlockESPConfig.FairySoulIsland backwaterBayou;

        @Category(name = "Lotus Atoll", desc = "Lotus Atoll ESP settings")
        @ConfigOrder(19)
        public SkyBlockESPConfig.FairySoulIsland lotusAtoll;

        @Category(name = "Jerry's Workshop", desc = "Jerry's Workshop ESP settings")
        @ConfigOrder(20)
        public SkyBlockESPConfig.FairySoulIsland jerrysWorkshop;

        public BasicConfigView(SkyBlockESPConfig backing) {
            this.backing = backing;
            this.general = backing.general;
            this.privateIsland = backing.privateIsland;
            this.garden = backing.garden;
            this.hub = backing.hub;
            this.theRift = backing.theRift;
            this.dungeonHub = backing.dungeonHub;
            this.theFarmingIslands = backing.theFarmingIslands;
            this.thePark = backing.thePark;
            this.moongladeMarsh = backing.moongladeMarsh;
            this.torrhusCanyon = backing.torrhusCanyon;
            this.desert = backing.desert;
            this.goldMine = backing.goldMine;
            this.deepCaverns = backing.deepCaverns;
            this.dwarvenMines = backing.dwarvenMines;
            this.crystalHollows = backing.crystalHollows;
            this.spidersDen = backing.spidersDen;
            this.theEnd = backing.theEnd;
            this.crimsonIsle = backing.crimsonIsle;
            this.backwaterBayou = backing.backwaterBayou;
            this.lotusAtoll = backing.lotusAtoll;
            this.jerrysWorkshop = backing.jerrysWorkshop;
        }

        @Override
        public StructuredText getTitle() {
            return backing.getTitle();
        }

        @Override
        public void executeRunnable(int runnableId) {
            backing.executeRunnable(runnableId);
        }

        @Override
        public void saveNow() {
            backing.saveNow();
        }
    }

    public static final class FullConfigView extends BasicConfigView {
        @Category(name = "Debug", desc = "Debug settings")
        public SkyBlockESPConfig.Debug debug;

        public FullConfigView(SkyBlockESPConfig backing) {
            super(backing);
            this.debug = backing.debug;
        }
    }
}
