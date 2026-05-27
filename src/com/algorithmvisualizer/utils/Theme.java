package com.algorithmvisualizer.utils;

import java.awt.*;

/**
 * Centralized design system — all colors, fonts, and spacing
 * defined in one place. Ensures visual consistency throughout the UI.
 */
public final class Theme {

    private Theme() {}

    // ---- Background palette ----
    public static final Color BG_DARKEST  = new Color(9,  11, 16);
    public static final Color BG_DARK     = new Color(13, 17, 23);
    public static final Color BG_MEDIUM   = new Color(22, 27, 34);
    public static final Color BG_CARD     = new Color(30, 37, 48);
    public static final Color BG_HOVER    = new Color(40, 50, 64);

    // ---- Accent colors ----
    public static final Color ACCENT_BLUE    = new Color(56,  139, 253);
    public static final Color ACCENT_CYAN    = new Color(79,  193, 255);
    public static final Color ACCENT_GREEN   = new Color(63,  185, 80);
    public static final Color ACCENT_YELLOW  = new Color(210, 153, 34);
    public static final Color ACCENT_ORANGE  = new Color(255, 140, 0);
    public static final Color ACCENT_RED     = new Color(248, 81,  73);
    public static final Color ACCENT_PURPLE  = new Color(139, 92,  246);

    // ---- Step-type bar colors ----
    public static final Color BAR_DEFAULT  = new Color(70,  100, 150);
    public static final Color BAR_COMPARE  = ACCENT_YELLOW;
    public static final Color BAR_SWAP     = ACCENT_RED;
    public static final Color BAR_PIVOT    = ACCENT_ORANGE;
    public static final Color BAR_SORTED   = ACCENT_GREEN;
    public static final Color BAR_SET      = ACCENT_CYAN;
    public static final Color BAR_HIGHLIGHT = ACCENT_PURPLE;

    // ---- Text colors ----
    public static final Color TEXT_PRIMARY   = new Color(230, 237, 243);
    public static final Color TEXT_SECONDARY = new Color(139, 148, 158);
    public static final Color TEXT_MUTED     = new Color(88,  96,  105);
    public static final Color TEXT_ACCENT    = ACCENT_BLUE;

    // ---- Border colors ----
    public static final Color BORDER_DEFAULT = new Color(48,  54,  61);
    public static final Color BORDER_ACTIVE  = new Color(56,  139, 253);

    // ---- Fonts ----
    public static final Font FONT_MONO_LARGE  = new Font(Font.MONOSPACED, Font.BOLD,  15);
    public static final Font FONT_MONO_MEDIUM = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    public static final Font FONT_MONO_SMALL  = new Font(Font.MONOSPACED, Font.PLAIN, 11);
    public static final Font FONT_SANS_TITLE  = new Font(Font.SANS_SERIF, Font.BOLD,  18);
    public static final Font FONT_SANS_LARGE  = new Font(Font.SANS_SERIF, Font.BOLD,  14);
    public static final Font FONT_SANS_MEDIUM = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font FONT_SANS_SMALL  = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    // ---- Dimensions ----
    public static final int  CORNER_RADIUS    = 8;
    public static final int  BAR_GAP          = 2;

    // ---- Gradient helpers ----
    public static GradientPaint barGradient(int x, int y, int height, Color base) {
        return new GradientPaint(
                x, y,          base,
                x, y + height, base.darker()
        );
    }

    public static Color stepTypeColor(com.algorithmvisualizer.models.AlgorithmStep.StepType type) {
        return switch (type) {
            case COMPARE   -> BAR_COMPARE;
            case SWAP      -> BAR_SWAP;
            case PIVOT     -> BAR_PIVOT;
            case SORTED    -> BAR_SORTED;
            case SET       -> BAR_SET;
            case HIGHLIGHT, FOUND, PATH, VISITED, FRONTIER -> BAR_HIGHLIGHT;
            default        -> BAR_DEFAULT;
        };
    }
}