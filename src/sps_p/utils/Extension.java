package sps_p.utils;


final public class Extension {
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

    static public void printPairs(Pair... pairs) {
        for (Pair pair : pairs) {
            System.out.println(pair.x + ", " + pair.y);
        }
    }
}
