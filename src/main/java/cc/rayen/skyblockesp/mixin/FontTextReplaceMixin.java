package cc.rayen.skyblockesp.mixin;

import cc.rayen.skyblockesp.client.text.NameStyler;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Font.class)
public abstract class FontTextReplaceMixin {
    @ModifyVariable(
            method = "prepareText(Ljava/lang/String;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private String skyblockesp$replacePrepareString(String text) {
        return NameStyler.replacePlain(text);
    }

    @ModifyVariable(
            method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private FormattedCharSequence skyblockesp$replacePrepareSequence(FormattedCharSequence text) {
        return NameStyler.styleSequence(text);
    }

    @ModifyVariable(
            method = "width(Ljava/lang/String;)I",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private String skyblockesp$replaceWidthString(String text) {
        return NameStyler.replacePlain(text);
    }

    @ModifyVariable(
            method = "width(Lnet/minecraft/util/FormattedCharSequence;)I",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private FormattedCharSequence skyblockesp$replaceWidthSequence(FormattedCharSequence text) {
        return NameStyler.styleSequence(text);
    }
}
