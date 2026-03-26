class BellNote {
    private final Note note;
    private final NoteLength length;

    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }

    public Note getNote() {
        return note;
    }

    public NoteLength getNoteLength() {
        return length;
    }
}