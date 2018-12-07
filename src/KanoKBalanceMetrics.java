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

    public double calcHeiderBalanceBasedOnAllTriangle(double[][] k, int n, int type) {
        /*
         * This function calculates the index of Heider balance state based on all triangles in particles.
         *
         * @param k Kij represents "to what extent person i prefers person j" defined in the Kano's thesis.
         * @return The index of Heider balance state.
         */
        Combination combination = new Combination(n, ELEMENT_NUM);
        List<int[]> l = combination.list();

        new Combination(3, 2).print();

        double balance = 0;
        double tmpBalance = 1;

        System.out.println(l.get(0)[0]);
        System.out.println(l.get(0)[1]);
        System.out.println(l.get(0)[2]);
        System.out.println("-----------------");

        balanceWithAverage(k, l.get(0), n, type);
//        for (int[] c : l) {
//            for (int key = 0; key < ELEMENT_NUM; key++) {
        // TODO: Consider how to calculate the balance of one triangle.
//            }
//        }

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

    private BigDecimal balanceWithAverage(double[][] k, int c[], int n, int t) {
//        double balance = 1;w
        BigDecimal balance = new BigDecimal("1.0");
        int iIdx;
        int jIdx;
        BigDecimal tmp1, tmp2;
        for (int comb[] : memo3C2) {
            iIdx = index(c[comb[0]], n, t);
            jIdx = index(c[comb[1]], n, t);
            System.out.println("B: " + balance.doubleValue() + " {i, j} : " + iIdx + ", " + jIdx);
            // TODO: Fix |tmp1| is 1.42 when 0.7 + 0.7.
            tmp1 = BigDecimal.valueOf(k[iIdx][jIdx]).add(BigDecimal.valueOf(k[jIdx][iIdx]));
            System.out.println("kij : " + BigDecimal.valueOf(k[iIdx][jIdx]).doubleValue());
            System.out.println("kji : " + BigDecimal.valueOf(k[jIdx][iIdx]).doubleValue());
            tmp2 = tmp1.divide(BigDecimal.valueOf(2.0));
            System.out.println("1 : " + tmp1 + "2 : " + tmp2);
            balance.multiply(tmp2);
            System.out.println("B: " + balance);
        }
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
