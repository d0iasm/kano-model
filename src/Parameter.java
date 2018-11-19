import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class Parameter {
    private int dim;
    private double[][] params;
    private int paramChangedCount = 0;
    JTextArea textArea;

    public Parameter(int dimension) {
        this.dim = dimension;
        this.params = initThreeDimParams();
//        this.params = initTwoDimParams();
        this.textArea = createParamsText();
    }

    public double[][] getParams() {
        return this.params;
    }

    public void setParams(List<String> paramsList) {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                params[i][j] = Double.parseDouble(paramsList.get(dim * i + j));
            }
        }
    }

    public void setParams(double[][] params) {
        this.params = params;
    }

    public int getParamChangedCount() {
        return this.paramChangedCount;
    }

    public void setParamChangedCount(int n) {
        this.paramChangedCount = n;
    }

    public JLabel getTitle() {
        JLabel label = new JLabel("Params");
        label.setFont(new Font("OpenSans", Font.BOLD, 16));
        label.setBounds(670, 10, 100, 30);
        return label;
    }

    public JTextArea getParamsText() {
        this.textArea = createParamsText();
        textArea.setFont(new Font("OpenSans", Font.PLAIN, 16));
        textArea.setBounds(650, 40, 120, 60);
        textArea.setEditable(true);
        textArea.requestFocus();
        return textArea;
    }

    public List<String> parseParamsText() {
        List<String> splited = new ArrayList<>();

        for (String s : textArea.getText().split("\\s+")) {
            if (s.equals("")) continue;
            s = s.replace(",", "");
            splited.add(s);
        }
        return splited;
    }

    public JButton getUpdateButton() {
        JButton button = new JButton("Update");
        button.setFont(new Font("OpenSans", Font.PLAIN, 16));
        button.setBounds(650, 110, 120, 30);
        button.setBackground(Color.WHITE);
        return button;
    }

    public JButton getRandomButton() {
        JButton button = new JButton("Random");
        button.setFont(new Font("OpenSans", Font.PLAIN, 16));
        button.setBounds(650, 140, 120, 30);
        button.setBackground(Color.WHITE);
        return button;
    }

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

    public double[][] random() {
        double[][] params = new double[this.dim][this.dim];
        double tmp;
        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                tmp = -2.0 + Math.random() * 4.0;
                tmp *= 10;
                tmp = Math.floor(tmp);
                tmp /= 10;
                params[i][j] = tmp;
            }
        }
        return params;
    }


    // ------------------- Private --------------------
    private JTextArea createParamsText() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (params[i][j] >= 0) {
                    str.append(" ");
                }
                str.append(params[i][j]);
                if (j != dim - 1) {
                    str.append(", ");
                }
            }
            str.append("\n");
        }
        return new JTextArea(str.toString());
    }

    private double[][] initThreeDimParams() {
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

    private double[][] initTwoDimParams() {
        //        Separation and fusion
//        double[][] params = {
//                {0.8, 1.1},
//                {0.6, 1.0}
//        };

//        Like >--< this form
//        double[][] params = {
//                {0.8, 1.7},
//                {0.5, 1.2}
//        };

//        Chase blue -> red -> blue
//        double[][] params = {
//                {1.0, 1.0},
//                {0.5, 1.3}
//        };

        // Periodic boundary
        // Keep each particle's position
        double[][] params = {
                {0.7, 1.2},
                {0.5, 0.7},
        };

        // Periodic boundary
        // Move to the same direction while keeping their position.
//        double[][] params = {
//                {0.9, 1.2},
//                {0.5, 0.9},
//        };

        // Periodic boundary
        // Create a circle
//        double[][] params = {
//                {1.0, 0.9},
//                {0.9, 0.8},
//        };

        // Periodic boundary
        // Divide particles into red and blue
//        double[][] params = {
//                {1.0, 0.8},
//                {0.8, 0.9},
//        };

        // Periodic boundary
        // Red particles chase blue ones
//        double[][] params = {
//                {1.0, 1.0},
//                {0.5, 1.3},
//        };

        // Periodic boundary
        // Funny
//        double[][] params = {
//                {1.3, 1.4},
//                {0.7, 0.8},
//        };

        return params;
    }
}
