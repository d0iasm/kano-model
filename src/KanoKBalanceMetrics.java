public class KanoKBalanceMetrics implements Metrics {
    private static Metrics instance = new KanoKBalanceMetrics();

    public static Metrics getInstance() {
        return instance;
    }

    private KanoKBalanceMetrics() {
    }

    @Override
    public void debug() {
        System.out.println("Kano Kij Balance Metrics");
    }
}
