package gh2;
import deque.Deque;
import deque.LinkedListDeque;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHeroLite {
    public static final double A = 440.0;
    public static final double CONCERT_C = A * Math.pow(2, 3.0 / 12.0);


    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        Deque<GuitarString> nodeA36 = new LinkedListDeque<>();
        for (int i = 0; i < 36; i++) {
            double toneFez = 440 * Math.pow(2, (i - 24) / 12.0);
            GuitarString stringI = new GuitarString(toneFez);
            nodeA36.addLast(stringI);
        }
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

        StdDraw.setCanvasSize(800, 200);
        StdDraw.setXscale(0, 800);
        StdDraw.setYscale(0, 200);
        drawPiano(keyboard);
        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {

                char key = StdDraw.nextKeyTyped();
                int num = keyboard.indexOf(key);
                if (num >= 0 && num < 36) {
                    GuitarString theOne = nodeA36.get(num);
                    theOne.pluck();
                    for (int i = 0; i < 20000; i += 1) {
                        double sample = theOne.sample();
                        edu.princeton.cs.introcs.StdAudio.play(sample);
                        theOne.tic();
                    }
                    theOne.pluck();
                    /* play the sample on standard audio */

                    /* advance the simulation of each guitar string by one step */

                    highlightKey(num, keyboard.length());
                }

                /* compute the superposition of samples */

            }
        }
    }


    // 绘制钢琴界面
    private static void drawPiano(String keyboard) {
        double keyWidth = 800.0 / 36; // 36个键位
        for (int i = 0; i < keyboard.length(); i++) {
            // 绘制白键
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledRectangle(i * keyWidth + keyWidth / 2, 100, keyWidth / 2, 100);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.rectangle(i * keyWidth + keyWidth / 2, 100, keyWidth / 2, 100);

            // 绘制键位标签
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(i * keyWidth + keyWidth / 2, 80, String.valueOf(keyboard.charAt(i))); //
        }

        // 绘制黑键
        double blackKeyOffset = keyWidth / 2.0; // 黑键的偏移量
        int[] blackKeyPositions = {1, 3, 6, 8, 10}; // 黑键在白键中的位置
        for (int i = 0; i < blackKeyPositions.length; i++) {
            for (int j = 0; j < 3; j++) { // 为每组音符添加黑键
                int position = blackKeyPositions[i] + j * 12;
                if (position < 36) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledRectangle(position * keyWidth + blackKeyOffset, 150, keyWidth / 4, 40); // 高度调整
                }
            }
        }
        /*
        
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.text(400, 180, "Press the keys to play the guitar strings");
    */}

    // 高亮当前按下的键
    private static void highlightKey(int keyIndex, int totalKeys) {
        double keyWidth = 800.0 / totalKeys;
        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.filledRectangle(keyIndex * keyWidth + keyWidth / 2, 100, keyWidth / 2, 100);

        // 恢复原样
        StdDraw.show();
        try {
            Thread.sleep(300); // 持续高亮 300 毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 重新绘制钢琴
        drawPiano("q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' "); // 恢复
    }

}

