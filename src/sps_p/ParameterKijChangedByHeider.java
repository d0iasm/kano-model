package sps_p;

/**
 * [Deprecated] Same of ParametreKij class, but has the methods to change Parameter K.
 * This class will be deleted because the methods to change parameters was failed.
 */
public class ParameterKijChangedByHeider extends Parameter {
    private int paramChangedCount = 0;

    ParameterKijChangedByHeider(int num, int type, Swarm swarm) {
        super(num, type, swarm);
    }

    public int getParamChangedCount() {
        return this.paramChangedCount;
    }

    public void setParamChangedCount(int n) {
        this.paramChangedCount = n;
    }

    /**
     * Change parameters depends on kSum.
     * double[][] kSums = new double[][]{
     * {0, 0, 0},
     * {0, 0, 0},
     * {0, 0, 0},
     * };
     *
     * @param kSums Define as "kSums[(p1.id - 1) / pPartition][(p2.id - 1) / pPartition] = tmpX + tmpY;" in sps_p.Swarm::run().
     */
    public void flipKParamSimpleHeider(double[][] kSums) {
        if (kSums[0][1] * kSums[1][2] * kSums[2][0] < 0) {
            int base = (int) (Math.random() * 3);
            params[base][base + 1 > 2 ? 0 : base + 1] = -params[base][base + 1 > 2 ? 0 : base + 1];
            System.out.println("1: Flip k param " + base);
            return;
        }

        if (kSums[0][2] * kSums[2][1] * kSums[1][0] < 0) {
            int base = (int) (Math.random() * 3);
            params[base][base + 1 > 2 ? 0 : base + 1] = -params[base][base + 1 > 2 ? 0 : base + 1];
            System.out.println("2: Flip k param " + base);
            return;
        }
    }

    public void flipKParamHeider(double[][] kSums) {
        int perceiver = (int) (Math.random() * 3);
        int other = (int) (Math.random() * 3);
        while (other == perceiver) {
            other = (int) (Math.random() * 3);
        }
        int x = 3 - perceiver - other;

        if (kSums[perceiver][other] * kSums[perceiver][x] * kSums[other][x] < 0) {
            paramChangedCount += 1;
            params[perceiver][other] = -params[perceiver][other];
        }
    }

    public void changeKParamHeider(double[][] kSums) {
        int perceiver = (int) (Math.random() * 3);
        int other = (int) (Math.random() * 3);
        while (other == perceiver) {
            other = (int) (Math.random() * 3);
        }
        int x = 3 - perceiver - other;
        double offset = 0.1;

        if (kSums[perceiver][other] * kSums[perceiver][x] * kSums[other][x] < 0) {
            paramChangedCount += 1;
            if (kSums[perceiver][other] < 0 && params[perceiver][other] < 2.0 - offset) {
                params[perceiver][other] += offset;
            } else if (-2.0 + offset < params[perceiver][other]) {
                params[perceiver][other] -= offset;
            }
        }
    }

    public void balanceKParamHeiderHelper(int x, int y, double kDiff, boolean isPlus) {
        double tmp = params[x][y];
        if (kDiff != 0) {
            paramChangedCount += 1;
        }
        if (isPlus) {
            params[x][y] = Math.min(2.0, params[x][y] + kDiff);
        } else {
            params[x][y] = Math.max(-2.0, params[x][y] - kDiff);
        }
    }

