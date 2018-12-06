import utils.Combination;

import java.util.LinkedList;

public class KanoKBalanceMetrics implements Metrics {
    private static Metrics instance = new KanoKBalanceMetrics();

    public static Metrics getInstance() {
        return instance;
    }

    private KanoKBalanceMetrics() {
    }

    @Override
    public void debug() {
        System.out.println("Kano Kij Balance Metrics");
    }

    public double calcHeiderBalance(double[][] k) {
        Combination c = new Combination();
        LinkedList<String> r = c.combination(3, 4);

        System.out.println("combination results");
        System.out.println(r);
        return 0;
    }
}
