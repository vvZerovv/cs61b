package gh2;
import deque.ArrayDeque;
import deque.Deque;
//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        buffer = new ArrayDeque<Double>();
        int capacity = (int) Math.round(SR / frequency);
        for (int i = 0; i < capacity; i++) {
            buffer.addLast(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        int bufferSize = buffer.size();
        while (bufferSize > 0) {
            double r = Math.random() - 0.5;
            buffer.removeFirst();
            buffer.addLast(r);
            bufferSize -= 1;
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double sample = buffer.removeFirst();
        double newFront = buffer.get(0);
        sample = ((sample + newFront) / 2) * DECAY;
        buffer.addLast(sample);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }
}
