import javax.sound.sampled.SourceDataLine;

/**
 * The {@code Player} class represents a single musician responsible for playing
 * a specific {@link Note}.
 * Each player runs on its own thread and waits until the {@code Choir} (conductor)
 * signals that it is their turn to play.
 */
public class Player implements Runnable {

    /** The note this player is responsible for playing. */
    private final Note note;

    /** The duration of the note to be played. */
    private volatile NoteLength noteLength;

    /** Shared audio output line used to write sound data. */
    private final SourceDataLine sourceDataLine;

    /** Thread responsible for executing this player. */
    private final Thread playerThread;

    /** Indicates whether it is currently this player's turn to play. */
    private volatile boolean myTurn;

    /** Controls whether the player thread should continue running. */
    private volatile boolean running;

    /**
     * Constructs a {@code Player} for a specific note.
     *
     * @param note the note this player will play
     * @param sourceDataLine the shared audio output line
     */
    Player(Note note, SourceDataLine sourceDataLine) {
        this.note = note;
        this.noteLength = NoteLength.WHOLE;
        this.sourceDataLine = sourceDataLine;
        this.playerThread = new Thread(this);
    }

    /**
     * Main execution loop for the player thread.
     * <p>
     * The player waits until notified that it is their turn. When signaled,
     * it plays its note and then notifies the conductor that it has finished.
     * </p>
     */
    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                if (myTurn) {
                    playNote();     // Play assigned note
                    myTurn = false; // Reset turn flag

                    notify();       // Notify conductor that playback is complete
                } else {
                    try {
                        wait();     // Wait until notified it's this player's turn
                    } catch (InterruptedException ignored) {
                        // Thread interruption is ignored in this implementation
                    }
                }
            }
        }
    }

    /**
     * Sets the duration of the next note to be played.
     *
     * @param noteLength the note duration
     */
    public void setNoteLength(NoteLength noteLength) {
        this.noteLength = noteLength;
    }

    /**
     * Starts the player thread.
     */
    public void startThread() {
        running = true;
        playerThread.start();
    }

    /**
     * Signals the player thread to stop execution.
     * Note: The thread will exit its loop on the next iteration.
     */
    public void stopThread() {
        running = false;
    }

    /**
     * Plays the current note through the audio line.
     * The duration is capped at one measure length to prevent overflow.
     * A short rest is appended after each note to separate sounds.
     */
    private void playNote() {
        final int ms = Math.min(noteLength.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;

        // Write audio samples for the note
        sourceDataLine.write(note.sample(), 0, length);

        // Add a short rest between notes
        sourceDataLine.write(Note.REST.sample(), 0, 50);
    }

    /**
     * Causes the calling thread (typically the conductor) to wait until
     * this player has finished playing its note.
     */
    public synchronized void waitToStop() {
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Signals this player that it is their turn to play.
     */
    public synchronized void giveTurn() {
        myTurn = true;
        notify();
    }

    /**
     * Wakes up the player thread if it is waiting.
     * <p>
     * Used during shutdown to ensure the thread can exit cleanly.
     * </p>
     */
    public synchronized void notifyPlayer() {
        notify();
    }
}