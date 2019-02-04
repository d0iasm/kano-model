package sps_p;

import sps_p.metrics.KanoKBalanceMetrics;
import sps_p.metrics.Metrics;
import sps_p.utils.Extension;
import sps_p.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Calculate next position for each particle and paint them.
 * There are 2 ways to calculate next position, open boundary and periodic boundary.
 */
public class Swarm extends JPanel {
    private int width;
    private int height;

    private static final int SCALE = 10;
    private static final int CYCLE_L = 10;
    private static final double TIME_STEP = 0.002;
    private static final int P_SIZE = 1;

    private int pNum;
    private int pType;
    private List<Particle> particles;

    private int count = 0;
    private Boundary boundary;
    private Parameter parameter;

    private Metrics metrics = KanoKBalanceMetrics.getInstance();

    private enum Boundary {
        OPEN,
        PERIODIC
    }

    public Swarm(int width, int height, int num, int type) {
        this.width = width;
        this.height = height;
        this.pNum = num;
        this.pType = type;

        this.parameter = new ParameterKabpm(num, type, this);

        this.boundary = Boundary.OPEN;
        showBoundaryButton();

        this.particles = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            Particle p = new Particle(i);
            p.initPosition((width / 2) / SCALE - 1, (width / 2) / SCALE + 1);
            particles.add(p);
        }
    }

    /**
     * Calculate one step for each particle.
     */
    public void run() {
        List<Pair<Double>> timeEvolution = timeEvolutions(particles);

        Pair<Double> curG = ((ParameterKabpm) parameter).getGravity(particles);

        double curX, curY;
        for (int i = 0; i < pNum; i++) {
            curX = particles.get(i).x;
            curY = particles.get(i).y;
            switch (boundary) {
                case OPEN:
                    particles.get(i).x = curX + timeEvolution.get(i).x;
                    particles.get(i).y = curY + timeEvolution.get(i).y;
                    break;
                case PERIODIC:
                    particles.get(i).x = imaging(curX + timeEvolution.get(i).x);
                    particles.get(i).y = imaging(curY + timeEvolution.get(i).y);
                    break;
            }
        }

        count++;
        if (count % 100 == 0) {
            repaint();

            // TODO: Remove these lines for debug.
            Pair<Double> nextG = ((ParameterKabpm) parameter).getGravity(particles);
            double x = ((ParameterKabpm) parameter).getX(particles);
            double v = ((ParameterKabpm) parameter).getV(timeEvolution, curG, nextG);
//            System.out.println("Gravity: " + curG.x + ", " + curG.y + ", X: " + x + ", V: " + v);
            ((ParameterKabpm) parameter).addPoint(x, v);

            if (count % 100000 == 0) {
                Extension.printSwarmParam(parameter.getParams(), count);
                BigDecimal result = ((KanoKBalanceMetrics) metrics).calcHeiderBalanceBasedOnAllTriangle(parameter.getParams(), pNum, pType);
                Extension.printPairs(
                        new Pair<>("HB result", result.toString())
                );
//                System.exit(0);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.LIGHT_GRAY);
        switch (boundary) {
            case OPEN:
                for (int i = 0; i < height; i += (10 * SCALE)) {
                    g2.drawLine(0, i, width, i);
                    g2.drawLine(i, 0, i, height);
                }
                break;
            case PERIODIC:
                Stroke def = g2.getStroke();
                Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                g2.setStroke(dashed);
                g2.drawLine(0, height / 2, width, height / 2);
                g2.drawLine(width / 2, 0, width / 2, height);
                g2.setStroke(def);
                break;
        }

        for (Particle p : particles) {
            if (p.id < parameter.getSecondTypeIndex()) {
                g2.setColor(Color.RED);
            } else if (p.id < parameter.getThirdTypeIndex()) {
                g2.setColor(Color.BLUE);
            } else {
                g2.setColor(Color.GREEN);
            }

            switch (boundary) {
                case OPEN:
                    g2.fill(new Ellipse2D.Double(
                            p.x * SCALE,
                            p.y * SCALE,
                            P_SIZE * SCALE, P_SIZE * SCALE));
                    break;
                case PERIODIC:
                    g2.fill(new Ellipse2D.Double(
                            p.x * SCALE * 8,
                            p.y * SCALE * 8,
                            P_SIZE * SCALE, P_SIZE * SCALE));
                    break;

            }
        }
    }

    /**
     * Reset the positions of all particles.
     */
    void reset() {
        Extension.printSwarmParam(parameter.getParams(), this.count);
        System.out.println("============= Reset current count ==============");
        for (Particle p : particles) {
            p.initPosition((width / 2) / SCALE - 1, (width / 2) / SCALE + 1);
        }
        count = 0;
    }

    /**
     * Calculate |Rij| which denotes the distance between particle i and particle j.
     *
     * @param x1 The position x of particle I.
     * @param y1 The position y of particle I.
     * @param x2 The position x of particle J.
     * @param y2 The position y of particle J.
     * @return The distance between particle i and particle j.
     */
    double distance(double x1, double y1, double x2, double y2) {
        switch (boundary) {
            case PERIODIC:
                return distanceClosest(x1, y1, x2, y2);
            default: // case OPEN:
                return distanceDirect(x1, y1, x2, y2);
        }
    }

    /**
     * Calculate |Rij| which denotes the distance between particle i and particle j.
     *
     * @param p1 Particle i.
     * @param p2 Particle j.
     * @return The distance between particle i and particle j.
     */
    double distance(Particle p1, Particle p2) {
        switch (boundary) {
            case PERIODIC:
                return distanceClosest(p1, p2);
            default: // case OPEN:
                return distanceDirect(p1, p2);
        }
    }

    /**
     * Calculate Rij which denotes the difference of position vector between particle i and particle j.
     *
     * @param p1 Particle i.
     * @param p2 Particle j.
     * @return The difference of position vector between particle i and particle j.
     */
    Pair<Double> diff(Particle p1, Particle p2) {
        switch (boundary) {
            case PERIODIC:
                Pair<Double> diff = new Pair<>(0.0, 0.0);
                diff.x = diffXClosest(p1, p2);
                diff.y = diffYClosest(p1, p2);
                return diff;
            default: // case OPEN:
                return diffDirect(p1, p2);
        }
    }

    private void showBoundaryButton() {
        JToggleButton tb = new JToggleButton("Open");
        tb.setFont(new Font("OpenSans", Font.PLAIN, 16));
        tb.setBackground(Color.pink);
        tb.setBounds(650, 700, 120, 30);
        tb.addActionListener(e -> {
            toggleBoundary();
            tb.setText(boundary.toString());
            this.reset();
            parameter.reset();
        });
        this.add(tb);
    }

    private void toggleBoundary() {
        switch (boundary) {
            case OPEN:
                boundary = Boundary.PERIODIC;
                break;
            case PERIODIC:
                boundary = Boundary.OPEN;
                break;
        }
    }

    /**
     * Calculate the time evolution of ri for all particles that is given by
     * ri = Σ(i!=j) (kij|Rij|^(-1) - |Rij|^(-2)) * ^Rij.
     * Rij = rj - ri, ^Rij = Rij / |Rij|, and kij denotes a constant that represents
     * "to what extent person i prefers person j".
     *
     * @param particles All N particles.
     * @return The list of the time evolution for each ri.
     */
    private List<Pair<Double>> timeEvolutions(List<Particle> particles) {
        Pair<Double> sum = new Pair<>(0.0, 0.0);
        Pair<Double> diff;
        double dis;
        double paramK;
        /**
         * (dot)ri. The time evolution of ri.
         */
        List<Pair<Double>> timeEvolutions = new ArrayList<>(pNum);

        for (Particle p1 : particles) {
            sum.x = 0.0;
            sum.y = 0.0;

            for (Particle p2 : particles) {
                if (p1 == p2) continue;
                dis = distance(p1, p2); // |Rij|.
                diff = diff(p1, p2); // Rij.
                paramK = parameter.getKParam(p1.id, p2.id); // kij.

                sum.x += (paramK * Math.pow(dis, -1.0) - Math.pow(dis, -2.0)) * (diff.x / dis);
                sum.y += (paramK * Math.pow(dis, -1.0) - Math.pow(dis, -2.0)) * (diff.y / dis);
            }
            timeEvolutions.add(new Pair<>(calcRungeKutta(sum.x), calcRungeKutta(sum.y)));
        }
        return timeEvolutions;
    }

    private Pair<Double> diffDirect(Particle pi, Particle pj) {
        return new Pair<>(pj.x - pi.x, pj.y - pi.y);
    }

    private double diffXDirect(Particle pi, Particle pj) {
        return pj.x - pi.x;
    }

    private double diffYDirect(Particle pi, Particle pj) {
        return pj.y - pi.y;
    }

    private double distanceDirect(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private double distanceDirect(Particle pi, Particle pj) {
        double x1 = pi.x;
        double y1 = pi.y;
        double x2 = pj.x;
        double y2 = pj.y;
        return distanceDirect(x1, y1, x2, y2);
    }

    /**
     * Calculate the closest X difference between Pi and moved 9 types Pj.
     * Pi doesn't change its position and Pj changes its position.
     *
     * @param pi Particle I.
     * @param pj Particle J.
     * @return The closest X difference between Pi and moved 9 types Pj.
     */
    private double diffXClosest(Particle pi, Particle pj) {
        double tmp;
        double iX, jX;
        iX = pi.x % CYCLE_L;
        jX = pj.x % CYCLE_L;
        int d[] = {-1, 0, 1};
        double diffX = diffXDirect(pi, pj);
        for (int i = 0; i < 3; i++) {
            tmp = jX + CYCLE_L * d[i] - iX;
            if (Math.abs(tmp) < Math.abs(diffX)) {
                diffX = tmp;
            }
        }
        return diffX;
    }

    /**
     * Calculate the closest Y difference between Pi and moved 9 types Pj.
     * Pi doesn't change its position and Pj changes its position.
     *
     * @param pi Particle I.
     * @param pj Particle J.
     * @return The closest Y difference between Pi and moved 9 types Pj.
     */
    private double diffYClosest(Particle pi, Particle pj) {
        double tmp;
        double iY, jY;
        iY = pi.y % CYCLE_L;
        jY = pj.y % CYCLE_L;
        int d[] = {-1, 0, 1};
        double diffY = diffYDirect(pi, pj);
        for (int i = 0; i < 3; i++) {
            tmp = jY + CYCLE_L * d[i] - iY;
            if (Math.abs(tmp) < Math.abs(diffY)) {
                diffY = tmp;
            }
        }
        return diffY;
    }

    /**
     * Calculate the closest distance between Pi and moved 9 types Pj.
     * Pi doesn't change its position and Pj changes its position.
     *
     * @param x1 The position x of particle I.
     * @param y1 The position y of particle I.
     * @param x2 The position x of particle J.
     * @param y2 The position y of particle J.
     * @return The closest distance between Pi and moved 9 types Pj.
     */
    private double distanceClosest(double x1, double y1, double x2, double y2) {
        double tmp;
        double iX, iY, jX, jY;
        iX = x1 % CYCLE_L;
        iY = y1 % CYCLE_L;
        jX = x2 % CYCLE_L;
        jY = y2 % CYCLE_L;
        int d[] = {-1, 0, 1};
        double closest = distanceDirect(x1, y1, x2, y2);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tmp = distanceDirect(iX, iY, CYCLE_L * d[i] + jX, CYCLE_L * d[j] + jY);
                if (tmp < closest) {
                    closest = tmp;
                }
            }
        }
        return closest;
    }

    /**
     * Calculate the closest distance between Pi and moved 9 types Pj.
     * Pi doesn't change its position and Pj changes its position.
     *
     * @param pi Particle I.
     * @param pj Particle J.
     * @return The closest distance between Pi and moved 9 types Pj.
     */
    private double distanceClosest(Particle pi, Particle pj) {
        return distanceClosest(pi.x, pi.y, pj.x, pj.y);
    }

    /**
     * Move the range of from 0 to CYCLE_L.
     *
     * @param x Current position.
     * @return Moved position.
     */
    private double imaging(double x) {
        if (x < 0) return (x % CYCLE_L) + CYCLE_L;
        if (x > CYCLE_L) return x % CYCLE_L;
        return x;
    }

    private double calcRungeKutta(double x) {
        double k1 = x;
        double k2 = x + k1 * TIME_STEP * 0.5;
        double k3 = x + k2 * TIME_STEP * 0.5;
        double k4 = x + k3 * TIME_STEP;
        return (k1 + 2 * k2 + 2 * k3 + k4) * (TIME_STEP / 6.0);
    }
}
