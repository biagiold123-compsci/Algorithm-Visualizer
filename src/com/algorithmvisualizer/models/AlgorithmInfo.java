package com.algorithmvisualizer.models;

/**
 * Metadata container for an algorithm, used to display info panels.
 */
public record AlgorithmInfo(
        String name,
        String category,
        String timeComplexityBest,
        String timeComplexityAverage,
        String timeComplexityWorst,
        String spaceComplexity,
        boolean isStable,
        String description,
        String pseudocode
) {}