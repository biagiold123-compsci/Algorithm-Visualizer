package com.algorithmvisualizer.controllers;

import com.algorithmvisualizer.models.AlgorithmStep;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * PlaybackController manages the animation lifecycle.
 *
 * Design highlights:
 * - Uses a ScheduledExecutorService for precise, non-blocking timing
 * - Publishes step events to registered listeners (Observer pattern)
 * - Supports play, pause, step-forward, step-back, and speed control
 * - Thread-safe: all state mutations guarded by volatile/atomic
 */
public class PlaybackController {

    public enum State { IDLE, PLAYING, PAUSED, FINISHED }

    private volatile State state = State.IDLE;
    private volatile int   currentStepIndex = 0;
    private volatile int   speedMs = 150;   // delay between steps

    private List<AlgorithmStep> steps;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "playback-thread");
                t.setDaemon(true);
                return t;
            });

    private ScheduledFuture<?> playbackTask;

    // Listeners
    private Consumer<AlgorithmStep> onStepChange  = s -> {};
    private Consumer<State>         onStateChange = s -> {};
    private Consumer<Integer>       onIndexChange = i -> {};

    // ---- Public API ----

    public void load(List<AlgorithmStep> newSteps) {
        stop();
        this.steps = newSteps;
        this.currentStepIndex = 0;
        this.state = State.IDLE;
        fireStateChange();
        if (!steps.isEmpty()) fireStepChange(steps.get(0));
        onIndexChange.accept(0);
    }

    public void play() {
        if (steps == null || steps.isEmpty()) return;
        if (state == State.FINISHED) {
            currentStepIndex = 0;
            fireStepChange(steps.get(0));
        }
        state = State.PLAYING;
        fireStateChange();
        scheduleNext();
    }

    public void pause() {
        if (state == State.PLAYING) {
            cancelPlayback();
            state = State.PAUSED;
            fireStateChange();
        }
    }

    public void stop() {
        cancelPlayback();
        state = State.IDLE;
        currentStepIndex = 0;
        fireStateChange();
    }

    public void stepForward() {
        if (steps == null) return;
        pause();
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            fireStepChange(steps.get(currentStepIndex));
            onIndexChange.accept(currentStepIndex);
        }
    }

    public void stepBackward() {
        if (steps == null) return;
        pause();
        if (currentStepIndex > 0) {
            currentStepIndex--;
            fireStepChange(steps.get(currentStepIndex));
            onIndexChange.accept(currentStepIndex);
        }
    }

    public void seekTo(int index) {
        if (steps == null || index < 0 || index >= steps.size()) return;
        boolean wasPlaying = state == State.PLAYING;
        if (wasPlaying) cancelPlayback();
        currentStepIndex = index;
        fireStepChange(steps.get(currentStepIndex));
        onIndexChange.accept(currentStepIndex);
        if (wasPlaying) scheduleNext();
    }

    public void setSpeed(int delayMs) {
        this.speedMs = Math.max(10, delayMs);
    }

    // ---- Accessors ----

    public State   getState()            { return state; }
    public int     getCurrentIndex()     { return currentStepIndex; }
    public int     getTotalSteps()       { return steps == null ? 0 : steps.size(); }
    public boolean hasSteps()            { return steps != null && !steps.isEmpty(); }

    // ---- Listener registration ----

    public void setOnStepChange(Consumer<AlgorithmStep> listener)  { onStepChange  = listener; }
    public void setOnStateChange(Consumer<State> listener)         { onStateChange = listener; }
    public void setOnIndexChange(Consumer<Integer> listener)       { onIndexChange = listener; }

    // ---- Internal ----

    private void scheduleNext() {
        playbackTask = scheduler.schedule(() -> {
            if (state != State.PLAYING) return;
            if (currentStepIndex < steps.size() - 1) {
                currentStepIndex++;
                fireStepChange(steps.get(currentStepIndex));
                onIndexChange.accept(currentStepIndex);
                scheduleNext();
            } else {
                state = State.FINISHED;
                fireStateChange();
            }
        }, speedMs, TimeUnit.MILLISECONDS);
    }

    private void cancelPlayback() {
        if (playbackTask != null && !playbackTask.isDone()) {
            playbackTask.cancel(false);
        }
    }

    private void fireStepChange(AlgorithmStep step) {
        onStepChange.accept(step);
    }

    private void fireStateChange() {
        onStateChange.accept(state);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}