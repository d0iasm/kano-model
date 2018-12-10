import java.math.BigDecimal;


public class Kij<pulic> extends Parameter{
    private int paramChangedCount = 0;

    Kij(int dimension) {
        super(dimension);
    }

    public int getParamChangedCount() {
        return this.paramChangedCount;
    }

    public void setParamChangedCount(int n) {
        this.paramChangedCount = n;
    }

    public void flipKParamSimpleHeider(double[][] kSums) {
        if (kSums[0][1] * kSums[1][2] * kSums[2][0] < 0) {
            int base = (int) (Math.random() * 3);
            params[base][base + 1 > 2 ? 0 : base + 1] = params[base][base + 1 > 2 ? 0 : base + 1].negate();
            System.out.println("1: Flip k param " + base);
            return;
        }

        if (kSums[0][2] * kSums[2][1] * kSums[1][0] < 0) {
            int base = (int) (Math.random() * 3);
            params[base][base + 1 > 2 ? 0 : base + 1] = params[base][base + 1 > 2 ? 0 : base + 1].negate();
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
            params[perceiver][other] = params[perceiver][other].negate();
        }
    }

    public void changeKParamHeider(double[][] kSums) {
        int perceiver = (int) (Math.random() * 3);
        int other = (int) (Math.random() * 3);
        while (other == perceiver) {
            other = (int) (Math.random() * 3);
        }
        int x = 3 - perceiver - other;
        final BigDecimal TWO = new BigDecimal(2);
        final BigDecimal OFFSET = new BigDecimal(0.1);

        if (kSums[perceiver][other] * kSums[perceiver][x] * kSums[other][x] < 0) {
            paramChangedCount += 1;
            if (kSums[perceiver][other] < 0 && params[perceiver][other].compareTo(TWO.subtract(OFFSET)) < 0) {
                params[perceiver][other] = params[perceiver][other].add(OFFSET);
            } else if (params[perceiver][other].compareTo(OFFSET.subtract(TWO)) > 0) {
                params[perceiver][other] = params[perceiver][other].subtract(OFFSET);
            }
        }
    }

    void balanceKParamHeiderHelper(int x, int y, BigDecimal kDiff, boolean isPlus) {
        BigDecimal tmp = params[x][y];
        final BigDecimal TWO = new BigDecimal(2);
        if (!kDiff.equals(0)) {
            paramChangedCount += 1;
        }
        if (isPlus) {
            params[x][y] = TWO.min(params[x][y].add(kDiff));
        } else {
            params[x][y] = TWO.min
                    (params[x][y].subtract(kDiff));
        }
    }

    public void balanceKParamHeider(double[][] kSums) {
        int perceiver = (int) (Math.random() * 3);
        int other = (int) (Math.random() * 3);
        while (other == perceiver) {
            other = (int) (Math.random() * 3);
        }
        int x = 3 - perceiver - other;
        BigDecimal kPO, kPX, kDiff;
        boolean isKPOBigger;
        final BigDecimal TWO = new BigDecimal(2);
        final BigDecimal TEN = new BigDecimal(10);

        if (kSums[perceiver][other] * kSums[perceiver][x] * kSums[other][x] < 0) {
            kPO = params[perceiver][other].abs();
            kPX = params[perceiver][x].abs();
            isKPOBigger = kPO.compareTo(kPX) > 0;
            kDiff = isKPOBigger ? kPO.subtract(kPX) : kPX.subtract(kPO);
            kDiff = kDiff.divide(TWO);
            kDiff = kDiff.multiply(TEN);
            // TODO: Fix this. Are these operations necessary?
//            kDiff = Math.floor(kDiff);
            kDiff = kDiff.divide(TEN);

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
            params[a][x] = params[a][x].negate();
        }
    }

    public void changeKParamNewcomb(double[][] kSums) {
        int a = (int) (Math.random() * 3);
        int b = (int) (Math.random() * 3);
        while (a == b) {
            b = (int) (Math.random() * 3);
        }
        int x = 3 - a - b;
        final BigDecimal OFFSET = new BigDecimal(0.1);
        final BigDecimal TWO = new BigDecimal(2);

        if (kSums[a][x] * kSums[b][x] < 0) {
            System.out.println("Change k param " + a + " -> " + x + " : " + params[a][x]);
            if (kSums[a][x] < 0 && params[a][x].compareTo(TWO) < 0) {
                params[a][x] = params[a][x].add(OFFSET);
            } else if (TWO.negate().compareTo(params[a][x]) < 0) {
                params[a][x] = params[a][x].subtract(OFFSET);
            }
        }
    }

    public void memeNewcomb(double[][] kSums) {
        final BigDecimal TEN = new BigDecimal(10);
        int a = (int) (Math.random() * 3);
        int b = (int) (Math.random() * 3);
        while (a == b) {
            b = (int) (Math.random() * 3);
        }
        int x = 3 - a - b;
        BigDecimal offset;

        if (kSums[a][x] * kSums[b][x] < 0) {
            System.out.println("Change k param " + a + " -> " + x + " : " + params[a][x]);
            if (kSums[a][x] < 0) {
                offset = params[b][x].divide(TEN);
                params[a][x] = params[a][x].add(offset);
                params[b][x] = params[b][x].subtract(offset);
            } else {
                offset = params[a][x].divide(TEN);
                params[b][x] = params[b][x].add(offset);
                params[a][x] = params[a][x].subtract(offset);
            }
        }
    }

