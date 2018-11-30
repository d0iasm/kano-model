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
         * > The function nbal counted the number of all triads that were balanced,
         * > imbalanced or incomplete (contained at least one null relation).
         * > The nbal function was used only to determine how many iterations were
         * > needed inside the balance function before changes in relations ceased.
         * > It was not a part of the regular simulation.
         * 2.4 (http://jasss.soc.surrey.ac.uk/6/3/2.html)
         *
         * @param counts The first element means the number of balanced triads and
         *  the second one means the number of imbalanced triads.
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

    public void subgroups() {
        /*
         * > The function subgroups employed a cheap programming trick to determine
         * > which persons in a 9-person or a 16-person group or a 25-person group
         * > liked and disliked the same people. Two people were considered to be part
         * > of the same subgroup if (1) they liked each other, and (2) they liked and
         * > disliked the same remaining people. The output of the balance function
         * > became the input of the subgroups function. The output of the subgroups
         * > function was a list of who was in each of the subgroups found. From this list,
         * > the number of groups and their size could be calculated.
         * 2.5 (http://jasss.soc.surrey.ac.uk/6/3/2.html)
         */
    }
}
