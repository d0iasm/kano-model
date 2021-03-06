package sps_p;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for parameter K which represents "to what extent person i prefers person j".
 */
abstract class Parameter {
    int pNum;
    int pType;
    Swarm swarm;

    // TODO: Replace to BigDecimal because "double" type is unstable.
    double[][] params;
    private JTextArea paramsText;

    Parameter(int num, int type, Swarm swarm) {
        this.pNum = num;
        this.pType = type;
        this.swarm = swarm;
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
        roundingParams();
        initParamsLayout(); // Set |paramsText| in this function.
    }

    abstract double[][] init2x2();

    abstract double[][] init3x3();

    abstract void reset();

    /**
     * Return the randomized parameter K which is depending on the number of |pType|.
     *
     * @return Randomized parameter K.
     */
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
    };

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

    /**
     * Return the type of particle.
     *
     * @param i The index of particles.
     * @return The type of particle.
     */
    int getType(int i) {
        if (i < getSecondTypeIndex()) {
            return 0;
        } else if (i < getThirdTypeIndex()) {
            return 1;
        }
        return 2;
    }

    /**
     * Return the ParameterKij that denotes "to what extent person i prefers person j".
     *
     * @param i The index of particle i.
     * @param j The index of particle j.
     * @return sps_p.ParameterKij.
     */
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
        roundingParams();
    }

    void setParams(double[][] params) {
        this.params = params;
        roundingParams();
    }

    /**
     * Round the second decimal place of current params.
     * Ex. 21.8355 -> 21.8
     * 21.8755 -> 21.9
     */
    private void roundingParams() {
        for (int i = 0; i < pType; i++) {
            for (int j = 0; j < pType; j++) {
                params[i][j] = Double.parseDouble(String.format("%.1f", params[i][j]));
            }
        }
    }

    /**
     * Create new JLabel object for showing title. This should be called only once.
     *
     * @return New JLabel object for showing title.
     */
    private JLabel getTitle() {
        JLabel label = new JLabel("Params");
        label.setFont(new Font("OpenSans", Font.BOLD, 16));
        label.setBounds(670, 10, 100, 30);
        return label;
    }

    private List<String> parseParamsText() {
        List<String> l = new ArrayList<>();

        for (String s : paramsText.getText().split("\\s+")) {
            if (s.equals("")) continue;
            s = s.replace(",", "");
            l.add(s);
        }
        return l;
    }

    /**
     * Create new JTextArea that shows parameters. This should be called when you want to update parameters based on
     * the content in text area that you input.
     * The order of parameters is {i, j} =
     * {0, 0}, {0, 1}
     * {1, 0}, {1, 1}
     * 0 means the index of particle < 2/N.
     * 1 means the index of particle >= 2/N.
     *
     * @return New JTextArea that shows parameters.
     */
    private JTextArea createNewParamsText() {
        JTextArea newParamsText;
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
        newParamsText = new JTextArea(str.toString());
        newParamsText.setFont(new Font("OpenSans", Font.PLAIN, 16));
        newParamsText.setBounds(650, 40, 120, 60);
        newParamsText.setEditable(true);
        newParamsText.requestFocus();
        return newParamsText;
    }

    /**
     * Remove previous |paramsText| from sps_p.Swarm JPanel and add new one on sps_p.Swarm JPanel.
     */
    void updateParamsText() {
        swarm.remove(paramsText);
        paramsText = createNewParamsText();
        swarm.add(paramsText);
    }

    /**
     * Create new button component.
     *
     * @param text The button text.
     * @param x    The position x.
     * @param y    The position y.
     * @return New button component.
     */
    JButton createButton(String text, int x, int y, int w, int h) {
        JButton button = new JButton(text);
        button.setFont(new Font("OpenSans", Font.PLAIN, 16));
        button.setBounds(x, y, w, h);
        button.setBackground(Color.WHITE);
        return button;
    }

    /**
     * Create title, text area for parameters, and 2 buttons and add them on sps_p.Swarm.
     * This function returns nothing, but initialize |paramsText| as a side effect.
     */
    private void initParamsLayout() {
        swarm.setLayout(null);
        swarm.add(getTitle());
        paramsText = createNewParamsText();
        swarm.add(paramsText);

        JButton updateButton = createButton("Update", 650, 110, 120, 30);
        updateButton.addActionListener(e -> {
            List<String> pt = parseParamsText();
            setParams(pt);
            updateParamsText();
            swarm.reset();
            this.reset();
        });
        swarm.add(updateButton);

        JButton randomButton = createButton("Random", 650, 140, 120, 30);
        randomButton.addActionListener(e -> {
            double[][] rnd = random();
            setParams(rnd);
            updateParamsText();
            swarm.reset();
            this.reset();
        });
        swarm.add(randomButton);
    }
}
