package com.algorithmvisualizer.gui;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import com.algorithmvisualizer.utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * InfoPanel — right-hand panel with four sections:
 *   1. Algorithm name + category badge
 *   2. Complexity table (best / avg / worst / space / stable)
 *   3. Description paragraph
 *   4. Pseudocode block
 *   5. Live runtime stats (comparisons / swaps / current step type)
 */
public class InfoPanel extends JPanel {

    // Complexity labels
    private final JLabel lblName, lblCategory;
    private final JLabel lblBest, lblAvg, lblWorst, lblSpace, lblStable;

    // Description + pseudocode
    private final JTextArea taDescription;
    private final JTextArea taPseudo;

    // Live stats
    private final JLabel lblComparisons, lblSwaps, lblStepType, lblStepDesc;

    public InfoPanel() {
        setBackground(Theme.BG_DARK);
        setPreferredSize(new Dimension(340, 600));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        // ---- 1. Header ----
        lblName = new JLabel("—");
        lblName.setFont(Theme.FONT_SANS_TITLE);
        lblName.setForeground(Theme.TEXT_PRIMARY);
        lblName.setAlignmentX(LEFT_ALIGNMENT);

        lblCategory = new JLabel("—");
        lblCategory.setFont(Theme.FONT_SANS_SMALL);
        lblCategory.setForeground(Theme.ACCENT_BLUE);
        lblCategory.setAlignmentX(LEFT_ALIGNMENT);

        content.add(lblName);
        content.add(vgap(2));
        content.add(lblCategory);
        content.add(vgap(12));
        content.add(divider());
        content.add(vgap(12));

        // ---- 2. Complexity table ----
        content.add(sectionTitle("Complexity"));
        content.add(vgap(6));

        lblBest   = complexityLabel(); lblAvg    = complexityLabel();
        lblWorst  = complexityLabel(); lblSpace  = complexityLabel();
        lblStable = complexityLabel();

        content.add(complexityRow("Best Case:",    lblBest));
        content.add(vgap(3));
        content.add(complexityRow("Average Case:", lblAvg));
        content.add(vgap(3));
        content.add(complexityRow("Worst Case:",   lblWorst));
        content.add(vgap(3));
        content.add(complexityRow("Space:",        lblSpace));
        content.add(vgap(3));
        content.add(complexityRow("Stable:",       lblStable));
        content.add(vgap(12));
        content.add(divider());
        content.add(vgap(12));

        // ---- 3. Description ----
        content.add(sectionTitle("Description"));
        content.add(vgap(6));
        taDescription = styledTextArea(4);
        JScrollPane descScroll = scrollPane(taDescription);
        descScroll.setAlignmentX(LEFT_ALIGNMENT);
        content.add(descScroll);
        content.add(vgap(12));
        content.add(divider());
        content.add(vgap(12));

        // ---- 4. Pseudocode ----
        content.add(sectionTitle("Pseudocode"));
        content.add(vgap(6));
        taPseudo = new JTextArea(7, 0);
        taPseudo.setFont(Theme.FONT_MONO_SMALL);
        taPseudo.setForeground(new Color(130, 210, 130));
        taPseudo.setBackground(new Color(16, 22, 30));
        taPseudo.setEditable(false);
        taPseudo.setLineWrap(false);
        taPseudo.setWrapStyleWord(false);
        taPseudo.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane pseudoScroll = new JScrollPane(taPseudo);
        pseudoScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        pseudoScroll.setBackground(new Color(16, 22, 30));
        pseudoScroll.setAlignmentX(LEFT_ALIGNMENT);
        pseudoScroll.setMaximumSize(new Dimension(Short.MAX_VALUE, 160));
        content.add(pseudoScroll);
        content.add(vgap(12));
        content.add(divider());
        content.add(vgap(12));

        // ---- 5. Live stats ----
        content.add(sectionTitle("Live Statistics"));
        content.add(vgap(6));

        lblComparisons = statLabel("—");
        lblSwaps       = statLabel("—");
        lblStepType    = statLabel("—");

        content.add(statRow("Comparisons:", lblComparisons, Theme.ACCENT_YELLOW));
        content.add(vgap(4));
        content.add(statRow("Swaps / Sets:", lblSwaps,      Theme.ACCENT_RED));
        content.add(vgap(4));
        content.add(statRow("Step Type:",    lblStepType,   Theme.ACCENT_CYAN));
        content.add(vgap(10));

        lblStepDesc = new JLabel("<html><i>—</i></html>");
        lblStepDesc.setFont(Theme.FONT_SANS_SMALL);
        lblStepDesc.setForeground(Theme.TEXT_SECONDARY);
        lblStepDesc.setAlignmentX(LEFT_ALIGNMENT);
        lblStepDesc.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        content.add(lblStepDesc);

        JScrollPane outer = new JScrollPane(content);
        outer.setBorder(null);
        outer.setBackground(Theme.BG_DARK);
        outer.getVerticalScrollBar().setUnitIncrement(12);
        outer.getViewport().setBackground(Theme.BG_DARK);
        add(outer, BorderLayout.CENTER);
    }

