/**
 * The {@code NoteLength} enum represents the duration of musical notes
 * as fractions of a measure.
 * <p>
 * Each value corresponds to a standard musical note duration:
 * whole, half, quarter, and eighth notes.
 * </p>
 * <p>
 * The actual duration in milliseconds is calculated based on
 * {@link Note#MEASURE_LENGTH_SEC}.
 * </p>
 */
public enum NoteLength {

    /** A whole note (full measure). */
    WHOLE(1.0f),

    /** A half note (1/2 of a measure). */
    HALF(0.5f),

    /** A quarter note (1/4 of a measure). */
    QUARTER(0.25f),

    /** An eighth note (1/8 of a measure). */
    EIGHTH(0.125f);

    /** Duration of the note in milliseconds. */
    private final int timeMs;

    /**
     * Constructs a {@code NoteLength} and calculates its duration.
     *
     * @param length fraction of a measure (e.g., 1.0 = whole, 0.5 = half)
     */
    private NoteLength(float length) {
        // Convert fractional measure length into milliseconds
        timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    /**
     * Returns the duration of the note in milliseconds.
     *
     * @return note duration in milliseconds
     */
    public int timeMs() {
        return timeMs;
    }
}