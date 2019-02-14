package sps_p.metrics;

import sps_p.utils.Combination;
import sps_p.utils.Permutation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculate the stable value besed on Heider balance theory.
 */
public class KanoKBalanceMetrics implements Metrics {
    private static Metrics instance = new KanoKBalanceMetrics();
    private final int TRIANGLE = 3;
    private final List<int[]> MEMO_3C2 = new Combination(3, 2).list();
    private final List<int[]> MEMO_3P3 = new Permutation(3, 3).list();

    public static Metrics getInstance() {
        return instance;
    }

    /**
     * This constructor disallow to create multiple instances because of a private constructor.
     */
    private KanoKBalanceMetrics() {
    }

    @Override
    public void debug() {
        System.out.println("Kano sps_p.ParameterKij Balance sps_p.metrics.Metrics");
    }

    /**
     * Calculates the index of Heider balance state based on all triangles in particles.
     *
     * @param k ParameterKij represents "to what extent person i prefers person j" defined in the Kano's thesis.
     * @return The average of Heider balance state per a triangle.
     */
    public BigDecimal calcHeiderBalanceBasedOnAllTriangle(double[][] k, int n, int type) {
        Combination combination = new Combination(n, TRIANGLE);
        List<int[]> l = combination.list();
        int tNum = combination.size();

        BigDecimal balance = new BigDecimal(0);
        for (int[] c : l) {
            balance = balance.add(balanceWithPOX(k, c, n, type));
//            balance = balance.add(balanceWithAverage(k, c, n, type));
        }

        return balance.divide(new BigDecimal(tNum), MathContext.DECIMAL32);
    }

    /**
     * [DEPRECATED and NOT IMPLEMENTED YET]
     * This function calculates the index of Heider balance state based on a combination of K params.
     *
     * @param k Parameters represents "to what extent person i prefers person j" defined in the Kano's thesis.
     * @return The index of Heider balance state.
     */
    public double calcHeiderBalanceBasedOnK(double[][] k) {
        Combination combination = new Combination(k.length * k[0].length, TRIANGLE);

        System.out.println("combination results");
        List<int[]> l = combination.list();
        combination.print();
        System.out.println(combination.size());

        double balance = 0;
        double tmpBalance = 1;

        Map<Integer, Integer[]> memo = new HashMap<>();
        for (int[] c : l) {
            for (int key = 0; key < TRIANGLE; key++) {
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

    /**
     * Calculate the Heider balance state with an average of Parameters and Kji.
     * i.e. c[] = {1, 4, 39};
     * There is the 3 connections in c[] defined MEMO_3C2.
     * {1, 4}, {1, 39}, {4, 39}
     * Calculate the average of each combination.
     * (K(1->4) + K(4->1)) / 2
     * (K(1->39) + K(39->1)) / 2
     * (K(4->39) + K(39->3)) / 2
     * Then, multiply all averages.
     *
     * @param k Parameters represents "to what extent person i prefers person j" defined in the Kano's thesis.
     * @param c The 3 particle's indexes.
     * @param n The total number of particles.
     * @param t The number of type.
     * @return Result of Heider balance state.
     */
    private BigDecimal balanceWithAverage(double[][] k, int c[], int n, int t) {
        BigDecimal balance = new BigDecimal(1);
        int iIdx;
        int jIdx;
        BigDecimal tmp1;
        BigDecimal tmp2;
        BigDecimal DIVISOR = new BigDecimal(2);

        for (int comb[] : MEMO_3C2) {
            iIdx = index(c[comb[0]], n, t);
            jIdx = index(c[comb[1]], n, t);
            tmp1 = BigDecimal.valueOf(k[iIdx][jIdx]).add(BigDecimal.valueOf(k[jIdx][iIdx]));
            tmp2 = tmp1.divide(DIVISOR);
            balance = balance.multiply(tmp2);
        }
        return balance;
    }

    /**
     * Calculate the Heider balance state with 6 pattens average of POX.
     * In HB theory, only P->O, P->X, O->X directions are valid.
     * i.e. c[] = {1, 4, 39};
     * All possible patterns are 3! = 3P3 = 6.
     * The number of patterns is always 6 and defined by MEMO_3P3.
     * P  |  O  |  X
     * ===============
     * 1  |  4  |  39
     * 1  |  39 |  4
     * 4  |  1  |  39
     * 4  |  39 |  1
     * 39 |  1  |  4
     * 39 |  4  |  1
     * <p>
     * Calculate the average of the sum of each pattern.
     * ( K(1->4) * K(1->39) * K(4->39)
     * + K(1->39) * K(1->4) * K(4->39)
     * + K(4->1) * K(4->39) * K(1->39)
     * + ....
     * ) / 6
     *
     * @param k Parameters represents "to what extent person i prefers person j" defined in the Kano's thesis.
     * @param c The 3 particle's indexes.
     * @param n The total number of particles.
     * @param t The number of type.
     * @return Result of Heider balance state.
     */
    private BigDecimal balanceWithPOX(double[][] k, int c[], int n, int t) {
        BigDecimal balance = new BigDecimal(0);
        int pIdx;
        int oIdx;
        int xIdx;
        BigDecimal tmp;
        BigDecimal DIVISOR = new BigDecimal(6);

        for (int[] perm : MEMO_3P3) {
            pIdx = index(c[perm[0]], n, t);
            oIdx = index(c[perm[1]], n, t);
            xIdx = index(c[perm[2]], n, t);
            tmp = BigDecimal.valueOf(k[pIdx][oIdx])
                    .multiply(BigDecimal.valueOf(k[pIdx][xIdx]))
                    .multiply(BigDecimal.valueOf(k[oIdx][xIdx]));
            balance = balance.add(tmp);
        }
        // > DECIMAL32 (https://docs.oracle.com/javase/8/docs/api/java/math/MathContext.html)
        // > "A MathContext object with a precision setting matching the IEEE 754R Decimal32 format,
        // 7 digits, and a rounding mode of HALF_EVEN, the IEEE 754R default."
        balance = balance.divide(DIVISOR, MathContext.DECIMAL32);
        return balance;
    }

    /**
     * Note that this method only handle 2 type particles.
     *
     * @param i The index of a particle.
     * @param n The total number of particles.
     * @param t The number of type.
     * @return The index for Parameters array.
     */
    private int index(int i, int n, int t) {
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
