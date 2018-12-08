import utils.Combination;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class KanoKBalanceMetrics implements Metrics {
    private static Metrics instance = new KanoKBalanceMetrics();
    private final int ELEMENT_NUM = 3;
    private List<int[]> memo3C2 = new Combination(3, 2).list();

    public static Metrics getInstance() {
        return instance;
    }

    private KanoKBalanceMetrics() {
    }

    @Override
    public void debug() {
        System.out.println("Kano Kij Balance Metrics");
    }

    public BigDecimal calcHeiderBalanceBasedOnAllTriangle(double[][] k, int n, int type) {
        /**
         * This function calculates the index of Heider balance state based on all triangles in particles.
         *
         * @param k Kij represents "to what extent person i prefers person j" defined in the Kano's thesis.
         * @return The average of Heider balance state per a triangle.
         */
        Combination combination = new Combination(n, ELEMENT_NUM);
        List<int[]> l = combination.list();
        int tNum = combination.size();

        BigDecimal balance = new BigDecimal(0);
        for (int[] c : l) {
            balance = balance.add(balanceWithAverage(k, c, n, type));
        }

        return balance.divide(new BigDecimal(tNum));
    }

    public double calcHeiderBalanceBasedOnK(double[][] k) {
        /**
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
            }
            balance += tmpBalance;
            tmpBalance = 1;
        }
        return balance;
    }

    private BigDecimal balanceWithAverage(double[][] k, int c[], int n, int t) {
        /**
         * Calculate the Heider balance state with an average of Kij and Kji.
         * i.e. c[] = {1, 4, 39};
         *      There is the 3 connections in c[] defined memo3C2.
         *      {1, 4}, {1, 39}, {4, 39}
         *      Calculate the average of each combination.
         *      (K(1->4) + K(4->1)) / 2
         *      (K(1->39) + K(39->1)) / 2
         *      (K(4->39) + K(39->3)) / 2
         *      Then, multiply all averages.
         *
         * @param k Kij represents "to what extent person i prefers person j" defined in the Kano's thesis.
         * @param c The 3 particle's indexes.
         * @param n The total number of particles.
         * @param t The number of type.
         * @return result of Heider balance state.
         */
        BigDecimal balance = new BigDecimal(1);
        int iIdx;
        int jIdx;
        BigDecimal tmp1;
        BigDecimal tmp2;
        BigDecimal DIVISOR = new BigDecimal(2);

        for (int comb[] : memo3C2) {
            iIdx = index(c[comb[0]], n, t);
            jIdx = index(c[comb[1]], n, t);
            tmp1 = BigDecimal.valueOf(k[iIdx][jIdx]).add(BigDecimal.valueOf(k[jIdx][iIdx]));
            tmp2 = tmp1.divide(DIVISOR);
            balance = balance.multiply(tmp2);
        }
        return balance;
    }

    private int index(int i, int n, int t) {
        /**
         * Note that this method only handle 2 type particles.
         *
         * @param i The index of a particle.
         * @param n The total number of particles.
         * @param t The number of type.
         * @return The index for Kij array.
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