    public void balanceKParamHeider(double[][] kSums) {
        int perceiver = (int) (Math.random() * 3);
        int other = (int) (Math.random() * 3);
        while (other == perceiver) {
            other = (int) (Math.random() * 3);
        }
        int x = 3 - perceiver - other;
        double kPO, kPX, kDiff;
        boolean isKPOBigger;

        if (kSums[perceiver][other] * kSums[perceiver][x] * kSums[other][x] < 0) {
            kPO = Math.abs(params[perceiver][other]);
            kPX = Math.abs(params[perceiver][x]);
            isKPOBigger = kPO > kPX ? true : false;
            kDiff = isKPOBigger ? kPO - kPX : kPX - kPO;
            kDiff /= 2;
            kDiff *= 10;
            kDiff = Math.floor(kDiff);
            kDiff /= 10;

            if (kSums[perceiver][other] < 0 && kSums[other][x] < 0) {
                if (isKPOBigger) {
                    balanceKParamHeiderHelper(perceiver, x, kDiff, true);
                } else {
                    balanceKParamHeiderHelper(perceiver, other, kDiff, true);
                }
            } else if (kSums[perceiver][other] < 0 && kSums[perceiver][x] >= 0) {
                if (isKPOBigger) {
                    balanceKParamHeiderHelper(perceiver, x, kDiff, false);
                } else {
                    balanceKParamHeiderHelper(perceiver, other, kDiff, true);
                }
            } else if (kSums[perceiver][other] >= 0 && kSums[perceiver][x] < 0) {
                if (isKPOBigger) {
                    balanceKParamHeiderHelper(perceiver, x, kDiff, true);
                } else {
                    balanceKParamHeiderHelper(perceiver, other, kDiff, false);
                }
            } else if (kSums[perceiver][other] >= 0 && kSums[perceiver][x] >= 0) {
                if (isKPOBigger) {
                    balanceKParamHeiderHelper(perceiver, x, kDiff, false);
                } else {
                    balanceKParamHeiderHelper(perceiver, other, kDiff, false);
                }
            }
        }
    }

    public void flipKParamNewcomb(double[][] kSums) {
        int a = (int) (Math.random() * 3);
        int b = (int) (Math.random() * 3);
        while (a == b) {
            b = (int) (Math.random() * 3);
        }
        int x = 3 - a - b;

        if (kSums[a][x] * kSums[b][x] < 0) {
            System.out.println("Flip k param " + a + " -> " + x + " : " + params[a][x]);
            params[a][x] = -params[a][x];
        }
    }

    public void changeKParamNewcomb(double[][] kSums) {
        int a = (int) (Math.random() * 3);
        int b = (int) (Math.random() * 3);
        while (a == b) {
            b = (int) (Math.random() * 3);
        }
        int x = 3 - a - b;
        double offset = 0.1;

        if (kSums[a][x] * kSums[b][x] < 0) {
            System.out.println("Change k param " + a + " -> " + x + " : " + params[a][x]);
            if (kSums[a][x] < 0 && params[a][x] < 2.0) {
                params[a][x] += offset;
            } else if (-2.0 < params[a][x]) {
                params[a][x] -= offset;
            }
        }
    }

    public void memeNewcomb(double[][] kSums) {
        int a = (int) (Math.random() * 3);
        int b = (int) (Math.random() * 3);
        while (a == b) {
            b = (int) (Math.random() * 3);
        }
        int x = 3 - a - b;
        double offset;

        if (kSums[a][x] * kSums[b][x] < 0) {
            System.out.println("Change k param " + a + " -> " + x + " : " + params[a][x]);
            if (kSums[a][x] < 0) {
                offset = params[b][x] / 10;
                params[a][x] += offset;
                params[b][x] -= offset;
            } else {
                offset = params[a][x] / 10;
                params[a][x] -= offset;
                params[b][x] += offset;
            }
        }
    }

