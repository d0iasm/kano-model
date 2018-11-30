public final class Extension {
    public static void printSwarmParam(double[][] params, int count) {
        System.out.println("----------- Print current kParams --------------");
        System.out.println("count: " + count);
        for (int i = 0; i < params.length; i++) {
            for (int j = 0; j < params[0].length; j++) {
                System.out.print(params[i][j] + ", ");
            }
            System.out.println(" ");
        }
    }
}
