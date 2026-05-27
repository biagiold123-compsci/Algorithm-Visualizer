package com.algorithmvisualizer.algorithms;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;

public class BubbleSort extends SortingAlgorithm {

    @Override
    public String getName() { return "Bubble Sort"; }

    @Override
    public List<AlgorithmStep> generateSteps(int[] input) {
        reset();
        int[] arr = input.clone();
        List<AlgorithmStep> steps = new ArrayList<>();
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                comparisons++;
                steps.add(compareStep(arr,
                        "Comparing arr[%d]=%d and arr[%d]=%d".formatted(j, arr[j], j+1, arr[j+1]),
                        j, j + 1));

                if (arr[j] > arr[j + 1]) {
                    doSwap(arr, j, j + 1);
                    swapped = true;
                    steps.add(swapStep(arr,
                            "Swapped arr[%d]=%d and arr[%d]=%d".formatted(j, arr[j], j+1, arr[j+1]),
                            j, j + 1));
                }
            }
            steps.add(sortedStep(arr, "Pass %d complete — position %d is sorted".formatted(i+1, n-i-1), n - i - 1));
            if (!swapped) break; // early-exit optimisation
        }

        // Mark all sorted
        int[] allIdx = new int[n];
        for (int k = 0; k < n; k++) allIdx[k] = k;
        steps.add(new AlgorithmStep.Builder()
                .arrayState(arr).highlighted(allIdx)
                .type(AlgorithmStep.StepType.SORTED)
                .description("Array fully sorted!")
                .comparisons(comparisons).swaps(swaps).build());
        return steps;
    }

    @Override
    public AlgorithmInfo getInfo() {
        return new AlgorithmInfo(
                "Bubble Sort", "Comparison Sort",
                "Ω(n)", "Θ(n²)", "O(n²)", "O(1)", true,
                "Bubble Sort repeatedly steps through the list, compares adjacent elements, and swaps them if they're in the wrong order. The largest unsorted element \"bubbles\" to its correct position each pass.",
                """
                for i = 0 to n-2:
                  for j = 0 to n-i-2:
                    if arr[j] > arr[j+1]:
                      swap(arr[j], arr[j+1])
                """
        );
    }
}