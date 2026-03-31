import javax.sound.sampled.SourceDataLine;

public class Player implements Runnable {
    private final Note note; // Note player will play
    private volatile NoteLength noteLength;

    private final SourceDataLine sourceDataLine;
    private final Thread playerThread;

    private volatile boolean myTurn;
    private volatile boolean running;

    Player(Note note, SourceDataLine sourceDataLine) {
        this.note = note;
        noteLength = NoteLength.WHOLE;
        this.sourceDataLine = sourceDataLine;
        playerThread = new Thread(this);
    }

    public void run() {
        while (running) {
            synchronized (this) {
                if (myTurn) {
                    playNote();
                    myTurn = false;
                    notify();
                } else {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }

    public void setNoteLength(NoteLength noteLength) {
        this.noteLength = noteLength;
    }

    public void startThread() {
        running = true;
        playerThread.start();
    }

    public void stopThread() {
        running = false;
    }

    /**
     * Play note on the line
     */
    private void playNote() {
        final int ms = Math.min(noteLength.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        sourceDataLine.write(note.sample(), 0, length);
        sourceDataLine.write(Note.REST.sample(), 0, 50);
    }

    public synchronized void waitToStop() {
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    public synchronized void giveTurn() {
        myTurn = true;
        notify();
    }

    public synchronized void notifyPlayer() {
        notify();
    }
}