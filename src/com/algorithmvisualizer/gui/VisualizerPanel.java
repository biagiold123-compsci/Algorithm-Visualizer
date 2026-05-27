package com.algorithmvisualizer.gui;

import com.algorithmvisualizer.models.AlgorithmStep;
import com.algorithmvisualizer.utils.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * VisualizerPanel renders the array as an animated bar chart.
 *
 * Rendering highlights:
 * - Full antialiasing and text antialiasing via RenderingHints
 * - Per-bar color driven by step type (compare = yellow, swap = red, etc.)
 * - Rounded-top bars with gradient shading
 * - Value labels on bars when there is sufficient room
 * - Subtle grid lines and axis
 * - Background grid glow effect for highlighted bars
 */
public class VisualizerPanel extends JPanel {

    private int[]        array        = new int[0];
    private int[]        highlighted  = new int[0];
    private AlgorithmStep.StepType stepType = AlgorithmStep.StepType.COMPARE;
    private Set<Integer> sortedSet    = new HashSet<>();

    // Padding
    private static final int PAD_LEFT   = 48;
    private static final int PAD_RIGHT  = 24;
    private static final int PAD_TOP    = 32;
    private static final int PAD_BOTTOM = 40;

    public VisualizerPanel() {
        setBackground(Theme.BG_MEDIUM);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER_DEFAULT));
        setPreferredSize(new Dimension(960, 520));
    }

    /** Set the baseline array (reset sorted tracking). */
    public void setArray(int[] arr) {
        this.array    = arr.clone();
        this.highlighted = new int[0];
        this.sortedSet.clear();
        repaint();
    }

    /** Apply a step snapshot from the algorithm. */
    public void setStep(AlgorithmStep step) {
        this.array       = step.getArrayState();
        this.highlighted = step.getHighlightedIndices();
        this.stepType    = step.getType();

        // Accumulate sorted positions
        if (step.getType() == AlgorithmStep.StepType.SORTED) {
            for (int idx : highlighted) sortedSet.add(idx);
            // All sorted on final step
            if (step.getDescription().contains("fully sorted")) {
                for (int i = 0; i < array.length; i++) sortedSet.add(i);
            }
        } else if (step.getDescription().contains("fully sorted")) {
            for (int i = 0; i < array.length; i++) sortedSet.add(i);
        }
        repaint();
    }

    // ---------------------------------------------------------------- paint

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        applyRenderingHints(g2);

        int w = getWidth();
        int h = getHeight();

        drawBackground(g2, w, h);
        if (array.length == 0) { g2.dispose(); return; }

        int chartW = w - PAD_LEFT - PAD_RIGHT;
        int chartH = h - PAD_TOP  - PAD_BOTTOM;

        drawGridLines(g2, chartH, w);
        drawBars(g2, chartW, chartH);
        drawAxisLabels(g2, chartW, chartH, w, h);
        drawLegend(g2, w, h);

        g2.dispose();
    }

    private void applyRenderingHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE);
    }

    private void drawBackground(Graphics2D g2, int w, int h) {
        // Subtle vignette
        g2.setColor(Theme.BG_MEDIUM);
        g2.fillRect(0, 0, w, h);
    }

    private void drawGridLines(Graphics2D g2, int chartH, int totalW) {
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1f, new float[]{4f, 6f}, 0f));
        int levels = 5;
        for (int i = 1; i <= levels; i++) {
            int y = PAD_TOP + (int)(chartH * (1.0 - (double) i / levels));
            g2.setColor(new Color(255, 255, 255, 18));
            g2.drawLine(PAD_LEFT, y, totalW - PAD_RIGHT, y);

            // Y-axis value
            g2.setFont(Theme.FONT_MONO_SMALL);
            g2.setColor(Theme.TEXT_MUTED);
            String label = String.valueOf(i * 20);
            g2.drawString(label, PAD_LEFT - 30, y + 4);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawBars(Graphics2D g2, int chartW, int chartH) {
        int n = array.length;
        if (n == 0) return;

        Set<Integer> highlightSet = new HashSet<>();
        for (int idx : highlighted) highlightSet.add(idx);

        double barWidth = (double) chartW / n;
        int gap = n > 60 ? 1 : Theme.BAR_GAP;

        int maxVal = Arrays.stream(array).max().orElse(1);

        for (int i = 0; i < n; i++) {
            double barH   = (double) array[i] / maxVal * chartH;
            int    x      = PAD_LEFT + (int)(i * barWidth);
            int    y      = PAD_TOP  + (int)(chartH - barH);
            int    bw     = Math.max(1, (int) barWidth - gap);
            int    bh     = (int) barH;

            Color barColor = resolveColor(i, highlightSet);

            // Glow for highlighted bars
            if (highlightSet.contains(i) && bw > 3) {
                drawGlow(g2, x, y, bw, bh, barColor);
            }

            // Bar gradient
            GradientPaint gp = new GradientPaint(
                    x,      y,      barColor,
                    x,      y + bh, barColor.darker().darker()
            );
            g2.setPaint(gp);

            if (bw > 4) {
                g2.fill(new RoundRectangle2D.Double(x, y, bw, bh, 4, 4));
            } else {
                g2.fillRect(x, y, bw, bh);
            }

            // Top accent line for highlighted
            if (highlightSet.contains(i)) {
                g2.setColor(barColor.brighter());
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(x, y, x + bw, y);
                g2.setStroke(new BasicStroke(1f));
            }

            // Value label (only if bars are wide enough)
            if (bw >= 18 && bh > 14) {
                g2.setFont(Theme.FONT_MONO_SMALL);
                g2.setColor(new Color(255, 255, 255, 180));
                String val = String.valueOf(array[i]);
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (bw - fm.stringWidth(val)) / 2;
                int ty = y + 12;
                if (ty + 2 < PAD_TOP + chartH) g2.drawString(val, tx, ty);
            }
        }
    }

    private void drawGlow(Graphics2D g2, int x, int y, int w, int h, Color color) {
        int glowSize = 6;
        for (int g = glowSize; g > 0; g--) {
            float alpha = 0.06f * (glowSize - g + 1);
            g2.setColor(new Color(
                    color.getRed() / 255f,
                    color.getGreen() / 255f,
                    color.getBlue() / 255f,
                    alpha
            ));
            g2.fillRoundRect(x - g, y - g, w + g * 2, h + g * 2, 6, 6);
        }
    }

    private Color resolveColor(int index, Set<Integer> highlightSet) {
        if (sortedSet.contains(index)) return Theme.BAR_SORTED;
        if (highlightSet.contains(index)) return Theme.stepTypeColor(stepType);
        return Theme.BAR_DEFAULT;
    }

    private void drawAxisLabels(Graphics2D g2, int chartW, int chartH, int w, int h) {
        // X axis line
        g2.setColor(Theme.BORDER_DEFAULT);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(PAD_LEFT, PAD_TOP + chartH, w - PAD_RIGHT, PAD_TOP + chartH);

        // Y axis line
        g2.drawLine(PAD_LEFT, PAD_TOP, PAD_LEFT, PAD_TOP + chartH);
        g2.setStroke(new BasicStroke(1f));

        // Array size label
        g2.setFont(Theme.FONT_SANS_SMALL);
        g2.setColor(Theme.TEXT_MUTED);
        String sizeLabel = "n = " + array.length;
        g2.drawString(sizeLabel, w - PAD_RIGHT - g2.getFontMetrics().stringWidth(sizeLabel), h - 8);
    }

    private void drawLegend(Graphics2D g2, int w, int h) {
        Object[][] legend = {
                { Theme.BAR_DEFAULT,   "Default"  },
                { Theme.BAR_COMPARE,   "Compare"  },
                { Theme.BAR_SWAP,      "Swap"     },
                { Theme.BAR_PIVOT,     "Pivot"    },
                { Theme.BAR_SET,       "Set"      },
                { Theme.BAR_SORTED,    "Sorted"   },
        };

        int lx = PAD_LEFT + 4;
        int ly = PAD_TOP - 18;
        g2.setFont(Theme.FONT_SANS_SMALL);

        for (Object[] entry : legend) {
            Color c = (Color) entry[0];
            String label = (String) entry[1];

            g2.setColor(c);
            g2.fillRoundRect(lx, ly - 8, 10, 10, 3, 3);

            g2.setColor(Theme.TEXT_SECONDARY);
            g2.drawString(label, lx + 14, ly);

            lx += g2.getFontMetrics().stringWidth(label) + 30;
        }
    }
}