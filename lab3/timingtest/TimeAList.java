package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private  static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
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

    public static void main (String[] args) {
        timeAListConstruction();
    }

    public static double timesingleConstruction(int number,AList<Integer> time_test){
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < number; i++) {
            time_test.addLast(i);
        }
        double timeInSeconds = sw.elapsedTime();
        return timeInSeconds;
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> Numbers = new AList<>();
        AList<Integer> time_test = new AList<>();
        AList<Integer> ops = new AList<>();
        AList<Double>  times = new AList<>();
        for (int i = 1000; i <130000; i = i*2) {
            Numbers.addLast(i);
            ops.addLast(i);
        }
        int numers_length = Numbers.size();
        for (int i = 0; i < numers_length; i++) {
            double time = timesingleConstruction(Numbers.get(i),time_test );
            times.addLast(time);
        }
        printTimingTable(Numbers,times,ops);

    }
}
