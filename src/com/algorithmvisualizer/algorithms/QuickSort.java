package com.algorithmvisualizer.algorithms;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;

public class QuickSort extends SortingAlgorithm {

    private List<AlgorithmStep> steps;
    private int[] arr;

    @Override
    public String getName() { return "Quick Sort"; }

    @Override
    public List<AlgorithmStep> generateSteps(int[] input) {
        reset();
        arr   = input.clone();
        steps = new ArrayList<>();
        quickSort(0, arr.length - 1);

        int[] allIdx = new int[arr.length];
        for (int k = 0; k < arr.length; k++) allIdx[k] = k;
        steps.add(new AlgorithmStep.Builder().arrayState(arr).highlighted(allIdx)
                .type(AlgorithmStep.StepType.SORTED).description("Array fully sorted!")
                .comparisons(comparisons).swaps(swaps).build());
        return steps;
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = arr[high];
        steps.add(pivotStep(arr, "Pivot selected: arr[%d]=%d".formatted(high, pivot), high));

        int i = low - 1;
        for (int j = low; j < high; j++) {
            comparisons++;
            steps.add(compareStep(arr,
                    "Comparing arr[%d]=%d with pivot=%d".formatted(j, arr[j], pivot), j, high));

            if (arr[j] <= pivot) {
                i++;
                doSwap(arr, i, j);
                if (i != j)
                    steps.add(swapStep(arr,
                            "arr[%d]=%d ≤ pivot → swapping with arr[%d]=%d".formatted(j, arr[j], i, arr[i]),
                            i, j));
            }
        }
        doSwap(arr, i + 1, high);
        steps.add(swapStep(arr,
                "Placing pivot=%d at its correct position %d".formatted(pivot, i+1), i+1, high));
        steps.add(sortedStep(arr, "Pivot %d is now in its final sorted position".formatted(arr[i+1]), i + 1));
        return i + 1;
    }

    @Override
    public AlgorithmInfo getInfo() {
        return new AlgorithmInfo(
                "Quick Sort", "Divide & Conquer",
                "Ω(n log n)", "Θ(n log n)", "O(n²)", "O(log n)", false,
                "Quick Sort picks a pivot element and partitions the array so all elements smaller than the pivot precede it and all larger elements follow. It then recursively sorts both partitions. It is cache-friendly and extremely fast in practice.",
                """
                quickSort(arr, low, high):
                  if low < high:
                    pi = partition(arr, low, high)
                    quickSort(arr, low, pi-1)
                    quickSort(arr, pi+1, high)
    
                partition(arr, low, high):
                  pivot = arr[high]; i = low - 1
                  for j = low to high-1:
                    if arr[j] <= pivot: i++; swap(arr[i], arr[j])
                  swap(arr[i+1], arr[high])
                  return i + 1
                """
        );
    }
}