package cc.rayen.skyblockesp.client.feature;

import cc.rayen.skyblockesp.client.config.SkyBlockESPConfig;
import io.github.notenoughupdates.moulconfig.ChromaColour;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class ESPMarkers {
    public static final int WHITE_STROKE_COLOR = 0xFFFFFFFF;
    public static final int WHITE_FILL_COLOR = 0x14FFFFFF;
    public static final int WHITE_TEXT_COLOR = 0xFFFFFFFF;
    private static final int FILL_ALPHA = 0x14;
    private static final float TITLE_SCALE = 0.6f;

    private ESPMarkers() {
    }

    public static void renderBlockMarker(BlockPos pos, String label, int strokeColor, int fillColor, int textColor) {
        AABB box = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
        renderBoxMarker(box, Vec3.atCenterOf(pos), label, strokeColor, fillColor, textColor);
    }

    public static int colorA() {
        return color(SkyBlockESPConfig.INSTANCE.general.colorA);
    }

    public static int colorAFill() {
        return fillColor(colorA());
    }

    public static int colorB() {
        return color(SkyBlockESPConfig.INSTANCE.general.colorB);
    }

    public static int colorBFill() {
        return fillColor(colorB());
    }

    public static int colorC() {
        return color(SkyBlockESPConfig.INSTANCE.general.colorC);
    }

    public static int colorCFill() {
        return fillColor(colorC());
    }

    public static void renderBoxMarker(AABB box, Vec3 labelPosition, String label, int strokeColor, int fillColor, int textColor) {
        Gizmos.cuboid(box, GizmoStyle.strokeAndFill(strokeColor, 2.0f, fillColor))
                .setAlwaysOnTop()
                .persistForMillis(75);
        renderTitle(label, labelPosition, textColor);
    }

    public static void renderTitle(String label, Vec3 position, int textColor) {
        if (!SkyBlockESPConfig.INSTANCE.debug.showTitles) {
            return;
        }

        Gizmos.billboardText(label, position, TextGizmo.Style.forColorAndCentered(textColor).withScale(TITLE_SCALE))
                .setAlwaysOnTop()
                .persistForMillis(75);
    }

    private static int color(String rawColor) {
        try {
            return ChromaColour.specialToChromaRGB(rawColor);
        } catch (RuntimeException exception) {
            return WHITE_STROKE_COLOR;
        }
    }

    private static int fillColor(int argb) {
        return (FILL_ALPHA << 24) | (argb & 0x00FFFFFF);
    }
}
