import utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Parameters in this class are defined in Kano's thesis(Mathematical Analysis for Non-reciprocal-interaction-based Model of Collective Behavior, 2017).
 */
public class K_ABPM extends Parameter {
    // TODO: Replace to BigDecimal because "double" type is unstable.
    private static double kA = 0.8;
    private static double kB = 0.4;
    private static double kP = 0.6;
    private static double kM = -0.8;

    private JPanel textA;
    private JPanel textB;
    private JPanel textP;
    private JPanel textM;

    K_ABPM(int num, int type, Swarm swarm) {
        super(num, type, swarm);
        initABPMLayout(); // Set all JPanels and a Button in this function.
    }

    /**
     * Calculate the position of the center of gravity.
     * rg = N^(-1) * Σ(N, i=1)ri
     *
     * @param particles The list of particles.
     * @return The position of the center of gravity.
     */
    Pair<Double> getGravity(List<Particle> particles) {
        double sumX = 0;
        double sumY = 0;
        for (Particle p : particles) {
            sumX += p.x;
            sumY += p.y;
        }
        return new Pair<>(sumX / pNum, sumY / pNum);
    }

    /**
     * X = (N^(-1) * Σ(N, i=1)|ri - rg|)^(-1)
     * rg = N^(-1) * Σ(N, i=1)ri
     * rg denotes the position of the center of gravity.
     * X converges to zero when at least one of the particles moves an infinite distance from the center of gravity.
     *
     * @param particles The list of particles.
     * @return The reciprocal of the average of distance from the gravity.
     */
    double getX(List<Particle> particles) {
        double sum = 0;
        Pair<Double> rg = getGravity(particles);
        for (Particle ri : particles) {
            sum += distance(ri, rg);
        }
        return Math.pow(sum / pNum, -1.0);
    }

    /**
     * V = N^(-1) * Σ(N, i=1)|ri(dot)-rg(dot)|
     * V converges to zero when the relative velocities of all particles with respect to the center of gravity converge to zero.
     *
     * @param timeEvolution The list of the time evolution for each ri.
     * @param curG          The position of the center of gravity in current step.
     * @param nextG         The position of the center of gravity in next step.
     * @return The average of relative speed with the gravity.
     */
    double getV(List<Pair<Double>> timeEvolution, Pair<Double> curG, Pair<Double> nextG) {
        Pair<Double> dotrg = new Pair<>(nextG.x - curG.x, nextG.y - curG.y);
        double sum = 0.0;
        for (Pair<Double> dotri : timeEvolution) {
            sum += distance(dotri.x, dotri.y, dotrg.x, dotrg.y);
        }
        return sum / pNum;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private double distance(Particle pi, Pair<Double> rg) {
        double x1 = pi.x;
        double y1 = pi.y;
        double x2 = rg.x;
        double y2 = rg.y;
        return distance(x1, y1, x2, y2);
    }

    private JPanel createNewTextArea(String labelText, double val, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("OpenSans", Font.PLAIN, 16));
        label.setBounds(0, 0, 60, 30);

        JTextField field = new JTextField(String.valueOf(val));
        field.setFont(new Font("OpenSans", Font.PLAIN, 16));
        field.setBounds(60, 4, 40, 26);
        field.setHorizontalAlignment(JTextField.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(field);
        panel.setBackground(Color.WHITE);
        panel.setBounds(x, y, 100, 30);
        return panel;
    }

    /**
     * Should be called once in this constructor.
     */
    private void initABPMLayout() {
        textA = createNewTextArea("k_a = ", kA, 20, 30 * 1 - 15);
        textB = createNewTextArea("k_b = ", kB, 20, 30 * 2 - 15);
        textP = createNewTextArea("k_p = ", kP, 20, 30 * 3 - 15);
        textM = createNewTextArea("k_m = ", kM, 20, 30 * 4 - 15);
        swarm.add(textA);
        swarm.add(textB);
        swarm.add(textP);
        swarm.add(textM);

        JButton updateButton = createButton("Update", 20, 140, 100, 30);
        updateButton.addActionListener(e -> {
//            List<String> pt = parseParamsText();
//            setParams(pt);
//            updateParamsText();
            swarm.reset();
        });
        swarm.add(updateButton);
    }

    @Override
    double[][] init2x2() {
        return new double[][]{
                {kA, kP + kM},
                {kP - kM, kB}
        };
    }

    @Override
    double[][] init3x3() {
        return new double[0][];
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
