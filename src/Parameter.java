import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


abstract class Parameter {
    int dim;
    // TODO: Replace to BigDecimal because "BigDecimal" type is unstable.
//    BigDecimal[][] params;
    final BigDecimal ZERO = new BigDecimal(0);
    BigDecimal[][] params;
    JTextArea textArea;

    Parameter(int dimension) {
        this.dim = dimension;
        switch (dim) {
            case 2:
                this.params = initTwoDim();
                break;
            case 3:
                this.params = initThreeDim();
                break;
            default:
                this.params = random();
                break;
        }
        this.textArea = createParamsText();
    }

    abstract BigDecimal[][] initTwoDim();
    abstract BigDecimal[][] initThreeDim();
    abstract BigDecimal[][] random();

    BigDecimal[][] getParams() {
        return this.params;
    }

    void setParams(List<String> paramsList) {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                params[i][j] = new BigDecimal(paramsList.get(dim * i + j));
            }
        }
    }

    void setParams(BigDecimal[][] params) {
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

    java.util.List<String> parseParamsText() {
        List<String> splited = new ArrayList<>();

        for (String s : textArea.getText().split("\\s+")) {
            if (s.equals("")) continue;
            s = s.replace(",", "");
            splited.add(s);
        }
        return splited;
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
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (params[i][j].compareTo(ZERO) >= 0) {
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
}
