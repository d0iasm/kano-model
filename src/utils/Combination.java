package utils;

import java.util.ArrayList;
import java.util.List;


public class Combination {
    // Reference: https://rosettacode.org/wiki/Combinations#Java

    private int n, k;
    private List<int[]> comb = new ArrayList<>();

    public Combination(int n, int k) {
        if (n < k || n < 1) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.k = k;
        comb = combination();
    }

    public int size() {
        return comb.size();
    }

    public List<int[]> list() {
        return comb;
    }

    public void print() {
        for (int[] i : comb) {
            for (int j=0; j<3; j++) {
                System.out.print(i[j] + ", ");
            }
            System.out.println(" ");
        }
    }

    private int[] findOne(int u) {
        // TODO: Consider this method name.
        int r[] = new int[u];
        int idx = 0;
        for (int n = 0; u > 0; ++n, u >>= 1)
            if ((u & 1) > 0) {
                r[idx] = n;
                idx++;
            }
        return r;
    }

    private int bitcount(int u) {
        int n;
        for (n = 0; u > 0; ++n, u &= (u - 1)) ;
        return n;
    }

    private List<int[]> combination() {
        for (int u = 0; u < 1 << n; u++) {
            System.out.println(u);
            if (bitcount(u) == k) comb.add(findOne(u));
        }
        return comb;
    }

    public static void main(String[] args) {
        Combination c = new Combination(4, 3);
        List<int[]> l = c.list();
        System.out.println("The number of combinations is " + c.size());
        for (int[] i : l) {
            for (int j=0; j<3; j++) {
                System.out.print(i[j] + ", ");
            }
            System.out.println(" ");
        }
    }
}