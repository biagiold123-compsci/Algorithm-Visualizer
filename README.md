# Algorithm Visualizer

A professional, interactive algorithm visualization tool built entirely in **Java** and **Swing**, demonstrating advanced language features, clean architecture, and a polished dark-themed GUI fit for a computer science portfolio.

---

## Features

| Feature | Detail |
|---|---|
| **7 Sorting Algorithms** | Bubble, Selection, Insertion, Shell, Merge, Quick, and Heap Sort |
| **Step-by-step Playback** | Play, Pause, Step Forward, Step Back, and Stop controls |
| **Click-to-Seek** | Click anywhere on the progress bar to jump to any point in the execution |
| **Speed Control** | Adjustable delay from 10ms (fast) to 800ms (slow) with live effect |
| **Array Presets** | Random, Nearly Sorted, Reversed, and Few Unique array generators |
| **Array Size Slider** | Resize the input array from 5 to 100 elements on the fly |
| **Color-coded Bars** | Compare (yellow), Swap (red), Pivot (orange), Set (cyan), Sorted (green) |
| **Glow Effects** | Active bars rendered with a soft glow and gradient shading |
| **Live Statistics** | Comparison count, swap count, and step type update on every frame |
| **Info Panel** | Per-algorithm time/space complexity, stability, description, and pseudocode |
| **Per-step Descriptions** | Human-readable explanation of every individual operation |

---

## Project Structure

```
AlgorithmVisualizer/
└── src/
    └── main/
        └── java/
            └── com/algorithmvisualizer/
                ├── Main.java                          # Entry point — configures rendering and launches Swing on the EDT
                ├── algorithms/
                │   ├── SortingAlgorithm.java          # Abstract base class (Template Method pattern)
                │   ├── AlgorithmRegistry.java         # Registry / Factory — maps names to instances
                │   ├── BubbleSort.java
                │   ├── SelectionSort.java
                │   ├── InsertionSort.java
                │   ├── ShellSort.java                 # Knuth sequence gap reduction
                │   ├── MergeSort.java                 # Recursive divide and conquer
                │   ├── QuickSort.java                 # Lomuto partition scheme
                │   └── HeapSort.java                  # Binary max-heap extraction
                ├── controllers/
                │   └── PlaybackController.java        # Animation lifecycle via ScheduledExecutorService
                ├── gui/
                │   ├── MainFrame.java                 # Root JFrame — wires all panels and owns the controller
                │   ├── VisualizerPanel.java            # Custom bar chart canvas with antialiased rendering
                │   ├── HeaderPanel.java               # Algorithm selector, array size slider, and presets
                │   ├── ControlPanel.java              # Transport buttons, progress bar, and speed slider
                │   └── InfoPanel.java                 # Complexity table, pseudocode, and live stats
                ├── models/
                │   ├── AlgorithmStep.java             # Immutable step snapshot (Builder pattern)
                │   └── AlgorithmInfo.java             # Algorithm metadata (Java Record)
                └── utils/
                    └── Theme.java                     # Centralized design system — colors, fonts, and spacing
```

---

## Key Java Concepts Demonstrated

- **Template Method Pattern** — `SortingAlgorithm` defines the step-recording contract; each subclass implements only `generateSteps()` and `getInfo()`
- **Builder Pattern** — `AlgorithmStep.Builder` constructs immutable step snapshots with a fluent API
- **Registry / Factory Pattern** — `AlgorithmRegistry` maps algorithm names to instances; adding a new algorithm requires no changes outside the registry
- **Observer Pattern** — `PlaybackController` exposes `setOnStepChange`, `setOnStateChange`, and `setOnIndexChange` listeners; panels subscribe without coupling to each other
- **Immutable Value Objects** — `AlgorithmStep` is fully immutable for safe cross-thread publishing from the playback executor to the Swing EDT
- **Java Records** — `AlgorithmInfo` is declared as a `record`, demonstrating compact, boilerplate-free data carriers
- **Concurrency** — `PlaybackController` uses a daemon-backed `ScheduledExecutorService` for frame-accurate, non-blocking animation timing
- **Swing EDT compliance** — all UI updates dispatched via `SwingUtilities.invokeLater`, keeping the playback thread and the render thread correctly separated
- **Custom 2D Rendering** — `VisualizerPanel` overrides `paintComponent` with full `RenderingHints` antialiasing, `GradientPaint` bar fills, `RoundRectangle2D` shapes, and a layered glow effect
- **Enums in switch expressions** — `AlgorithmStep.StepType` drives color and behavior resolution via sealed `switch` expressions in `Theme` and `InfoPanel`
- **Streams** — `Arrays.stream(array).max()` and registry collection methods use the Stream API throughout
- **Text Blocks** — pseudocode strings in each algorithm use Java 15+ text block literals for clean multiline formatting
- **Functional interfaces** — step, state, and index listeners are typed as `Consumer<T>` and wired as lambda expressions

---

## Getting Started

### Prerequisites

- Java 17 or later

### Run the pre-built JAR

```bash
java -jar AlgorithmVisualizer.jar
```

### Build from source

```bash
find src -name "*.java" > sources.txt
mkdir -p out/classes
javac --release 17 -d out/classes @sources.txt
jar --create --file AlgorithmVisualizer.jar --main-class com.algorithmvisualizer.Main -C out/classes .
```

---

## Using the Application

### Controls

| Control | Action |
|---|---|
| **Algorithm dropdown** | Switch between all 7 sorting algorithms |
| **Array Size slider** | Resize the array from 5 to 100 elements |
| **Generate buttons** | Seed the array with Random, Nearly Sorted, Reversed, or Few Unique values |
| **Play** | Run the full animation from the current position |
| **Pause** | Freeze playback at the current step |
| **Step Back / Step Forward** | Move backward or forward one operation at a time |
| **Stop** | Reset playback to the beginning |
| **Progress bar** | Click anywhere to seek directly to that step |
| **Speed slider** | Drag left for faster, right for slower animation |

### Color Legend

| Color | Meaning |
|---|---|
| Blue-grey | Default unsorted bar |
| Yellow | Elements being compared |
| Red | Elements being swapped |
| Orange | Current pivot element |
| Cyan | Element being written or shifted |
| Green | Element confirmed in its sorted position |
