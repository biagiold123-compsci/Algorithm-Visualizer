package com.algorithmvisualizer.algorithms;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;

public class MergeSort extends SortingAlgorithm {

    private List<AlgorithmStep> steps;
    private int[] arr;

    @Override
    public String getName() { return "Merge Sort"; }

    @Override
    public List<AlgorithmStep> generateSteps(int[] input) {
        reset();
        arr   = input.clone();
        steps = new ArrayList<>();
        mergeSort(0, arr.length - 1);

        int[] allIdx = new int[arr.length];
        for (int k = 0; k < arr.length; k++) allIdx[k] = k;
        steps.add(new AlgorithmStep.Builder().arrayState(arr).highlighted(allIdx)
                .type(AlgorithmStep.StepType.SORTED).description("Array fully sorted!")
                .comparisons(comparisons).swaps(swaps).build());
        return steps;
    }

    private void mergeSort(int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(left, mid);
        mergeSort(mid + 1, right);
        merge(left, mid, right);
    }

    private void merge(int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        steps.add(new AlgorithmStep.Builder().arrayState(arr)
                .highlighted(rangeIndices(left, right))
                .type(AlgorithmStep.StepType.HIGHLIGHT)
                .description("Merging subarrays [%d..%d] and [%d..%d]".formatted(left, mid, mid+1, right))
                .comparisons(comparisons).swaps(swaps).build());

        while (i <= mid && j <= right) {
            comparisons++;
            steps.add(compareStep(arr,
                    "Comparing arr[%d]=%d and arr[%d]=%d".formatted(i, arr[i], j, arr[j]), i, j));
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }
        while (i <= mid)   temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];

        for (int x = 0; x < temp.length; x++) {
            arr[left + x] = temp[x];
            swaps++;
        }

        // Build the step directly to avoid the varargs/array ambiguity
        int[] range = rangeIndices(left, right);
        steps.add(new AlgorithmStep.Builder().arrayState(arr)
                .highlighted(range)
                .type(AlgorithmStep.StepType.SET)
                .description("Merged segment [%d..%d]".formatted(left, right))
                .comparisons(comparisons).swaps(swaps).build());
    }

    private int[] rangeIndices(int from, int to) {
        int[] idx = new int[to - from + 1];
        for (int i = 0; i < idx.length; i++) idx[i] = from + i;
        return idx;
    }

    @Override
    public AlgorithmInfo getInfo() {
        return new AlgorithmInfo(
                "Merge Sort", "Divide & Conquer",
                "Ω(n log n)", "Θ(n log n)", "O(n log n)", "O(n)", true,
                "Merge Sort recursively divides the array in half, sorts each half, and merges the results. It guarantees O(n log n) in all cases and is widely used in standard libraries due to its stability and predictable performance.",
                """
                mergeSort(arr, left, right):
                  if left >= right: return
                  mid = (left + right) / 2
                  mergeSort(arr, left, mid)
                  mergeSort(arr, mid+1, right)
                  merge(arr, left, mid, right)
                """
        );
    }
}
