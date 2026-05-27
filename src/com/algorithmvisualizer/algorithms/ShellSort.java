package com.algorithmvisualizer.algorithms;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;

public class ShellSort extends SortingAlgorithm {

    @Override
    public String getName() { return "Shell Sort"; }

    @Override
    public List<AlgorithmStep> generateSteps(int[] input) {
        reset();
        int[] arr = input.clone();
        List<AlgorithmStep> steps = new ArrayList<>();
        int n = arr.length;

        // Knuth sequence: ..., 121, 40, 13, 4, 1
        int gap = 1;
        while (gap < n / 3) gap = 3 * gap + 1;

        while (gap >= 1) {
            steps.add(new AlgorithmStep.Builder().arrayState(arr)
                    .highlighted(new int[0]).type(AlgorithmStep.StepType.HIGHLIGHT)
                    .description("Gap = %d: performing gapped insertion sort".formatted(gap))
                    .comparisons(comparisons).swaps(swaps).build());

            for (int i = gap; i < n; i++) {
                int temp = arr[i];
                int j    = i;
                steps.add(pivotStep(arr, "Key=%d at index %d, gap=%d".formatted(temp, i, gap), i));

                while (j >= gap) {
                    comparisons++;
                    steps.add(compareStep(arr,
                            "Comparing arr[%d]=%d with key=%d (gap=%d)".formatted(j - gap, arr[j - gap], temp, gap),
                            j - gap, j));
                    if (arr[j - gap] > temp) {
                        arr[j] = arr[j - gap];
                        swaps++;
                        steps.add(setStep(arr,
                                "Moving arr[%d]=%d to position %d".formatted(j - gap, arr[j], j), j, j - gap));
                        j -= gap;
                    } else break;
                }
                arr[j] = temp;
                steps.add(setStep(arr, "Placed key=%d at index %d".formatted(temp, j), j));
            }
            gap /= 3;
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
                "Shell Sort", "Diminishing Increment Sort",
                "Ω(n log n)", "Θ(n^(4/3))", "O(n²)", "O(1)", false,
                "Shell Sort is a generalization of Insertion Sort. It starts by sorting elements far apart, gradually reducing the gap. Using the Knuth sequence (3^k - 1)/2, it achieves better-than-quadratic performance.",
                """
                gap = 1
                while gap < n/3: gap = 3*gap + 1
                while gap >= 1:
                  for i = gap to n-1:
                    temp = arr[i]; j = i
                    while j >= gap and arr[j-gap] > temp:
                      arr[j] = arr[j-gap]; j -= gap
                    arr[j] = temp
                  gap /= 3
                """
        );
    }
}