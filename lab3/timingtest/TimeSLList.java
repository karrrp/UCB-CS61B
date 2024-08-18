package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static double timesingleConstruction(int op_number,int numbers,SLList<Integer> time_test){
        for (int i = 0; i < numbers; i++) {
            time_test.addFirst(i);
        }
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < op_number; i++) {
            time_test.getLast();
        }
        double timeInSeconds = sw.elapsedTime();
        return timeInSeconds;
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE

        AList<Integer> Numbers = new AList<>();
        SLList<Integer> time_test = new SLList<>();
        AList<Integer> ops = new AList<>();
        AList<Double>  times = new AList<>();
        for (int i = 1000; i < 130500; i = i * 2) {
            Numbers.addLast(i);
            ops.addLast(10000);
        }
        for (int i = 0; i < ops.size(); i++) {
            double time = timesingleConstruction(ops.getLast(), Numbers.get(i),time_test );
            times.addLast(time);
        }
        printTimingTable(Numbers,times,ops);
    }

}
