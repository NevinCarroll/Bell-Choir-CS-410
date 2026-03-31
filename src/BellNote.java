/**
 * The {@code BellNote} class represents a single musical note event,
 * combining a {@link Note} with its corresponding {@link NoteLength}.
 * <p>
 * This is a simple data container used by the {@code Choir} to store
 * and sequence notes for playback.
 * </p>
 */
class BellNote {

    /** The musical note to be played. */
    private final Note note;

    /** The duration of the note. */
    private final NoteLength length;

    /**
     * Constructs a {@code BellNote} with the given note and duration.
     *
     * @param note the musical note
     * @param length the duration of the note
     */
    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }

    /**
     * Returns the note.
     *
     * @return the {@link Note}
     */
    public Note getNote() {
        return note;
    }

    /**
     * Returns the note length (duration).
     *
     * @return the {@link NoteLength}
     */
    public NoteLength getNoteLength() {
        return length;
    }
}