    // ---- Public setters ----

    public void setAlgorithmInfo(AlgorithmInfo info) {
        lblName    .setText(info.name());
        lblCategory.setText(info.category());
        lblBest    .setText(info.timeComplexityBest());
        lblAvg     .setText(info.timeComplexityAverage());
        lblWorst   .setText(info.timeComplexityWorst());
        lblSpace   .setText(info.spaceComplexity());
        lblStable  .setText(info.isStable() ? "✓ Yes" : "✗ No");
        lblStable  .setForeground(info.isStable() ? Theme.ACCENT_GREEN : Theme.ACCENT_RED);
        taDescription.setText(info.description());
        taDescription.setCaretPosition(0);
        taPseudo.setText(info.pseudocode());
        taPseudo.setCaretPosition(0);
        resetStats();
    }

    public void setStep(AlgorithmStep step) {
        lblComparisons.setText(String.format("%,d", step.getComparisons()));
        lblSwaps      .setText(String.format("%,d", step.getSwaps()));
        lblStepType   .setText(formatStepType(step.getType()));
        String desc = step.getDescription();
        lblStepDesc.setText("<html>" + desc + "</html>");
    }

    // ---- Private builders ----

    private void resetStats() {
        lblComparisons.setText("0");
        lblSwaps      .setText("0");
        lblStepType   .setText("—");
        lblStepDesc   .setText("<html><i>Press Play to begin</i></html>");
    }

    private String formatStepType(AlgorithmStep.StepType t) {
        return switch (t) {
            case COMPARE   -> "Compare";
            case SWAP      -> "Swap";
            case PIVOT     -> "Pivot";
            case SORTED    -> "Sorted ✓";
            case SET       -> "Set";
            case HIGHLIGHT -> "Highlight";
            default        -> t.name();
        };
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        l.setForeground(Theme.TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel complexityLabel() {
        JLabel l = new JLabel("—");
        l.setFont(Theme.FONT_MONO_MEDIUM);
        l.setForeground(Theme.ACCENT_CYAN);
        return l;
    }

    private JLabel statLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_MONO_LARGE);
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    private JPanel complexityRow(String key, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        JLabel k = new JLabel(key);
        k.setFont(Theme.FONT_SANS_SMALL);
        k.setForeground(Theme.TEXT_SECONDARY);
        row.add(k, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JPanel statRow(String key, JLabel value, Color accent) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
        JLabel k = new JLabel(key);
        k.setFont(Theme.FONT_SANS_SMALL);
        k.setForeground(Theme.TEXT_SECONDARY);
        value.setForeground(accent);
        row.add(k, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JTextArea styledTextArea(int rows) {
        JTextArea ta = new JTextArea(rows, 0);
        ta.setFont(Theme.FONT_SANS_SMALL);
        ta.setForeground(Theme.TEXT_SECONDARY);
        ta.setBackground(Theme.BG_CARD);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(6, 6, 6, 6));
        return ta;
    }

    private JScrollPane scrollPane(JTextArea ta) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        sp.setMaximumSize(new Dimension(Short.MAX_VALUE, 90));
        return sp;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_DEFAULT);
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    private Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }
}