    @Override
    BigDecimal[][] initTwoDim() {
        // Periodic boundary
        // Fig. 1 in the thesis.
        // Create a circle.
//        BigDecimal[][] params = {
//                {1.0, 0.9},
//                {0.9, 0.8},
//        };

        // Periodic boundary
        // Fig. 2 in the thesis.
        // Divide particles into red and blue
//        BigDecimal[][] params = {
//                {1.0, 0.8},
//                {0.8, 0.9},
//        };

        // Open/periodic boundary
        // Fig 3 and 4 in the thesis.
        // Red particles chase blue ones
//        BigDecimal[][] params = {
//                {1.0, 1.0},
//                {0.5, 1.3},
//        };

        // Periodic boundary
        // Fig. 5 in the thesis.
        // Move to the same direction while keeping their position.
//        BigDecimal[][] params = {
//                {0.9, 1.2},
//                {0.5, 0.9},
//        };

        // Periodic boundary
        // Fig. 6 in the thesis.
        // Keep each particle's position.
//        BigDecimal[][] params = {
//                {0.7, 1.2},
//                {0.5, 0.7},
//        };

        // Separation and fusion
        // Fig.7 in the thesis.
//        BigDecimal[][] params = {
//                {0.8, 1.1},
//                {0.6, 1.0}
//        };

//        Like >--< this form
        // Fig. 8 in the thesis.
        BigDecimal[][] params = {
                {new BigDecimal(0.8), new BigDecimal(1.7)},
                {new BigDecimal(0.5), new BigDecimal(1.2)}
        };

        // Periodic boundary
        // Funny
//        BigDecimal[][] params = {
//                {1.3, 1.4},
//                {0.7, 0.8},
//        };

        return params;
    }

    @Override
    BigDecimal[][] initThreeDim() {
//        Balance
//    BigDecimal[][] params = {
//                {0.0, 1.0, 1.0},
//                {1.0, 0.0, 1.0},
//                {1.0, 1.0, 0.0},
//        };

//    Balance
//    BigDecimal[][] params = {
//            {0.0, 1.0, -1.0},
//            {1.0, 0.0, -1.0},
//            {-1.0, -1.0, 0.0},
//    };

//    Balance same to the above
//    BigDecimal[][] params = {
//            {0.0, -1.0, 1.0},
//            {-1.0, 0.0, -1.0},
//            {1.0, -1.0, 0.0},
//    };

//    Balance same to the above
//    BigDecimal[][] params = {
//            {0.0, -1.0, -1.0},
//            {-1.0, 0.0, 1.0},
//            {-1.0, 1.0, 0.0},
//    };

//    Unbalance ?
//    BigDecimal[][] params = {
//            {0.0, -1.0, -1.0},
//            {-1.0, 0.0, -1.0},
//            {-1.0, -1.0, 0.0},
//    };


//    Spin as one big cluster at the center
//        BigDecimal[][] params = {
//                {0.0, 1.4, 1.5},
//                {1.5, 0.0, 1.4},
//                {1.4, 1.5, 0.0},
//        };

        //        Attack and complicated movement NOT SAME to a paper
//    BigDecimal[][] params = {
//            {1.1, 0.0, 1.5},
//            {1.5, 1.1, 0.0},
//            {0.0, 1.5, 1.1},
//    };

        //        Spin as a small cluster
        // GOOD with changeKParamNewcomb()
//        BigDecimal[][] params = {
//                {-0.5, 1.0, 1.4},
//                {1.4, -0.5, 1.0},
//                {1.0, 1.4, -0.5},
//        };

//        Spin like a film
//    GOOD with changeKParamNewcomb()
//        BigDecimal[][] params = {
//                {-0.1, 1.0, 1.4},
//                {1.4, -0.1, 1.0},
//                {1.0, 1.4, -0.1},
//        };

//        Spin speedy with making a cluster with the same type
//        BigDecimal[][] params = {
//                {1.3, 0.0, 1.5},
//                {1.5, 1.3, 0.0},
//                {0.0, 1.5, 1.3}
//        };

        // Periodic boundary
        // each type lines up.
//        BigDecimal[][] params = {
//                {0.0, -0.2, -0.4},
//                {-0.4, 0.0, -0.2},
//                {-0.2, -0.4, 0.0},
//        };

        // Periodic boundary
        // Each cluster moves to the same direction
//        BigDecimal[][] params = {
//                {1.1, 0.0, 1.5},
//                {1.5, 1.1, 0.0},
//                {0.0, 1.5, 1.1},
//        };

        BigDecimal[][] params = {
                {new BigDecimal(0), new BigDecimal(-0.7), new BigDecimal(0.7)},
                {new BigDecimal(0.7), new BigDecimal(0), new BigDecimal(-0.7)},
                {new BigDecimal(-0.7), new BigDecimal(0.7), new BigDecimal(0)},
        };

        return params;
    }

    @Override
    BigDecimal[][] random() {
        BigDecimal[][] params = new BigDecimal[dim][dim];
        BigDecimal tmp;
        final BigDecimal RANGE = new BigDecimal(4);
        final BigDecimal TWO = new BigDecimal(2);

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                tmp = new BigDecimal(Math.random());
                tmp = tmp.multiply(RANGE).subtract(TWO);
                params[i][j] = tmp;
            }
        }
        return params;
    }
}
