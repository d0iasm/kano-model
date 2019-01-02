import metrics.KanoKBalanceMetrics;
import metrics.Metrics;
import utils.Extension;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Swarm is a main class that calculate next position for each particle and paint them.
 * There are 2 ways to calculate next position. The one is open boundary and the second is periodic boundary.
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
    private int pPartition;
    private List<Particle> particles;

    private Boundary boundary;

    private int count = 0;
    private Parameter paramManager;
    private JTextArea paramsText;
    private double[][] params;

    private Metrics metrics = KanoKBalanceMetrics.getInstance();

    private enum Boundary {
        OPEN,
        PERIODIC
    }

    public Swarm(int width, int height, int num, int type) {
        this.pNum = num;
        this.width = width;
        this.height = height;
        this.pType = type;
        // |pPartition| means the first index for second type.
        // i.e. 1. pNum = 4, pType = 2, pPartition = 2
        //      The second type's index starts 2( = pParition).
        //      2. pNum = 5, pType = 2, pPartition = 3
        //      The second type's index starts 3( = pParition).
        this.pPartition = (pNum + pType - 1) / pType;

        this.paramManager = new KaKmKp(pType);
        this.params = paramManager.getParams();
        showParams();
        showBoundaryButton();

        this.boundary = Boundary.OPEN;

        this.particles = new ArrayList<>(num);
        for (int i = 1; i <= num; i++) {
            particles.add(new Particle(i));
        }
    }

    public void run() {
        double sumX, sumY;
        double dis;
        double diffX, diffY;
        double paramK;
        double rungeSumX, rungeSumY;

        double tmpX, tmpY;

        List<Double> newX = new ArrayList<>(pNum);
        List<Double> newY = new ArrayList<>(pNum);

        for (Particle p1 : particles) {
            sumX = 0;
            sumY = 0;

            for (Particle p2 : particles) {
                if (p1 == p2) continue;

                // TODO: Calculate a distance table before this.
                switch (boundary) {
                    case OPEN:
                        // dis: |Rij|.
                        // {diffX, diffY}: Rij.
                        dis = distance(p1, p2);
                        diffX = diffX(p1, p2);
                        diffY = diffY(p1, p2);
                        break;
                    case PERIODIC:
                        // dis: |Rij|.
                        // {diffX, diffY}: Rij.
                        dis = distanceClosest(p1, p2);
                        diffX = diffXClosest(p1, p2);
                        diffY = diffYClosest(p1, p2);
                        break;
                    default:
                        dis = distance(p1, p2);
                        diffX = diffX(p1, p2);
                        diffY = diffY(p1, p2);
                }

                // paramK: kij.
                paramK = getKParam(p1.id, p2.id);

                // TODO: Bug? |Rij|^-1 and |Rij|^-2
//                sumX += (diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -1.0) - Math.pow(dis, -2.0));
//                sumY += (diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -1.0) - Math.pow(dis, -2.0));
//                tmpX = (diffX(p1, p2) / dis);
//                tmpY = (diffY(p1, p2) / dis);

                tmpX = (diffX / dis) * (paramK * Math.pow(dis, -0.8) - (1 / dis));
                tmpY = (diffY / dis) * (paramK * Math.pow(dis, -0.8) - (1 / dis));

                sumX += tmpX;
                sumY += tmpY;
            }

            rungeSumX = calcRungeKutta(sumX);
            rungeSumY = calcRungeKutta(sumY);

            newX.add(p1.x + rungeSumX);
            newY.add(p1.y + rungeSumY);
        }

        for (int i = 0; i < pNum; i++) {
            switch (boundary) {
                case OPEN:
                    particles.get(i).x = newX.get(i);
                    particles.get(i).y = newY.get(i);
                    break;
                case PERIODIC:
                    particles.get(i).x = imaging(newX.get(i));
                    particles.get(i).y = imaging(newY.get(i));
                    break;
                default:
                    particles.get(i).x = newX.get(i);
                    particles.get(i).y = newY.get(i);
            }
        }

        this.params = paramManager.getParams();

        count++;
        if (count % 100 == 0) {
            repaint();

            if (count % 10000 == 0) {
                Extension.printSwarmParam(params, count);
                BigDecimal result = ((KanoKBalanceMetrics) metrics).calcHeiderBalanceBasedOnAllTriangle(params, pNum, pType);
                Extension.printArgs(
                        new Extension.Pair<>("HB result", result.toString())
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
            if (p.id <= pPartition) {
                g2.setColor(Color.RED);
            } else if (p.id <= pPartition * 2) {
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
                case PERIODIC:
                    g2.fill(new Ellipse2D.Double(
                            p.x * SCALE * 8,
                            p.y * SCALE * 8,
                            P_SIZE * SCALE, P_SIZE * SCALE));

            }
        }
    }


    // ------------------- Private --------------------
    private void reset() {
        Extension.printSwarmParam(this.params, this.count);
        System.out.println("============= Reset current count ==============");
        count = 0;
    }

    private void showParams() {
        this.setLayout(null);
        this.add(paramManager.getTitle());
        this.paramsText = paramManager.getParamsText();
        this.add(paramsText);

        JButton updateButton = paramManager.getUpdateButton();
        updateButton.addActionListener(e -> {
            List<String> pt = paramManager.parseParamsText();
            paramManager.setParams(pt);
            this.params = paramManager.getParams();
            updateParamsText();
            reset();
        });
        this.add(updateButton);

        JButton randomButton = paramManager.getRandomButton();
        randomButton.addActionListener(e -> {
            double[][] rnd = paramManager.random();
            paramManager.setParams(rnd);
            this.params = paramManager.getParams();
            updateParamsText();
            reset();
        });
        this.add(randomButton);
    }

    private void updateParamsText() {
        this.remove(paramsText);
        this.paramsText = paramManager.getParamsText();
        this.add(paramsText);
    }

    private void showBoundaryButton() {
        JToggleButton tb = new JToggleButton("Open");
        tb.setFont(new Font("OpenSans", Font.PLAIN, 16));
        tb.setBackground(Color.pink);
        tb.setBounds(650, 700, 120, 30);
        tb.addActionListener(e -> {
            toggleBoundary();
            tb.setText(boundary.toString());
            reset();
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

    private double getKParam(int i, int j) {
        return params[(i - 1) / pPartition][(j - 1) / pPartition];
    }

    private double diffX(Particle pi, Particle pj) {
        return pj.x - pi.x;
    }

    private double diffY(Particle pi, Particle pj) {
        return pj.y - pi.y;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private double distance(Particle pi, Particle pj) {
        double x1 = pi.x;
        double y1 = pi.y;
        double x2 = pj.x;
        double y2 = pj.y;
        return distance(x1, y1, x2, y2);
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
        double diffX = diffX(pi, pj);
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
        double diffY = diffY(pi, pj);
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
        double closest = distance(x1, y1, x2, y2);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tmp = distance(iX, iY, CYCLE_L * d[i] + jX, CYCLE_L * d[j] + jY);
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
