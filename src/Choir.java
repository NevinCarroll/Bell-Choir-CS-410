import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Choir {

    // Mary had a little lamb
    private final List<BellNote> song = new ArrayList<>();

    private final Conductor conductor;
    private final AudioFormat af;

    Choir(AudioFormat af) {
        this.conductor = new Conductor();
        this.af = af;
    }

    private void loadNoteSheet(String filename) {
        BufferedReader noteReader = null;
        try {
            noteReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String line;
        while (true) {

            try {
                if ((line = noteReader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String[] parts = line.split(" ");

            if (parts.length != 2) {
                System.err.println("Invalid note sheet at line " + line);
                return;
            }

            String noteString = parts[0];
            int noteLengthString = 0;

            try {
                noteLengthString = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid length at line " + line);
            }

            Note note = Note.valueOf(noteString);
            NoteLength noteLength;

            switch (noteLengthString) {
                case 1:
                    noteLength = NoteLength.WHOLE;
                    break;
                case 2:
                    noteLength = NoteLength.HALF;
                    break;
                case 4:
                    noteLength = NoteLength.QUARTER;
                    break;
                case 8:
                    noteLength = NoteLength.EIGHTH;
                    break;
                default:
                    System.err.println("Invalid note length at line " + line);
                    return;
            }

            BellNote newNote = new BellNote(note, noteLength);

            song.add(newNote);
        }
    }

    public static void main(String[] args) throws Exception {
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        Choir choir = new Choir(af);
        choir.loadNoteSheet(args[0]);
        choir.playSong();
    }

    void playSong() throws LineUnavailableException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();

            for (BellNote bn: song) {
                playNote(line, bn);
            }
            line.drain();
        }
    }

    private void playNote(SourceDataLine line, BellNote bn) {
        final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }
}