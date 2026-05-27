package com.algorithmvisualizer.algorithms;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.List;

/**
 * Abstract base class for all sorting algorithms.
 * Uses the Template Method pattern: subclasses implement generateSteps(),
 * while common utilities (swap recording, comparison tracking) live here.
 *
 * All algorithms record every step so the visualizer can play, pause,
 * and scrub through execution at any speed.
 */
public abstract class SortingAlgorithm {

    protected int comparisons = 0;
    protected int swaps       = 0;

    /**
     * Generate all execution steps for a given array.
     * The array is NOT modified; steps capture state snapshots.
     */
    public abstract List<AlgorithmStep> generateSteps(int[] inputArray);

    /** Human-readable name for UI display */
    public abstract String getName();

    /** Full metadata for the info panel */
    public abstract AlgorithmInfo getInfo();

    // ---- Shared helpers ----

    protected AlgorithmStep compareStep(int[] arr, String desc, int... indices) {
        return new AlgorithmStep.Builder()
                .arrayState(arr)
                .highlighted(indices)
                .type(AlgorithmStep.StepType.COMPARE)
                .description(desc)
                .comparisons(comparisons)
                .swaps(swaps)
                .build();
    }

    protected AlgorithmStep swapStep(int[] arr, String desc, int... indices) {
        return new AlgorithmStep.Builder()
                .arrayState(arr)
                .highlighted(indices)
                .type(AlgorithmStep.StepType.SWAP)
                .description(desc)
                .comparisons(comparisons)
                .swaps(swaps)
                .build();
    }

    protected AlgorithmStep pivotStep(int[] arr, String desc, int pivotIdx, int... extra) {
        int[] all = new int[extra.length + 1];
        all[0] = pivotIdx;
        System.arraycopy(extra, 0, all, 1, extra.length);
        return new AlgorithmStep.Builder()
                .arrayState(arr)
                .highlighted(all)
                .type(AlgorithmStep.StepType.PIVOT)
                .description(desc)
                .comparisons(comparisons)
                .swaps(swaps)
                .build();
    }

    protected AlgorithmStep sortedStep(int[] arr, String desc, int... indices) {
        return new AlgorithmStep.Builder()
                .arrayState(arr)
                .highlighted(indices)
                .type(AlgorithmStep.StepType.SORTED)
                .description(desc)
                .comparisons(comparisons)
                .swaps(swaps)
                .build();
    }

    protected AlgorithmStep setStep(int[] arr, String desc, int... indices) {
        return new AlgorithmStep.Builder()
                .arrayState(arr)
                .highlighted(indices)
                .type(AlgorithmStep.StepType.SET)
                .description(desc)
                .comparisons(comparisons)
                .swaps(swaps)
                .build();
    }

    protected void doSwap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        swaps++;
    }

    protected void reset() {
        comparisons = 0;
        swaps       = 0;
    }
}