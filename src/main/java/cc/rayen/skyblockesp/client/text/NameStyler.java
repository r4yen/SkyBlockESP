package cc.rayen.skyblockesp.client.text;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public final class NameStyler {
    private static final int[] PALETTE_IRIS = gradient(new int[]{
            0xB8C5FF, 0xA89EFF, 0xD07DFA, 0xE7A6B8, 0xE3CC9E, 0xB6B0FF
    });
    private static final Replacement RAAAYEN = new Replacement(
            "raaayen",
            "❄ raaayen ❄",
            PALETTE_IRIS,
            25,
            2.0,
            0.16,
            true
    );

    private NameStyler() {
    }

    public static String replacePlain(String value) {
        String text = value == null ? "" : value;
        StringBuilder out = new StringBuilder(text.length() + 8);
        int cursor = 0;

        while (cursor < text.length()) {
            int index = findNextMatch(text, cursor);
            if (index < 0) {
                out.append(text.substring(cursor));
                break;
            }

            if (index > cursor) {
                out.append(text, cursor, index);
            }

            out.append(RAAAYEN.displayText);
            cursor = index + RAAAYEN.match.length();
        }

        return out.toString();
    }

    public static FormattedCharSequence styleSequence(FormattedCharSequence value) {
        if (value == null) {
            return FormattedCharSequence.EMPTY;
        }

        List<StyledCodePoint> glyphs = new ArrayList<>();
        StringBuilder plain = new StringBuilder();
        value.accept((ignored, style, codePoint) -> {
            glyphs.add(new StyledCodePoint(codePoint, style));
            plain.appendCodePoint(codePoint);
            return true;
        });

        if (glyphs.isEmpty()) {
            return value;
        }

        String text = plain.toString();
        if (findNextMatch(text, 0) < 0) {
            return value;
        }

        List<StyledCodePoint> out = new ArrayList<>(glyphs.size() + 8);
        int cursorChar = 0;
        int cursorGlyph = 0;

        while (cursorChar < text.length()) {
            int matchIndex = findNextMatch(text, cursorChar);
            if (matchIndex < 0) {
                appendOriginalGlyphs(out, glyphs, cursorGlyph, glyphs.size());
                break;
            }

            int matchGlyph = text.codePointCount(0, matchIndex);
            if (matchGlyph > cursorGlyph) {
                appendOriginalGlyphs(out, glyphs, cursorGlyph, matchGlyph);
            }

            Style baseStyle = matchGlyph < glyphs.size() ? glyphs.get(matchGlyph).style : Style.EMPTY;
            appendAnimatedReplacementGlyphs(out, baseStyle);

            cursorChar = matchIndex + RAAAYEN.match.length();
            cursorGlyph = text.codePointCount(0, cursorChar);
        }

        return sink -> {
            int index = 0;
            for (StyledCodePoint glyph : out) {
                if (!sink.accept(index, glyph.style, glyph.codePoint)) {
                    return false;
                }
                index += Character.charCount(glyph.codePoint);
            }
            return true;
        };
    }

    private static int findNextMatch(String text, int startIndex) {
        int searchFrom = Math.max(0, startIndex);
        while (searchFrom < text.length()) {
            int index = text.indexOf(RAAAYEN.match, searchFrom);
            if (index < 0) {
                return -1;
            }

            if (!isAlreadyDecorated(text, index)) {
                return index;
            }
            searchFrom = index + 1;
        }
        return -1;
    }

    private static boolean isAlreadyDecorated(String text, int index) {
        int matchInsideDisplay = RAAAYEN.displayText.indexOf(RAAAYEN.match);
        String prefix = RAAAYEN.displayText.substring(0, matchInsideDisplay);
        String suffix = RAAAYEN.displayText.substring(matchInsideDisplay + RAAAYEN.match.length());

        int prefixStart = index - prefix.length();
        int suffixStart = index + RAAAYEN.match.length();
        if (prefixStart < 0 || suffixStart + suffix.length() > text.length()) {
            return false;
        }

        return text.startsWith(prefix, prefixStart) && text.startsWith(suffix, suffixStart);
    }

    private static void appendOriginalGlyphs(List<StyledCodePoint> out, List<StyledCodePoint> source, int from, int toExclusive) {
        for (int i = from; i < toExclusive; i++) {
            out.add(source.get(i));
        }
    }

    private static void appendAnimatedReplacementGlyphs(List<StyledCodePoint> out, Style baseStyle) {
        double frameOffset = (double) System.currentTimeMillis() / Math.max(1, RAAAYEN.stepMs);
        int charIndex = 0;
        for (int i = 0; i < RAAAYEN.displayText.length(); ) {
            int cp = RAAAYEN.displayText.codePointAt(i);
            double position = charIndex * RAAAYEN.charStep + frameOffset * RAAAYEN.frameStep;
            int rgb = getAnimatedColor(RAAAYEN.colors, position);

            out.add(new StyledCodePoint(cp, baseStyle.withColor(TextColor.fromRgb(rgb)).withBold(RAAAYEN.bold)));

            i += Character.charCount(cp);
            charIndex++;
        }
    }

    private static int getAnimatedColor(int[] colors, double position) {
        if (colors.length == 0) {
            return 0xFFFFFF;
        }
        if (colors.length == 1) {
            return colors[0];
        }

        double wrapped = position % colors.length;
        if (wrapped < 0) {
            wrapped += colors.length;
        }

        int baseIndex = (int) Math.floor(wrapped);
        int nextIndex = (baseIndex + 1) % colors.length;
        return mix(colors[baseIndex], colors[nextIndex], wrapped - baseIndex);
    }

    private static int[] gradient(int[] anchors) {
        int stepsPerSegment = 6;
        if (anchors.length == 0) {
            return new int[]{0xFFFFFF};
        }
        if (anchors.length == 1) {
            return anchors.clone();
        }

        int[] forward = new int[(anchors.length - 1) * stepsPerSegment + 1];
        int index = 0;
        for (int i = 0; i < anchors.length - 1; i++) {
            for (int step = 0; step < stepsPerSegment; step++) {
                forward[index++] = mix(anchors[i], anchors[i + 1], step / (double) stepsPerSegment);
            }
        }
        forward[index] = anchors[anchors.length - 1];

        int[] out = new int[forward.length * 2 - 1];
        System.arraycopy(forward, 0, out, 0, forward.length);
        int outIndex = forward.length;
        for (int i = forward.length - 2; i >= 0; i--) {
            out[outIndex++] = forward[i];
        }
        return out;
    }

    private static int mix(int left, int right, double t) {
        double clamped = Math.clamp(t, 0.0, 1.0);
        int r = clamp((int) Math.round(((left >> 16) & 0xFF) + (((right >> 16) & 0xFF) - ((left >> 16) & 0xFF)) * clamped));
        int g = clamp((int) Math.round(((left >> 8) & 0xFF) + (((right >> 8) & 0xFF) - ((left >> 8) & 0xFF)) * clamped));
        int b = clamp((int) Math.round((left & 0xFF) + ((right & 0xFF) - (left & 0xFF)) * clamped));
        return (r << 16) | (g << 8) | b;
    }

    private static int clamp(int value) {
        return Math.clamp(value, 0, 255);
    }

    private record Replacement(
            String match,
            String displayText,
            int[] colors,
            int stepMs,
            double charStep,
            double frameStep,
            boolean bold
    ) {
    }

    private record StyledCodePoint(int codePoint, Style style) {
    }
}
