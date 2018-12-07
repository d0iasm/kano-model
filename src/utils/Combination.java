package utils;

import java.util.ArrayList;
import java.util.List;


public class Combination {
    // Reference: https://rosettacode.org/wiki/Combinations#Java

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
            for (int j = 0; j < 3; j++) {
                System.out.print(i[j] + ", ");
            }
            System.out.println(" ");
        }
        System.out.println("size is " + size());
    }

    private int[] findOne(long u) {
        // TODO: Consider this method name.
        System.out.println(u);
        // OutOfMemoryError : Java heap space
        int r[] = new int[(int) u];
        int idx = 0;
        for (int n = 0; u > 0; ++n, u >>= 1)
            if ((u & 1) > 0) {
                r[idx] = n;
                idx++;
            }
        return r;
    }

    private long bitcount(long u) {
        long n;
        for (n = 0; u > 0; ++n, u &= (u - 1)) ;
        return n;
    }

    private List<int[]> combinationWithError() {
        /*
         * Don't use this method because it happens OutOfMemoryError : Java heap space on the way to findOne().
         * However, it is ok in the case |n| is a small number.
         */
        List<int[]> comb = new ArrayList<>();
        for (long u = 0; u < 1 << n; u++) {
            if (bitcount(u) == k) {
                comb.add(findOne(u));
            }
        }
        return comb;
    }

    private List<int[]> combination() {
        List<int[]> comb = new ArrayList<>();

        int one[] = new int[k];

        // position of current index
        //  if (r = 1)              r*
        //  index ==>        0   |   1   |   2
        //  element ==>      A   |   B   |   C
        int r = 0;
        int index = 0;

        while (r >= 0) {
            // possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
            // possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"

            // for r = 0 ==> index < (4+ (0 - 2)) = 2
            if (index <= (n + (r - k))) {
                one[r] = index;

                // if we are at the last position print and increase the index
                if (r == k - 1) {
//                    for (int i : one) {
//                        System.out.print(i);
//                    }
//                    System.out.println(" ");
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

    public static void main(String[] args) {
        /*
         * This is the example usage of this class.
         */
        Combination c = new Combination(50, 3);
        c.print();
    }
}