package metrics;

import java.util.ArrayList;
import java.util.List;


final class SocialMitosisMetrics implements Metrics {
    private static Metrics instance = new SocialMitosisMetrics();
    private List<int[]> nbals = new ArrayList<>(1000);

    /**
     * This constructor disallow to create multiple instances because of a private constructor.
     */
    private SocialMitosisMetrics() {
    }

    public static Metrics getInstance() {
        return instance;
    }

    @Override
    public void debug() {
        System.out.println("Social Mitosis metrics.Metrics");
    }

    private int isLike(double preDis, double newDis) {
        if (newDis <= preDis) {
            return 1;
        }
        return -1;
    }

    private boolean isBalance(double preDisIJ, double newDisIJ, double preDisIK, double newDisIK, double preDisJK, double newDisJK) {
        return isLike(preDisIJ, newDisIJ) * isLike(preDisIK, newDisIK) * isLike(preDisJK, newDisJK) > 0;
    }

    // TODO: Currently cannot use it because distance() and distanceClosest() are defined in Swarm class.
//    public int[] nbal(List<Double> preX, List<Double> preY, List<Double> newX, List<Double> newY) {
    /*
     * > The function nbal counted the number of all triads that were balanced,
     * > imbalanced or incomplete (contained at least one null relation).
     * > The nbal function was used only to determine how many iterations were
     * > needed inside the balance function before changes in relations ceased.
     * > It was not a part of the regular simulation.
     * 2.4 (http://jasss.soc.surrey.ac.uk/6/3/2.html)
     *
     * @return counts The first element means the number of balanced triads and
     *  the second one means the number of imbalanced triads.
     */

//        int counts[] = {0, 0};
//        for (int i = 0; i < pNum; i++) {
//            for (int j = i + 1; j < pNum; j++) {
//                for (int k = j + 1; k < pNum; k++) {
//                     TODO: This function only be correct when an open boundary state.
//                    switch (boundary) {
//                        case OPEN:
//                            if (isBalance(
//                                    distance(preX.get(i), preY.get(i), preX.get(j), preY.get(i)),
//                                    distance(newX.get(i), newY.get(i), newX.get(j), newY.get(j)),
//                                    distance(preX.get(i), preY.get(i), preX.get(k), preY.get(k)),
//                                    distance(newX.get(i), newY.get(i), newX.get(k), newY.get(k)),
//                                    distance(preX.get(j), preY.get(j), preX.get(k), preY.get(k)),
//                                    distance(newX.get(j), newY.get(j), newX.get(k), newY.get(k))
//                            )) {
//                                counts[0] += 1;
//                            } else {
//                                counts[1] += 1;
//                            }
//                            break;
//                        case PERIODIC:
//                            if (isBalance(
//                                    distanceClosest(preX.get(i), preY.get(i), preX.get(j), preY.get(i)),
//                                    distanceClosest(newX.get(i), newY.get(i), newX.get(j), newY.get(j)),
//                                    distanceClosest(preX.get(i), preY.get(i), preX.get(k), preY.get(k)),
//                                    distanceClosest(newX.get(i), newY.get(i), newX.get(k), newY.get(k)),
//                                    distanceClosest(preX.get(j), preY.get(j), preX.get(k), preY.get(k)),
//                                    distanceClosest(newX.get(j), newY.get(j), newX.get(k), newY.get(k))
//                            )) {
//                                counts[0] += 1;
//                            } else {
//                                counts[1] += 1;
//                            }
//                            break;
//                        default:
//                            if (isBalance(
//                                    distance(preX.get(i), preY.get(i), preX.get(j), preY.get(i)),
//                                    distance(newX.get(i), newY.get(i), newX.get(j), newY.get(j)),
//                                    distance(preX.get(i), preY.get(i), preX.get(k), preY.get(k)),
//                                    distance(newX.get(i), newY.get(i), newX.get(k), newY.get(k)),
//                                    distance(preX.get(j), preY.get(j), preX.get(k), preY.get(k)),
//                                    distance(newX.get(j), newY.get(j), newX.get(k), newY.get(k))
//                            )) {
//                                counts[0] += 1;
//                            } else {
//                                counts[1] += 1;
//                            }
//                    }
//                }
//            }
//        }
//        return counts;
//    }


    /**
     * > The function nbal counted the number of all triads that were balanced,
     * > imbalanced or incomplete (contained at least one null relation).
     * > The nbal function was used only to determine how many iterations were
     * > needed inside the balance function before changes in relations ceased.
     * > It was not a part of the regular simulation.
     * 2.4 (http://jasss.soc.surrey.ac.uk/6/3/2.html)
     *
     * Usage:
     *   Create new variables to store previous step position and in Swarm::run().
     *     List<Double> preX = new ArrayList<>(pNum);
     *     List<Double> preY = new ArrayList<>(pNum);
     *
     *     for (Particle p1 : particles) {
     *       preX.add(p1.x);
     *       preY.add(p1.y);
     *       ....
     *     }
     *
     *     metrics.addNbals(nbal(preX, preY, newX, newY));
     *
     * @param counts The first element means the number of balanced triads and
     *               the second one means the number of imbalanced triads.
     */
    public void addNbals(int[] counts) {
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

    /**
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
    public void subgroups() {
    }
}
