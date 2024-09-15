package gh2;
import deque.Deque;
import deque.LinkedListDeque;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHeroLite {
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);


    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString stringA = new GuitarString(CONCERT_A);
        GuitarString stringC = new GuitarString(CONCERT_C);
        Deque<GuitarString> Note36 = new LinkedListDeque<>();
        for (int i = 0; i < 36; i++) {
            //440⋅2(i−24)/12
            int CONCERT_I = 440 * 2 ^ ((i - 24)/12);
            GuitarString stringI = new GuitarString(CONCERT_I);

            Note36.addLast(stringI);
        }
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {

                char key = StdDraw.nextKeyTyped();
                int num = keyboard.indexOf(key);
                if (num > 0 && num < 36 ) {
                    GuitarString theone = Note36.get(num);
                    theone.pluck();
                    double sample = theone.sample();

                    /* play the sample on standard audio */
                    StdAudio.play(sample);

                    /* advance the simulation of each guitar string by one step */
                    theone.tic();

            }

            /* compute the superposition of samples */

        }
    }
}
}