    @Override
    double[][] init2x2() {
        // Periodic boundary
        // Fig. 1 in the thesis.
        // Create a circle.
//        double[][] params = {
//                {1.0, 0.9},
//                {0.9, 0.8},
//        };

        // Periodic boundary
        // Fig. 2 in the thesis.
        // Divide particles into red and blue
//        double[][] params = {
//                {1.0, 0.8},
//                {0.8, 0.9},
//        };

        // Open/periodic boundary
        // Fig 3 and 4 in the thesis.
        // Red particles chase blue ones
//        double[][] params = {
//                {1.0, 1.0},
//                {0.5, 1.3},
//        };

        // Periodic boundary
        // Fig. 5 in the thesis.
        // Move to the same direction while keeping their position.
//        double[][] params = {
//                {0.9, 1.2},
//                {0.5, 0.9},
//        };

        // Periodic boundary
        // Fig. 6 in the thesis.
        // Keep each particle's position.
//        double[][] params = {
//                {0.7, 1.2},
//                {0.5, 0.7},
//        };

        // Separation and fusion
        // Fig.7 in the thesis.
//        double[][] params = {
//                {0.8, 1.1},
//                {0.6, 1.0}
//        };

//        Like >--< this form
        // Fig. 8 in the thesis.
        double[][] params = {
                {0.8, 1.7},
                {0.5, 1.2}
        };

        // Periodic boundary
        // Funny
//        double[][] params = {
//                {1.3, 1.4},
//                {0.7, 0.8},
//        };

        return params;
    }

    @Override
    double[][] init3x3() {
//        Balance
//    double[][] params = {
//                {0.0, 1.0, 1.0},
//                {1.0, 0.0, 1.0},
//                {1.0, 1.0, 0.0},
//        };

//    Balance
//    double[][] params = {
//            {0.0, 1.0, -1.0},
//            {1.0, 0.0, -1.0},
//            {-1.0, -1.0, 0.0},
//    };

//    Balance same to the above
//    double[][] params = {
//            {0.0, -1.0, 1.0},
//            {-1.0, 0.0, -1.0},
//            {1.0, -1.0, 0.0},
//    };

//    Balance same to the above
//    double[][] params = {
//            {0.0, -1.0, -1.0},
//            {-1.0, 0.0, 1.0},
//            {-1.0, 1.0, 0.0},
//    };

//    Unbalance ?
//    double[][] params = {
//            {0.0, -1.0, -1.0},
//            {-1.0, 0.0, -1.0},
//            {-1.0, -1.0, 0.0},
//    };


//    Spin as one big cluster at the center
//        double[][] params = {
//                {0.0, 1.4, 1.5},
//                {1.5, 0.0, 1.4},
//                {1.4, 1.5, 0.0},
//        };

        //        Attack and complicated movement NOT SAME to a paper
//    double[][] params = {
//            {1.1, 0.0, 1.5},
//            {1.5, 1.1, 0.0},
//            {0.0, 1.5, 1.1},
//    };

        //        Spin as a small cluster
        // GOOD with changeKParamNewcomb()
//        double[][] params = {
//                {-0.5, 1.0, 1.4},
//                {1.4, -0.5, 1.0},
//                {1.0, 1.4, -0.5},
//        };

//        Spin like a film
//    GOOD with changeKParamNewcomb()
//        double[][] params = {
//                {-0.1, 1.0, 1.4},
//                {1.4, -0.1, 1.0},
//                {1.0, 1.4, -0.1},
//        };

//        Spin speedy with making a cluster with the same type
//        double[][] params = {
//                {1.3, 0.0, 1.5},
//                {1.5, 1.3, 0.0},
//                {0.0, 1.5, 1.3}
//        };

        // Periodic boundary
        // each type lines up.
//        double[][] params = {
//                {0.0, -0.2, -0.4},
//                {-0.4, 0.0, -0.2},
//                {-0.2, -0.4, 0.0},
//        };

        // Periodic boundary
        // Each cluster moves to the same direction
//        double[][] params = {
//                {1.1, 0.0, 1.5},
//                {1.5, 1.1, 0.0},
//                {0.0, 1.5, 1.1},
//        };

        double[][] params = {
                {0.0, -0.7, 0.7},
                {0.7, 0.0, -0.7},
                {-0.7, 0.7, 0.0},
        };

        return params;
    }

    @Override
    double[][] random() {
        double[][] params = new double[pType][pType];
        double tmp;
        for (int i = 0; i < pType; i++) {
            for (int j = 0; j < pType; j++) {
                tmp = -2.0 + Math.random() * 4.0;
                tmp *= 10;
                tmp = Math.floor(tmp);
                tmp /= 10;
                params[i][j] = tmp;
            }
        }
        return params;
    }
}
