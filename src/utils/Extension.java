package utils;


import java.math.BigDecimal;

final public class Extension {
    static public final class Pair<V> {
        String key;
        V val;
        public Pair (String k, V v) {
            key = k;
            val = v;
        }
    }

    static public void printSwarmParam(BigDecimal[][] params, int count) {
        System.out.println("----------- Print current kParams --------------");
        System.out.println("count: " + count);
        for (BigDecimal[] param : params) {
            for (int j = 0; j < params[0].length; j++) {
                System.out.print(param[j] + ", ");
            }
            System.out.println(" ");
        }
    }

    static public void printSwarmParam(double[][] params, int count) {
        System.out.println("----------- Print current kParams --------------");
        System.out.println("count: " + count);
        for (double[] param : params) {
            for (int j = 0; j < params[0].length; j++) {
                System.out.print(param[j] + ", ");
            }
            System.out.println(" ");
        }
    }

    static public void printArgs(Object... args) {
        for (Object arg : args) {
            System.out.println(arg.getClass().getName() + ": " + arg);
        }
    }

    static public void printArgs(Pair... pairs) {
        for (Pair pair : pairs) {
            System.out.println(pair.key + ": " + pair.val);
        }
    }
}
