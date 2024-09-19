package gh2;
import deque.Deque;
import deque.LinkedListDeque;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHeroLite {
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);


    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        Deque<GuitarString> nodeA36 = new LinkedListDeque<>();
        for (int i = 0; i < 36; i++) {
            //440⋅2(i−24)/12
            double CONCERT_I = 440 * Math.pow(2, (i - 24) / 12.0);;
            GuitarString stringI = new GuitarString(CONCERT_I);
            nodeA36.addLast(stringI);
        }
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {

                char key = StdDraw.nextKeyTyped();
                int num = keyboard.indexOf(key);
                if (num > 0 && num < 36) {
                    GuitarString theOne = nodeA36.get(num);
                    theOne.pluck();
                    for (int i = 0; i < 20000; i += 1) {
                        double sample = theOne.sample();
                        edu.princeton.cs.introcs.StdAudio.play(sample);
                        theOne.tic(); }
                    theOne.pluck();
                    /* play the sample on standard audio */

                    /* advance the simulation of each guitar string by one step */


                }

            /* compute the superposition of samples */

            }
        }
    }
}

