package com.algorithmvisualizer.algorithms;

import java.util.*;

/**
 * Registry that maps algorithm names to instances.
 * Acts as a factory using the Registry pattern — new algorithms
 * can be added here without touching any other code.
 */
public final class AlgorithmRegistry {

    private static final Map<String, SortingAlgorithm> REGISTRY = new LinkedHashMap<>();

    static {
        register(new BubbleSort());
        register(new SelectionSort());
        register(new InsertionSort());
        register(new ShellSort());
        register(new MergeSort());
        register(new QuickSort());
        register(new HeapSort());
    }

    private AlgorithmRegistry() {}

    private static void register(SortingAlgorithm algo) {
        REGISTRY.put(algo.getName(), algo);
    }

    public static SortingAlgorithm get(String name) {
        return REGISTRY.get(name);
    }

    public static List<String> getNames() {
        return Collections.unmodifiableList(new ArrayList<>(REGISTRY.keySet()));
    }

    public static Collection<SortingAlgorithm> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }
}