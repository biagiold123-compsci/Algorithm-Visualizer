package com.algorithmvisualizer.algorithms;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;

public class InsertionSort extends SortingAlgorithm {

    @Override
    public String getName() { return "Insertion Sort"; }

    @Override
    public List<AlgorithmStep> generateSteps(int[] input) {
        reset();
        int[] arr = input.clone();
        List<AlgorithmStep> steps = new ArrayList<>();
        int n = arr.length;

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j   = i - 1;
            steps.add(pivotStep(arr, "Inserting key=%d (index %d) into sorted portion".formatted(key, i), i));

            while (j >= 0 && arr[j] > key) {
                comparisons++;
                steps.add(compareStep(arr,
                        "arr[%d]=%d > key=%d → shifting right".formatted(j, arr[j], key), j, j + 1));
                arr[j + 1] = arr[j];
                swaps++;
                steps.add(setStep(arr, "Shifted arr[%d]=%d to position %d".formatted(j, arr[j+1], j+1), j, j + 1));
                j--;
            }
            arr[j + 1] = key;
            steps.add(setStep(arr, "Inserted key=%d at position %d".formatted(key, j+1), j + 1));
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
                "Insertion Sort", "Comparison Sort",
                "Ω(n)", "Θ(n²)", "O(n²)", "O(1)", true,
                "Insertion Sort builds the sorted array one element at a time. It picks each element and inserts it into its correct position in the already-sorted prefix — similar to how you sort playing cards in your hand.",
                """
                for i = 1 to n-1:
                  key = arr[i]
                  j = i - 1
                  while j >= 0 and arr[j] > key:
                    arr[j+1] = arr[j]
                    j = j - 1
                  arr[j+1] = key
                """
        );
    }
}