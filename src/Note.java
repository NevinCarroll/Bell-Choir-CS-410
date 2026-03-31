/**
 * The {@code Note} enum represents musical notes and generates
 * corresponding audio samples as sine waves.
 * <p>
 * Each note (except {@code REST}) is assigned a frequency based on
 * equal temperament tuning, where A4 = 440 Hz.
 * </p>
 * <p>
 * The generated samples are used for playback via {@link javax.sound.sampled.SourceDataLine}.
 * </p>
 */
public enum Note {

    /** Represents silence (no sound). Must be the first enum value. */
    REST,

    /** Musical notes in ascending order relative to A4. */
    A4,
    A4S,
    B4,
    C4,
    C4S,
    D4,
    D4S,
    E4,
    F4,
    F4S,
    G4,
    G4S,
    A5;

    /** Sample rate in Hz (~48 kHz). */
    public static final int SAMPLE_RATE = 48 * 1024;

    /** Duration of one measure in seconds. */
    public static final int MEASURE_LENGTH_SEC = 1;

    /**
     * Angular step per sample:
     * (2π radians) / (samples per second)
     */
    private static final double STEP_ALPHA = (2.0d * Math.PI) / SAMPLE_RATE;

    /** Reference frequency for A4 (standard pitch). */
    private static final double FREQUENCY_A_HZ = 440.0d;

    /** Maximum amplitude for 8-bit audio (signed). */
    private static final double MAX_VOLUME = 127.0d;

    /**
     * Precomputed sine wave samples for this note.
     * Length = SAMPLE_RATE * MEASURE_LENGTH_SEC.
     */
    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

    /**
     * Constructs a {@code Note} and generates its audio sample.
     * <p>
     * The frequency is calculated using the equal temperament formula:
     * <pre>
     * f = 440 * 2^(n / 12)
     * </pre>
     * where {@code n} is the number of half-steps away from A4.
     * </p>
     * <p>
     * The resulting sine wave is stored as 8-bit PCM audio data.
     * </p>
     */
    private Note() {
        int n = this.ordinal();

        // Skip REST (no sound)
        if (n > 0) {

            // Number of half-steps above A4
            final double halfStepsFromA = n - 1;

            // Equal temperament exponent
            final double exponent = halfStepsFromA / 12.0d;

            // Calculate frequency for this note
            final double frequency = FREQUENCY_A_HZ * Math.pow(2.0d, exponent);

            // Convert frequency into angular step per sample
            final double sinStep = frequency * STEP_ALPHA;

            // Generate sine wave samples
            for (int i = 0; i < sinSample.length; i++) {
                sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
            }
        }
        // REST remains all zeros (silence)
    }

    /**
     * Returns the precomputed audio sample for this note.
     *
     * @return byte array containing 8-bit PCM audio data
     */
    public byte[] sample() {
        return sinSample;
    }
}