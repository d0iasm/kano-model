import utils.Combination;

import java.util.List;


public class KanoKBalanceMetrics implements Metrics {
    private static Metrics instance = new KanoKBalanceMetrics();
    private final int ELEMENT_NUM = 3;

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
        Combination combination = new Combination(k.length * k[0].length, ELEMENT_NUM);

        System.out.println("combination results");
        List<int[]> l = combination.list();
        combination.print();
        System.out.println(combination.size());


        return 0;
    }

    private int[] index(int n, int len) {
        int index[] = new int[2];
        int i = 0;
        while (true) {
            if (n < i * len) {
                index[0] = i - 1;
                index[1] = n - (index[0] * len);
                break;
            }
            i++;
        }
        return index;
    }
}
