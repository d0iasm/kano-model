final class Extension {
    static final class Pair<V> {
        String key;
        V val;
        Pair (String k, V v) {
            key = k;
            val = v;
        }
    }

    static void printSwarmParam(double[][] params, int count) {
        System.out.println("----------- Print current kParams --------------");
        System.out.println("count: " + count);
        for (double[] param : params) {
            for (int j = 0; j < params[0].length; j++) {
                System.out.print(param[j] + ", ");
            }
            System.out.println(" ");
        }
    }

    static void printArgs(Object... args) {
        for (Object arg : args) {
            System.out.println(arg.getClass().getName() + ": " + arg);
        }
    }

    static void printArgs(Pair... pairs) {
        for (Pair pair : pairs) {
            System.out.println(pair.key + ": " + pair.val);
        }
    }
}
