import javax.swing.*;
import java.awt.*;

public class Parameter {
    private int dim;
    private double[][] params;

    public Parameter(int dimension) {
        this.dim = dimension;
        this.params = random();
    }

    public double[][] getParams() {
        return this.params;
    }

    public JLabel getTitle() {
        JLabel label = new JLabel("Params");
        label.setFont(new Font("OpenSans", Font.BOLD, 16));
        label.setBounds(670, 10, 100, 30);
        return label;
    }

    public JTextArea getParamsText() {
        JTextArea textArea = createParamsText();
        textArea.setFont(new Font("OpenSans", Font.PLAIN, 16));
        textArea.setBounds(650, 40, 200, 200);
        return textArea;
    }

    private JTextArea createParamsText() {
        StringBuilder str = new StringBuilder();
        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                str.append(params[i][j]);
                if (j != dim - 1) {
                    str.append(", ");
                }
            }
            str.append("\n");
        }
        return new JTextArea(str.toString());
    }

    private double[][] random() {
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

    private void initThreeDimParams() {
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
//    double[][] params = {
//            {-0.5, 1.0, 1.4},
//            {1.4, -0.5, 1.0},
//            {1.0, 1.4, -0.5},
//    };

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
    }

    private void initTwoDimParams() {
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
        double[][] params = {
                {1.0, 1.0},
                {0.5, 1.3}
        };
    }
}
