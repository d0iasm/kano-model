import utils.Combination;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


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

        double balance = 0;
        double tmpBalance = 1;

        Map<Integer, Integer[]> memo = new HashMap<>();
        for (int[] c : l) {
            for (int key = 0; key < ELEMENT_NUM; key++) {
                if (!memo.containsKey(c[key])) {
                    memo.put(c[key], index(c[key], k.length));
                }
                int i = memo.get(c[key])[0];
                int j = memo.get(c[key])[1];
                tmpBalance *= k[i][j];
                System.out.print(k[i][j] + ", ");
            }
            balance += tmpBalance;
            tmpBalance = 1;
            System.out.println(" ");
        }
        System.out.println("--------- balance -----------");
        System.out.println(balance);
        return balance;
    }

    private Integer[] index(int n, int len) {
        Integer index[] = new Integer[2];
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
