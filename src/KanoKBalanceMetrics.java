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

    public double calcHeiderBalanceBasedOnAllTriangle(double[][] k, int n, int type) {
        /*
         * This function calculates the index of Heider balance state based on all triangles in particles.
         *
         * @param k Kij represents "to what extent person i prefers person j" defined in the Kano's thesis.
         * @return The index of Heider balance state.
         */
        Combination combination = new Combination(n, ELEMENT_NUM);

        System.out.println("combination results");
        List<int[]> l = combination.list();
        combination.print();

        System.out.println(n);
        System.out.println(combination.size());
        System.out.println(50 / 2);
        System.out.println(25 / 2);


        double balance = 0;
        double tmpBalance = 1;

        for (int[] c : l) {
            for (int key = 0; key < ELEMENT_NUM; key++) {
                // TODO: Consider how to calculate the balance of one triangle.
            }
        }

        return 0;
    }

    public double calcHeiderBalanceBasedOnK(double[][] k) {
        /*
         * This function calculates the index of Heider balance state based on a combination of K params.
         *
         * @param k Kij represents "to what extent person i prefers person j" defined in the Kano's thesis.
         * @return The index of Heider balance state.
         */
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
                    memo.put(c[key], indexes(c[key], k.length));
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

    private int index(int i, int n, int t) {
        /*
         * Note that this method only handle 2 type particles.
         */
        if (i <= n / t)
            return 0;
        else
            return 1;
    }

    private Integer[] indexes(int n, int len) {
        Integer indexes[] = new Integer[2];
        int i = 0;
        while (true) {
            if (n < i * len) {
                indexes[0] = i - 1;
                indexes[1] = n - (indexes[0] * len);
                break;
            }
            i++;
        }
        return indexes;
    }
}
