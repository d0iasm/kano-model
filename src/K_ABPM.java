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
    private static double kP = 0.2;
    private static double kM = 0.4;

    K_ABPM(int num, int type, Swarm swarm) {
        super(num, type, swarm);
        swarm.add(createNewTextArea("k_a = ", kA, 12, 20 * 1 - 10));
        swarm.add(createNewTextArea("k_b = ", kB, 12, 20 * 2 - 10));
        swarm.add(createNewTextArea("k_p = ", kP, 12, 20 * 3 - 10));
        swarm.add(createNewTextArea("k_m = ", kM, 12, 20 * 4 - 10));
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setBounds(0, 0, 46, 20);
        JTextField field = new JTextField(String.valueOf(val));
        field.setBounds(46, 0, 34, 20);
        field.setHorizontalAlignment(JTextField.CENTER);
        panel.add(label);
        panel.add(field);
        panel.setBounds(x, y, 80, 20);
        return panel;
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
