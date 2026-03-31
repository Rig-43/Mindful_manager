package mindfulmanager.mind;

import java.util.Random;

/**
 * Supplies random mindfulness breathing exercises and quotes.
 */
public class MindfulnessEngine {
    private final Random random = new Random();

    private final String[] quotes = new String[] {
            "Small steps every day. Progress compounds quietly.",
            "Breathe. You are exactly where you need to be to begin again.",
            "Focus on the next kind action.",
            "Your calm is a superpower.",
            "Let the moment be enough.",
            "Discipline is self-respect in action.",
            "Slow is smooth. Smooth is fast."
    };

    /**
     * Each entry is formatted as multi-line breathing instructions.
     */
    private final String[] breathingExercises = new String[] {
            // Matches the user-provided example pattern.
            "Inhale 4 sec\nHold 4 sec\nExhale 6 sec\nHold 2 sec\n\nRepeat for 3-5 cycles.",
            "Inhale 5 sec\nHold 3 sec\nExhale 7 sec\n\nRepeat for 3-5 cycles.",
            "Inhale 3 sec\nHold 3 sec\nExhale 4 sec\nHold 2 sec\n\nRepeat for 4 cycles.",
            "Inhale 6 sec\nExhale 6 sec\n\nRepeat for 5 cycles.",
            "Inhale 4 sec\nExhale 8 sec\n\nRepeat for 4 cycles."
    };

    public String getRandomBreathingExercise() {
        return breathingExercises[random.nextInt(breathingExercises.length)];
    }

    public String getRandomQuote() {
        return quotes[random.nextInt(quotes.length)];
    }
}

