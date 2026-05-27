package com.algorithmvisualizer;

import com.algorithmvisualizer.gui.MainFrame;
import javax.swing.*;
import java.awt.*;

/**
 * AlgorithmVisualizer - Entry Point
 *
 * A professional, interactive algorithm visualization tool built with Java Swing.
 * Demonstrates advanced Java concepts: generics, concurrency, design patterns,
 * custom rendering, observer pattern, and clean architecture.
 *
 * @author Algorithm Visualizer
 * @version 2.0
 */
public class Main {

    public static void main(String[] args) {
        // Set system look and feel hints for crisp rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.opengl", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                applyGlobalUIDefaults();
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to initialize UI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static void applyGlobalUIDefaults() {
        UIManager.put("Panel.background", new Color(13, 17, 23));
        UIManager.put("Button.background", new Color(30, 41, 59));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("ComboBox.background", new Color(30, 41, 59));
        UIManager.put("ComboBox.foreground", Color.WHITE);
        UIManager.put("Slider.background", new Color(13, 17, 23));
        UIManager.put("Slider.foreground", new Color(99, 179, 237));
        UIManager.put("TabbedPane.background", new Color(13, 17, 23));
        UIManager.put("TabbedPane.foreground", Color.WHITE);
    }
}