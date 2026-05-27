package com.algorithmvisualizer.gui;

import com.algorithmvisualizer.algorithms.AlgorithmRegistry;
import com.algorithmvisualizer.algorithms.SortingAlgorithm;
import com.algorithmvisualizer.controllers.PlaybackController;
import com.algorithmvisualizer.models.AlgorithmStep;
import com.algorithmvisualizer.utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;

/**
 * MainFrame is the top-level window.
 *
 * Layout (BorderLayout):
 *   NORTH  → HeaderPanel      (title + algorithm selector + array controls)
 *   CENTER → VisualizerPanel  (animated bar chart)
 *   SOUTH  → ControlPanel     (play/pause/step + speed slider + progress)
 *   EAST   → InfoPanel        (complexity table + step description + stats)
 */
public class MainFrame extends JFrame {

    // ---- Core components ----
    private final PlaybackController controller = new PlaybackController();
    private VisualizerPanel  visualizerPanel;
    private ControlPanel     controlPanel;
    private InfoPanel        infoPanel;
    private HeaderPanel      headerPanel;

    // ---- State ----
    private int[]            currentArray;
    private SortingAlgorithm currentAlgorithm;

    public MainFrame() {
        super("Algorithm Visualizer");
        initWindow();
        buildUI();
        wireController();
        generateArray(40);
        loadAlgorithm(AlgorithmRegistry.getNames().get(0));
    }

    // ------------------------------------------------------------------ init

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1400, 820));
        setMinimumSize(new Dimension(900, 600));
        getContentPane().setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout(0, 0));

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                controller.shutdown();
            }
        });
    }

    private void buildUI() {
        headerPanel    = new HeaderPanel(this);
        visualizerPanel = new VisualizerPanel();
        controlPanel   = new ControlPanel(controller, this);
        infoPanel      = new InfoPanel();

        // Wrap center + east in a split for resizing
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                visualizerPanel, infoPanel);
        centerSplit.setDividerLocation(960);
        centerSplit.setDividerSize(4);
        centerSplit.setContinuousLayout(true);
        centerSplit.setBorder(null);
        centerSplit.setBackground(Theme.BG_DARK);

        add(headerPanel,  BorderLayout.NORTH);
        add(centerSplit,  BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------- controller wiring

    private void wireController() {
        controller.setOnStepChange(step -> SwingUtilities.invokeLater(() -> {
            visualizerPanel.setStep(step);
            infoPanel.setStep(step);
            controlPanel.updateProgress(controller.getCurrentIndex(), controller.getTotalSteps());
        }));

        controller.setOnStateChange(state -> SwingUtilities.invokeLater(() -> {
            controlPanel.updateState(state);
        }));

        controller.setOnIndexChange(idx -> SwingUtilities.invokeLater(() ->
                controlPanel.updateProgress(idx, controller.getTotalSteps())));
    }

    // -------------------------------------------------- public actions

    /** Called by HeaderPanel when the user picks a new algorithm. */
    public void loadAlgorithm(String name) {
        controller.stop();
        currentAlgorithm = AlgorithmRegistry.get(name);
        if (currentAlgorithm == null) return;

        List<AlgorithmStep> steps = currentAlgorithm.generateSteps(currentArray);
        controller.load(steps);

        infoPanel.setAlgorithmInfo(currentAlgorithm.getInfo());
        controlPanel.updateProgress(0, steps.size());
        controlPanel.updateState(PlaybackController.State.IDLE);
        visualizerPanel.setArray(currentArray);
        visualizerPanel.repaint();
    }

    /** Called by HeaderPanel / ControlPanel to generate a new array. */
    public void generateArray(int size) {
        controller.stop();
        currentArray = randomArray(size);
        if (currentAlgorithm != null) {
            loadAlgorithm(currentAlgorithm.getName());
        } else {
            visualizerPanel.setArray(currentArray);
        }
    }

    public void setArray(int[] arr) {
        controller.stop();
        currentArray = arr.clone();
        if (currentAlgorithm != null) loadAlgorithm(currentAlgorithm.getName());
    }

    public int[] getCurrentArray() { return currentArray.clone(); }

    // -------------------------------------------------- helpers

    private int[] randomArray(int size) {
        Random rng = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = rng.nextInt(1, 101);
        return arr;
    }
}