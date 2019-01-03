import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


abstract class Parameter {
    int pNum;
    int pType;

    // TODO: Replace to BigDecimal because "double" type is unstable.
    double[][] params;
    JTextArea textArea;

    Parameter(int num, int type) {
        this.pNum = num;
        this.pType = type;
        switch (pType) {
            case 2:
                this.params = init2x2();
                break;
            case 3:
                this.params = init3x3();
                break;
            default:
                this.params = random();
        }
        this.textArea = createParamsText();
    }

    abstract double[][] init2x2();

    abstract double[][] init3x3();

    abstract double[][] random();

    /**
     * Always return 0 because the index of first type starts 0.
     *
     * @return 0.
     */
    int getFirstTypeIndex() {
        return 0;
    }

    /**
     * Return the index of starting second type. The result of |pNum / pType| is round-down.
     * To be precise, Java refers to this as rounding toward zero.
     *
     * @return the index of starting second type.
     */
    int getSecondTypeIndex() {
        return pNum / pType;
    }

    /**
     * Return the index of starting third type. Return |pNum| when pType is 2.
     * In the case 3,
     * Ex. n=4 : {0}, {1}, {2, 3} => return 2
     * Ex. n=5 : {0}, {1, 2}, {3, 4} => return 3
     * Ex. n=6 : {0, 1}, {2, 3}, {4, 5} => return 4
     *
     * @return the index of starting third type.
     */
    int getThirdTypeIndex() {
        switch (pType) {
            case 2:
                return pNum;
            case 3:
                int s = getSecondTypeIndex();
                int r = pNum - s;
                return s + r / 2;
            default:
                return pNum;
        }
    }

    int getType(int i) {
        if (i < getSecondTypeIndex()) {
            return 0;
        } else if (i < getThirdTypeIndex()) {
            return 1;
        }
        return 2;
    }

    double getKParam(int i, int j) {
        return params[getType(i)][getType(j)];
    }

    double[][] getParams() {
        return this.params;
    }

    void setParams(List<String> paramsList) {
        for (int i = 0; i < pType; i++) {
            for (int j = 0; j < pType; j++) {
                params[i][j] = Double.parseDouble(paramsList.get(pType * i + j));
            }
        }
    }

    void setParams(double[][] params) {
        this.params = params;
    }

    JLabel getTitle() {
        JLabel label = new JLabel("Params");
        label.setFont(new Font("OpenSans", Font.BOLD, 16));
        label.setBounds(670, 10, 100, 30);
        return label;
    }

    JTextArea getParamsText() {
        this.textArea = createParamsText();
        textArea.setFont(new Font("OpenSans", Font.PLAIN, 16));
        textArea.setBounds(650, 40, 120, 60);
        textArea.setEditable(true);
        textArea.requestFocus();
        return textArea;
    }

    List<String> parseParamsText() {
        List<String> l = new ArrayList<>();

        for (String s : textArea.getText().split("\\s+")) {
            if (s.equals("")) continue;
            s = s.replace(",", "");
            l.add(s);
        }
        return l;
    }

    JButton getUpdateButton() {
        JButton button = new JButton("Update");
        button.setFont(new Font("OpenSans", Font.PLAIN, 16));
        button.setBounds(650, 110, 120, 30);
        button.setBackground(Color.WHITE);
        return button;
    }

    JButton getRandomButton() {
        JButton button = new JButton("Random");
        button.setFont(new Font("OpenSans", Font.PLAIN, 16));
        button.setBounds(650, 140, 120, 30);
        button.setBackground(Color.WHITE);
        return button;
    }

    JTextArea createParamsText() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < pType; i++) {
            for (int j = 0; j < pType; j++) {
                if (params[i][j] >= 0) {
                    str.append(" ");
                }
                str.append(params[i][j]);
                if (j != pType - 1) {
                    str.append(", ");
                }
            }
            str.append("\n");
        }
        return new JTextArea(str.toString());
    }
}
