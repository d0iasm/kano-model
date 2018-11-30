final class Extension {
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
}
