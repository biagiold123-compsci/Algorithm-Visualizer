package com.algorithmvisualizer.gui;

import com.algorithmvisualizer.algorithms.AlgorithmRegistry;
import com.algorithmvisualizer.utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

/**
 * HeaderPanel — top bar containing:
 *   - Application title / branding
 *   - Algorithm selector combo box
 *   - Array size slider
 *   - Randomize, Nearly Sorted, Reversed array presets
 */
public class HeaderPanel extends JPanel {

    private final MainFrame    owner;
    private final JComboBox<String> algoCombo;
    private final JSlider      sizeSlider;
    private final JLabel       sizeLabel;

    public HeaderPanel(MainFrame owner) {
        this.owner = owner;
        setBackground(Theme.BG_DARKEST);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(12, 20, 12, 20)
        ));
        setLayout(new BorderLayout(16, 0));

        // ----- Left: branding -----
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);
        JLabel title = new JLabel("Algorithm Visualizer");
        title.setFont(Theme.FONT_SANS_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel version = new JLabel("  v2.0");
        version.setFont(Theme.FONT_SANS_SMALL);
        version.setForeground(Theme.TEXT_MUTED);
        brand.add(title);
        brand.add(version);

        // ----- Center: algorithm picker -----
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        center.setOpaque(false);

        JLabel algoLabel = styledLabel("Algorithm:");
        algoCombo = styledCombo(AlgorithmRegistry.getNames().toArray(new String[0]));
        algoCombo.addActionListener(e -> {
            String selected = (String) algoCombo.getSelectedItem();
            if (selected != null) owner.loadAlgorithm(selected);
        });

        center.add(algoLabel);
        center.add(algoCombo);

        // Separator
        center.add(separator());

        // Size slider
        JLabel szLabel = styledLabel("Array Size:");
        sizeSlider = new JSlider(5, 100, 40);
        sizeSlider.setOpaque(false);
        sizeSlider.setForeground(Theme.ACCENT_BLUE);
        sizeSlider.setPreferredSize(new Dimension(140, 28));
        sizeLabel  = styledLabel("40");
        sizeLabel.setForeground(Theme.ACCENT_BLUE);
        sizeLabel.setPreferredSize(new Dimension(28, 20));
        sizeSlider.addChangeListener(e -> {
            int v = sizeSlider.getValue();
            sizeLabel.setText(String.valueOf(v));
            if (!sizeSlider.getValueIsAdjusting()) owner.generateArray(v);
        });

        center.add(szLabel);
        center.add(sizeSlider);
        center.add(sizeLabel);

        // ----- Right: preset buttons -----
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        right.add(styledLabel("Generate:"));
        right.add(presetButton("🔀 Random",      () -> owner.generateArray(sizeSlider.getValue())));
        right.add(presetButton("↑ Nearly Sorted", () -> owner.setArray(nearlySorted(sizeSlider.getValue()))));
        right.add(presetButton("↓ Reversed",      () -> owner.setArray(reversed(sizeSlider.getValue()))));
        right.add(presetButton("▲ Few Unique",    () -> owner.setArray(fewUnique(sizeSlider.getValue()))));

        add(brand,  BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(right,  BorderLayout.EAST);
    }

    // ---- Factory helpers ----

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.TEXT_SECONDARY);
        l.setFont(Theme.FONT_SANS_MEDIUM);
        return l;
    }

    private JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(Theme.BG_CARD);
        cb.setForeground(Theme.TEXT_PRIMARY);
        cb.setFont(Theme.FONT_SANS_MEDIUM);
        cb.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        cb.setPreferredSize(new Dimension(160, 30));
        return cb;
    }

    private JButton presetButton(String label, Runnable action) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? Theme.BG_HOVER : Theme.BG_CARD;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(Theme.BORDER_DEFAULT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Theme.TEXT_PRIMARY);
        btn.setFont(Theme.FONT_SANS_SMALL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 28));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JSeparator separator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setForeground(Theme.BORDER_DEFAULT);
        sep.setPreferredSize(new Dimension(1, 24));
        return sep;
    }

    // ---- Array generators ----

    private int[] nearlySorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i + 1;
        java.util.Random rng = new java.util.Random();
        // Make ~5% of positions out of order
        int swaps = Math.max(1, n / 20);
        for (int s = 0; s < swaps; s++) {
            int i = rng.nextInt(n), j = rng.nextInt(n);
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
        return arr;
    }

    private int[] reversed(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = n - i;
        return arr;
    }

    private int[] fewUnique(int n) {
        int[] arr = new int[n];
        int[] vals = {10, 30, 50, 70, 90};
        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < n; i++) arr[i] = vals[rng.nextInt(vals.length)];
        return arr;
    }
}