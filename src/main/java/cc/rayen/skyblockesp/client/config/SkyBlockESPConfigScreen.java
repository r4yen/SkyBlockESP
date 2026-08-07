package cc.rayen.skyblockesp.client.config;

import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.gui.GuiElementComponent;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.platform.MoulConfigScreenComponent;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SkyBlockESPConfigScreen {
    private SkyBlockESPConfigScreen() {
    }

    public static Screen create(Screen previousScreen) {
        SkyBlockESPConfig config = SkyBlockESPConfig.INSTANCE;
        MoulConfigProcessor<SkyBlockESPConfig> processor = MoulConfigProcessor.withDefaults(config);
        ConfigProcessorDriver driver = new ConfigProcessorDriver(processor);
        driver.processConfig(config);

        MoulConfigEditor<SkyBlockESPConfig> editor = new MoulConfigEditor<>(processor);
        editor.setWide(true);

        return new MoulConfigScreenComponent(
                Component.literal("SkyBlockESP"),
                new GuiContext(new GuiElementComponent(editor)),
                previousScreen
        );
    }
}
