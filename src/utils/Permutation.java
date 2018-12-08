package utils;

import java.util.ArrayList;
import java.util.List;

public class Permutation {
    private int n;
    private int k;
    private List<int[]> perm = new ArrayList<>();

    public Permutation(int n, int k) {
        if (n < k || n < 1) {
            throw new IllegalArgumentException();
        }

        this.n = n;
        this.k = k;
        int[] origin = new int[k];
        for (int i = 0; i < k; i++) origin[i] = i;
        permutation(origin, 0, k - 1);
    }

    public int size() {
        return perm.size();
    }

    public List<int[]> list() {
        return perm;
    }

    public void print() {
        for (int[] i : perm) {
            for (int j = 0; j < k; j++) {
                System.out.print(i[j] + ", ");
            }
            System.out.println(" ");
        }
        System.out.println("size is " + size());
    }

    /**
     * Swap an array at the position.
     *
     * @param a Int array.
     * @param i Position 1.
     * @param j Position 2.
     * @return Swapped array.
     */
    private int[] swap(int[] a, int i, int j) {
        int tmp;
        tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        return a;
    }

    /**
     * This function directly changes the instance variable of |perm|.
     *
     * @param ans Original array to calculate permutation for.
     * @param l Start index.
     * @param r End index.
     */
    private void permutation(int[] ans, int l, int r) {
        if (l == r)
            perm.add(ans.clone());
        else {
            for (int i = l; i <= r; i++) {
                ans = swap(ans, l, i);
                permutation(ans, l + 1, r);
                ans = swap(ans, l, i);
            }
        }
    }

    /**
     * This is the example usage of this class.
     */
    public static void main(String[] args) {
        Permutation p = new Permutation(3, 3);
        p.print();
    }
}
