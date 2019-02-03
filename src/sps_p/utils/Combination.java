package sps_p.utils;

import java.util.ArrayList;
import java.util.List;


public class Combination {
    private int n, k;
    private List<int[]> comb;

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
            for (int j = 0; j < k; j++) {
                System.out.print(i[j] + ", ");
            }
            System.out.println(" ");
        }
        System.out.println("size is " + size());
    }

    /**
     * Reference: http://hmkcode.com/calculate-find-all-possible-combinations-of-an-array-using-java/
     */
    private List<int[]> combination() {
        List<int[]> comb = new ArrayList<>();
        int one[] = new int[k];
        int r = 0;
        int index = 0;

        while (r >= 0) {
            if (index <= (n + (r - k))) {
                one[r] = index;

                // If we are at the last position, add to the combination list and increase the index.
                if (r == k - 1) {
                    comb.add(one.clone());
                    index++;
                } else {
                    index = one[r] + 1;
                    r++;
                }
            } else {
                r--;
                if (r > 0)
                    index = one[r] + 1;
                else
                    index = one[0] + 1;
            }
        }
        return comb;
    }

    /**
     * This is the example usage of this class.
     */
    public static void main(String[] args) {
        Combination c = new Combination(50, 3);
        c.print();
    }
}