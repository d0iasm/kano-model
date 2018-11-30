import java.util.ArrayList;
import java.util.List;

final class Metrics {
    private static Metrics instance = new Metrics();
    private List<int[]> nbals = new ArrayList<>(1000);

    // This constructor disallow to create multiple instances because of a private constructor.
    private Metrics() {}

    public static Metrics getInstance() {
        return instance;
    }

    public void addNbals(int[] counts) {
        /*
         *
         */
        nbals.add(counts);
    }

    public void printNbals() {
        System.out.println("===================== Balance ========================");
        for (int[] nbal : nbals) {
            System.out.println(nbal[0]);
        }
        System.out.println("===================== Imbalance ========================");
        for (int[] nbal : nbals) {
            System.out.println(nbal[1]);
        }
    }
}
