package com.algorithmvisualizer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single step in an algorithm's execution.
 * Uses the immutable value object pattern for thread safety.
 */
public final class AlgorithmStep {

    public enum StepType {
        COMPARE, SWAP, SET, HIGHLIGHT, PIVOT, SORTED, RESET, FOUND, PATH, VISITED, FRONTIER
    }

    private final int[] arrayState;
    private final int[] highlightedIndices;
    private final StepType type;
    private final String description;
    private final int comparisons;
    private final int swaps;

    private AlgorithmStep(Builder builder) {
        this.arrayState = builder.arrayState.clone();
        this.highlightedIndices = builder.highlightedIndices.clone();
        this.type = builder.type;
        this.description = builder.description;
        this.comparisons = builder.comparisons;
        this.swaps = builder.swaps;
    }

    public int[] getArrayState()         { return arrayState.clone(); }
    public int[] getHighlightedIndices() { return highlightedIndices.clone(); }
    public StepType getType()            { return type; }
    public String getDescription()       { return description; }
    public int getComparisons()          { return comparisons; }
    public int getSwaps()                { return swaps; }

    // ----- Builder -----
    public static class Builder {
        private int[] arrayState       = new int[0];
        private int[] highlightedIndices = new int[0];
        private StepType type          = StepType.COMPARE;
        private String description     = "";
        private int comparisons        = 0;
        private int swaps              = 0;

        public Builder arrayState(int[] state)        { this.arrayState = state; return this; }
        public Builder highlighted(int... indices)    { this.highlightedIndices = indices; return this; }
        public Builder type(StepType t)               { this.type = t; return this; }
        public Builder description(String d)          { this.description = d; return this; }
        public Builder comparisons(int c)             { this.comparisons = c; return this; }
        public Builder swaps(int s)                   { this.swaps = s; return this; }
        public AlgorithmStep build()                  { return new AlgorithmStep(this); }
    }
}