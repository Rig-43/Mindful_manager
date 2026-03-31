package mindfulmanager.timer;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A simple Pomodoro timer: 25 minutes work + 5 minutes break by default.
 * Uses a Swing Timer so it plays nicely with Swing UI.
 */
public class PomodoroTimer {
    public enum Phase {
        WORK,
        BREAK
    }

    public interface PomodoroListener {
        void onTick(Phase phase, int remainingSeconds);

        /**
         * Called when a phase ends, before switching to the next phase.
         */
        void onPhaseEnded(Phase endedPhase, Phase nextPhase);

        void onStateChanged(boolean running, Phase phase);
    }

    private final int workDurationSeconds;
    private final int breakDurationSeconds;
    private final boolean autoAdvance;

    private Phase phase = Phase.WORK;
    private int remainingSeconds = 0;
    private boolean running = false;

    private Timer swingTimer;
    private PomodoroListener listener;

    public PomodoroTimer(int workDurationSeconds, int breakDurationSeconds, boolean autoAdvance) {
        this.workDurationSeconds = Math.max(1, workDurationSeconds);
        this.breakDurationSeconds = Math.max(1, breakDurationSeconds);
        this.autoAdvance = autoAdvance;
        this.remainingSeconds = this.workDurationSeconds;
        initSwingTimer();
    }

    public PomodoroTimer() {
        this(25 * 60, 5 * 60, true);
    }

    public void setListener(PomodoroListener listener) {
        this.listener = listener;
    }

    private void initSwingTimer() {
        swingTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        swingTimer.setRepeats(true);
    }

    public Phase getPhase() {
        return phase;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean value) {
        running = value;
        if (listener != null) listener.onStateChanged(running, phase);
    }

    private void tick() {
        if (!running) return;

        remainingSeconds--;
        if (remainingSeconds < 0) remainingSeconds = 0;

        if (listener != null) listener.onTick(phase, remainingSeconds);

        if (remainingSeconds <= 0) {
            Phase ended = phase;
            Phase next = (phase == Phase.WORK) ? Phase.BREAK : Phase.WORK;

            setRunning(false);
            if (listener != null) listener.onPhaseEnded(ended, next);

            // Move to next phase
            phase = next;
            remainingSeconds = (phase == Phase.WORK) ? workDurationSeconds : breakDurationSeconds;

            if (autoAdvance) {
                start();
            } else {
                // Notify UI about updated phase but keep paused.
                if (listener != null) listener.onStateChanged(false, phase);
            }
        }
    }

    private void start() {
        if (running) return;
        setRunning(true);
        swingTimer.start();
        // Immediately report tick to refresh UI quickly.
        if (listener != null) listener.onTick(phase, remainingSeconds);
    }

    public void startWork() {
        stopAndReset();
        phase = Phase.WORK;
        remainingSeconds = workDurationSeconds;
        start();
    }

    public void startBreak() {
        stopAndReset();
        phase = Phase.BREAK;
        remainingSeconds = breakDurationSeconds;
        start();
    }

    public void pause() {
        if (!running) return;
        swingTimer.stop();
        setRunning(false);
    }

    public void resume() {
        if (running) return;
        start();
    }

    public void stopAndReset() {
        swingTimer.stop();
        setRunning(false);
        phase = Phase.WORK;
        remainingSeconds = workDurationSeconds;
        if (listener != null) listener.onTick(phase, remainingSeconds);
    }

    /**
     * Immediately ends the current phase and (if auto-advance is enabled) starts the next one.
     */
    public void skipPhase() {
        if (!running) return;
        remainingSeconds = 0;
        tick();
    }

    public static String formatSeconds(int seconds) {
        int s = Math.max(0, seconds);
        int hours = s / 3600;
        int mins = (s % 3600) / 60;
        int secs = s % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, mins, secs);
        }
        return String.format("%02d:%02d", mins, secs);
    }
}

