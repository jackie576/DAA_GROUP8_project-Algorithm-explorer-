// Schedules each sort or recursion animation frame and controls playback state.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package visualization;

// this import is needed for the Timer class used to schedule animation frames.
import javax.swing.Timer;

//

// Plays a list of recorded steps one at a time. Handles play, pause, single

// step, reset, speed and the elapsed-time clock, and exposes a short "blip"
public class AnimationController {

    public interface Listener {
        void onStep(int index);
        void onTick();
        void onStateChanged();
    }

    private double baseIntervalMs = 500;

    private final Listener listener;
    private final Timer timer;

    private int total;
    private int index = -1;
    private boolean playing;
    private double speed = 1.0;
    private double accumulatedMs;
    private double elapsedMs;
    private long lastNanos = System.nanoTime();
    private long blipStartNanos;

    // class constructor that initializes the AnimationController with a listener for step, tick, and state change events.
    public AnimationController(Listener listener) {
        this.listener = listener;
        this.timer = new Timer(16, e -> tick());
        this.timer.start();
    }

    // This method is called on each timer tick to update the animation state, advance steps if playing, and notify the listener of ticks and state changes.
    private void tick() {
        long now = System.nanoTime();
        double dt = (now - lastNanos) / 1_000_000.0;
        lastNanos = now;

        if (playing) {
            elapsedMs += dt;
            accumulatedMs += dt;
            if (accumulatedMs >= intervalMs()) {
                accumulatedMs = 0;
                advance();
            }
        }

        if (playing || blip() > 0) {
            listener.onTick();
        }
    }

    // Advances to the next step in the animation, updating the index and notifying the listener. If the end of the steps is reached, it stops playing and notifies the listener of the state change.
    private void advance() {
        if (index + 1 >= total) {
            playing = false;
            listener.onStateChanged();
            return;
        }
        index++;
        blipStartNanos = System.nanoTime();
        listener.onStep(index);

        if (index >= total - 1) {
            playing = false;
            listener.onStateChanged();
        }
    }

    // Loads a new sequence of steps for animation, resetting the index, playing state, and elapsed time, and notifying the listener of the state change.
    public void load(int totalSteps) {
        total = totalSteps;
        index = -1;
        playing = false;
        accumulatedMs = 0;
        elapsedMs = 0;
        blipStartNanos = 0;
        listener.onStateChanged();
    }

    // Resets the animation to the beginning, stopping playback and resetting the index and elapsed time, and notifying the listener of the state change.
    public void play() {
        if (total == 0 || index >= total - 1) {
            return;
        }
        playing = true;
        listener.onStateChanged();
    }

    // Pauses the animation, stopping playback and notifying the listener of the state change.
    public void pause() {
        playing = false;
        listener.onStateChanged();
    }

    // Steps forward one frame in the animation, pausing playback and notifying the listener of the state change.
    public void stepOnce() {
        playing = false;
        advance();
        listener.onStateChanged();
    }

    // Time per step at 1x speed (default 500 ms).
    public void setBaseInterval(double ms) {
        this.baseIntervalMs = ms;
    }

    // Sets the playback speed multiplier, affecting the interval between steps and notifying the listener of the state change.
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    // Returns the current playback speed multiplier.
    public double intervalMs() {
        return baseIntervalMs / speed;
    }

    // 0 when idle; rises to 1 and falls back to 0 right after each step.
    public double blip() {
        if (index < 0) {
            return 0;
        }
        double duration = Math.min(280, intervalMs() * 0.85);
        double t = (System.nanoTime() - blipStartNanos) / 1_000_000.0 / duration;
        if (t < 0 || t >= 1) {
            return 0;
        }
        return Math.sin(Math.PI * t);
    }

    public boolean isPlaying() { return playing; }

    public boolean isLoaded() { return total > 0; }

    // True only when a run is under way and currently paused.
    public boolean isPaused() { return total > 0 && index >= 0 && !playing && !isFinished(); }

    public boolean isFinished() { return total > 0 && index >= total - 1; }

    public int getIndex() { return index; }

    public int getTotal() { return total; }

    public double getElapsedSeconds() { return elapsedMs / 1000.0; }
}
