package com.algorithmvisualizer.gui;

import com.algorithmvisualizer.controllers.PlaybackController;
import com.algorithmvisualizer.controllers.PlaybackController.State;
import com.algorithmvisualizer.utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ControlPanel — bottom bar with:
 *   - ◀◀ Step Back  |  ▶/⏸ Play/Pause  |  ▶▶ Step Forward  |  ⏹ Stop
 *   - Speed slider (10ms–800ms delay)
 *   - Progress bar with step counter
 *   - Step description label
 */
public class ControlPanel extends JPanel {

    private final PlaybackController controller;
    private final MainFrame          owner;

    // Buttons
    private final JButton btnStepBack;
    private final JButton btnPlayPause;
    private final JButton btnStepFwd;
    private final JButton btnStop;

    // Progress
    private final JProgressBar progressBar;
    private final JLabel       stepCountLabel;
    private final JLabel       descLabel;

    // Speed
    private final JSlider speedSlider;
    private final JLabel  speedLabel;

    public ControlPanel(PlaybackController controller, MainFrame owner) {
        this.controller = controller;
        this.owner      = owner;

        setBackground(Theme.BG_DARKEST);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(10, 20, 10, 20)
        ));
        setLayout(new BorderLayout(16, 0));

        // ---- Transport buttons ----
        btnStepBack  = transportButton("⏮", "Step Back");
        btnPlayPause = transportButton("▶", "Play");
        btnStepFwd   = transportButton("⏭", "Step Forward");
        btnStop      = transportButton("⏹", "Stop / Reset");

        btnPlayPause.setForeground(Theme.ACCENT_GREEN);
        btnPlayPause.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        JPanel transport = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        transport.setOpaque(false);
        transport.add(btnStepBack);
        transport.add(btnPlayPause);
        transport.add(btnStepFwd);
        transport.add(btnStop);

        // ---- Speed control ----
        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        speedPanel.setOpaque(false);
        JLabel speedTitle = new JLabel("Speed");
        speedTitle.setFont(Theme.FONT_SANS_SMALL);
        speedTitle.setForeground(Theme.TEXT_SECONDARY);

        speedLabel = new JLabel("Med");

        speedSlider = new JSlider(JSlider.HORIZONTAL, 10, 800, 150);
        speedSlider.setInverted(true); // left = fast
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(120, 28));
        speedSlider.addChangeListener(e -> {
            int ms = speedSlider.getValue();
            controller.setSpeed(ms);
            speedLabel.setText(ms < 50 ? "Fast" : ms < 250 ? "Med" : "Slow");
        });
        speedLabel.setFont(Theme.FONT_SANS_SMALL);
        speedLabel.setForeground(Theme.ACCENT_BLUE);
        speedLabel.setPreferredSize(new Dimension(34, 20));

        speedPanel.add(speedTitle);
        speedPanel.add(speedSlider);
        speedPanel.add(speedLabel);

        // ---- Progress ----
        JPanel progressPanel = new JPanel(new GridBagLayout());
        progressPanel.setOpaque(false);

        progressBar = new JProgressBar(0, 1) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                int filled = (int)((double) getValue() / getMaximum() * getWidth());
                g2.setColor(Theme.ACCENT_BLUE);
                if (filled > 0) g2.fillRoundRect(0, 0, filled, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        progressBar.setPreferredSize(new Dimension(300, 8));
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        // Click-to-seek
        progressBar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int total = controller.getTotalSteps();
                if (total > 0) {
                    int targetIdx = (int)((double) e.getX() / progressBar.getWidth() * total);
                    controller.seekTo(Math.min(targetIdx, total - 1));
                }
            }
        });

        stepCountLabel = new JLabel("0 / 0");
        stepCountLabel.setFont(Theme.FONT_MONO_SMALL);
        stepCountLabel.setForeground(Theme.TEXT_MUTED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        progressPanel.add(progressBar, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(2, 0, 0, 0);
        progressPanel.add(stepCountLabel, gbc);

        // ---- Description label ----
        descLabel = new JLabel("Select an algorithm and press Play");
        descLabel.setFont(Theme.FONT_SANS_SMALL);
        descLabel.setForeground(Theme.TEXT_SECONDARY);
        descLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // ---- Layout ----
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftGroup.setOpaque(false);
        leftGroup.add(speedPanel);

        JPanel rightGroup = new JPanel(new BorderLayout(8, 0));
        rightGroup.setOpaque(false);
        rightGroup.add(progressPanel, BorderLayout.CENTER);
        rightGroup.add(descLabel,     BorderLayout.EAST);

        add(leftGroup,  BorderLayout.WEST);
        add(transport,  BorderLayout.CENTER);
        add(rightGroup, BorderLayout.EAST);

        // ---- Wire actions ----
        btnPlayPause.addActionListener(e -> togglePlayPause());
        btnStepBack .addActionListener(e -> controller.stepBackward());
        btnStepFwd  .addActionListener(e -> controller.stepForward());
        btnStop     .addActionListener(e -> {
            controller.stop();
            owner.loadAlgorithm(((JComboBox<?>) owner
                    .getComponent(0)).getSelectedItem() == null
                    ? "Bubble Sort"
                    : (String)((JComboBox<?>)
                    ((JPanel)((JSplitPane)owner.getContentPane().getComponent(0))
                            .getComponent(0)).getParent()).toString());
            // Simplified: just reload the current array
            controller.stop();
        });
    }

    // ---- Public API ----

    public void updateState(State state) {
        boolean hasSteps = controller.hasSteps();
        btnPlayPause.setEnabled(hasSteps);  // <-- fix: was never re-enabled after init
        btnStepBack .setEnabled(hasSteps && state != State.IDLE);
        btnStepFwd  .setEnabled(hasSteps && state != State.FINISHED);
        btnStop     .setEnabled(state != State.IDLE);

        switch (state) {
            case PLAYING  -> { btnPlayPause.setText("⏸"); btnPlayPause.setForeground(Theme.ACCENT_YELLOW); }
            case PAUSED   -> { btnPlayPause.setText("▶"); btnPlayPause.setForeground(Theme.ACCENT_GREEN);  }
            case FINISHED -> { btnPlayPause.setText("↺"); btnPlayPause.setForeground(Theme.ACCENT_CYAN);   }
            default       -> { btnPlayPause.setText("▶"); btnPlayPause.setForeground(Theme.ACCENT_GREEN);  }
        }
    }

    public void updateProgress(int current, int total) {
        progressBar.setMaximum(Math.max(1, total));
        progressBar.setValue(current);
        stepCountLabel.setText("Step %,d / %,d".formatted(current + 1, total));
        progressBar.repaint();
    }

    public void setDescription(String text) {
        descLabel.setText(text);
    }

    // ---- Internal ----

    private void togglePlayPause() {
        State s = controller.getState();
        if (s == State.PLAYING)            controller.pause();
        else if (s == State.FINISHED)      { controller.stop(); controller.play(); }
        else                               controller.play();
    }

    private JButton transportButton(String symbol, String tooltip) {
        JButton btn = new JButton(symbol) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = !isEnabled()          ? Theme.BG_DARK :
                        getModel().isPressed() ? Theme.BORDER_ACTIVE :
                                getModel().isRollover() ? Theme.BG_HOVER : Theme.BG_CARD;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isEnabled() ? Theme.BORDER_DEFAULT : new Color(48,54,61,80));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        btn.setForeground(Theme.TEXT_PRIMARY);
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(52, 36));
        btn.setEnabled(false);
        return btn;
    }
}