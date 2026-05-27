package com.algorithmvisualizer.algorithms;

import com.algorithmvisualizer.models.AlgorithmInfo;
import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;

public class HeapSort extends SortingAlgorithm {

    private List<AlgorithmStep> steps;
    private int[] arr;

    @Override
    public String getName() { return "Heap Sort"; }

    @Override
    public List<AlgorithmStep> generateSteps(int[] input) {
        reset();
        arr   = input.clone();
        steps = new ArrayList<>();
        int n = arr.length;

        // Build max-heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            steps.add(new AlgorithmStep.Builder().arrayState(arr)
                    .highlighted(i).type(AlgorithmStep.StepType.HIGHLIGHT)
                    .description("Building max-heap: heapifying subtree at index %d".formatted(i))
                    .comparisons(comparisons).swaps(swaps).build());
            heapify(n, i);
        }

        steps.add(new AlgorithmStep.Builder().arrayState(arr)
                .highlighted(rangeIndices(0, n - 1)).type(AlgorithmStep.StepType.HIGHLIGHT)
                .description("Max-heap built! Largest element %d is at root".formatted(arr[0]))
                .comparisons(comparisons).swaps(swaps).build());

        // Extract elements from heap
        for (int i = n - 1; i > 0; i--) {
            doSwap(arr, 0, i);
            steps.add(swapStep(arr, "Extracting max %d → placing at position %d".formatted(arr[i], i), 0, i));
            steps.add(sortedStep(arr, "Position %d is now sorted (value=%d)".formatted(i, arr[i]), i));
            heapify(i, 0);
        }

        int[] allIdx = rangeIndices(0, n - 1);
        steps.add(new AlgorithmStep.Builder().arrayState(arr).highlighted(allIdx)
                .type(AlgorithmStep.StepType.SORTED).description("Array fully sorted!")
                .comparisons(comparisons).swaps(swaps).build());
        return steps;
    }

    private void heapify(int n, int i) {
        int largest = i;
        int left    = 2 * i + 1;
        int right   = 2 * i + 2;

        if (left < n) {
            comparisons++;
            steps.add(compareStep(arr,
                    "Comparing parent arr[%d]=%d with left child arr[%d]=%d".formatted(i, arr[i], left, arr[left]),
                    i, left));
            if (arr[left] > arr[largest]) largest = left;
        }
        if (right < n) {
            comparisons++;
            steps.add(compareStep(arr,
                    "Comparing arr[%d]=%d with right child arr[%d]=%d".formatted(largest, arr[largest], right, arr[right]),
                    largest, right));
            if (arr[right] > arr[largest]) largest = right;
        }
        if (largest != i) {
            doSwap(arr, i, largest);
            steps.add(swapStep(arr, "Heap violation: swapping arr[%d]=%d ↔ arr[%d]=%d".formatted(i, arr[i], largest, arr[largest]), i, largest));
            heapify(n, largest);
        }
    }

    private int[] rangeIndices(int from, int to) {
        int[] idx = new int[to - from + 1];
        for (int i = 0; i < idx.length; i++) idx[i] = from + i;
        return idx;
    }

    @Override
    public AlgorithmInfo getInfo() {
        return new AlgorithmInfo(
                "Heap Sort", "Comparison Sort",
                "Ω(n log n)", "Θ(n log n)", "O(n log n)", "O(1)", false,
                "Heap Sort uses a binary max-heap data structure. First it builds a heap from the array, then repeatedly extracts the maximum element and places it at the end. It guarantees O(n log n) and sorts in-place.",
                """
                heapSort(arr):
                  n = arr.length
                  for i = n/2-1 down to 0: heapify(arr, n, i)
                  for i = n-1 down to 1:
                    swap(arr[0], arr[i])
                    heapify(arr, i, 0)
    
                heapify(arr, n, i):
                  largest = i; l=2i+1; r=2i+2
                  if l<n and arr[l]>arr[largest]: largest=l
                  if r<n and arr[r]>arr[largest]: largest=r
                  if largest != i:
                    swap(arr[i], arr[largest])
                    heapify(arr, n, largest)
                """
        );
    }
}