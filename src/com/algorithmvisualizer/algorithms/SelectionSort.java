package com.algorithmvisualizer.algorithms;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;

public class SelectionSort extends SortingAlgorithm {

    @Override
    public String getName() { return "Selection Sort"; }

    @Override
    public List<AlgorithmStep> generateSteps(int[] input) {
        reset();
        int[] arr = input.clone();
        List<AlgorithmStep> steps = new ArrayList<>();
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            steps.add(pivotStep(arr, "Searching for minimum from index %d".formatted(i), i));

            for (int j = i + 1; j < n; j++) {
                comparisons++;
                steps.add(compareStep(arr,
                        "Is arr[%d]=%d < current min arr[%d]=%d?".formatted(j, arr[j], minIdx, arr[minIdx]),
                        minIdx, j));
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                    steps.add(pivotStep(arr, "New minimum found at index %d (value=%d)".formatted(minIdx, arr[minIdx]), minIdx));
                }
            }

            if (minIdx != i) {
                doSwap(arr, i, minIdx);
                steps.add(swapStep(arr, "Placing minimum %d at position %d".formatted(arr[i], i), i, minIdx));
            }
            steps.add(sortedStep(arr, "Position %d is now sorted".formatted(i), i));
        }

        int[] allIdx = new int[n];
        for (int k = 0; k < n; k++) allIdx[k] = k;
        steps.add(new AlgorithmStep.Builder().arrayState(arr).highlighted(allIdx)
                .type(AlgorithmStep.StepType.SORTED).description("Array fully sorted!")
                .comparisons(comparisons).swaps(swaps).build());
        return steps;
    }

    @Override
    public AlgorithmInfo getInfo() {
        return new AlgorithmInfo(
                "Selection Sort", "Comparison Sort",
                "Ω(n²)", "Θ(n²)", "O(n²)", "O(1)", false,
                "Selection Sort divides the array into sorted and unsorted regions. In each pass it finds the minimum element in the unsorted region and places it at the boundary — making exactly n-1 swaps total.",
                """
                for i = 0 to n-2:
                  minIdx = i
                  for j = i+1 to n-1:
                    if arr[j] < arr[minIdx]:
                      minIdx = j
                  swap(arr[i], arr[minIdx])
                """
        );
    }
}