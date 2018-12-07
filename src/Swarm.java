import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;


public class Swarm extends JPanel {
    private int w;
    private int h;

    private int scale = 10;
    private int l = 10;
    private double timeStep = 0.002;

    private int pNum;
    private int pType;
    private int pPartition;
    private int pSize = 1;
    private List<Particle> particles;

    private int count = 0;
    Parameter paramManager;
    JTextArea paramsText;
    double[][] params;

    private Boundary boundary;

    private Metrics metrics = KanoKBalanceMetrics.getInstance();

    private enum Boundary {
        OPEN,
        PERIODIC
    }

    public Swarm(int num, int w, int h) {
        this(num, w, h, 2);
    }

    public Swarm(int num, int w, int h, int type) {
        this.pNum = num;
        this.w = w;
        this.h = h;
        this.pType = type;
        this.pPartition = pNum / pType;

        this.paramManager = new Parameter(pType);
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

        // TODO: These two variables are for metrics. Remove these after a measurement.
//        List<Double> preX = new ArrayList<>(pNum);
//        List<Double> preY = new ArrayList<>(pNum);

//        double[][] kSums = new double[][]{
//                {0, 0, 0},
//                {0, 0, 0},
//                {0, 0, 0},
//        };

        for (Particle p1 : particles) {
            sumX = 0;
            sumY = 0;

            // TODO: These operations are for metrics. Remove these after a measurement.
//            preX.add(p1.x);
//            preY.add(p1.y);

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

//                kSums[(p1.id - 1) / pPartition][(p2.id - 1) / pPartition] = tmpX + tmpY;

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

//        flipKParamHeider(kSums);
//        paramManager.changeKParamHeider(kSums);
        this.params = paramManager.getParams();
//        balanceKParamHeider(kSums);


        count++;
        if (count % 100 == 0) {
            repaint();
            // TODO: This is for metrics. Remove these after a measurement.
//            metrics.addNbals(nbal(preX, preY, newX, newY));

            if (count % 500 == 0) {
                Extension.printSwarmParam(params, count);
                ((KanoKBalanceMetrics) metrics).calcHeiderBalance(params);
                System.exit(0);
//                if (count == 50000) {
//                    printSwarmParam();
//                    System.exit(0);
//                }
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
                for (int i = 0; i < h; i += (10 * scale)) {
                    g2.drawLine(0, i, w, i);
                    g2.drawLine(i, 0, i, h);
                }
                break;
            case PERIODIC:
                Stroke def = g2.getStroke();
                Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                g2.setStroke(dashed);
                g2.drawLine(0, h / 2, w, h / 2);
                g2.drawLine(w / 2, 0, w / 2, h);
                g2.setStroke(def);
                break;
        }

        for (Particle p : particles) {
            if (p.id <= pPartition) {
                g2.setColor(Color.RED);
            } else if (pPartition < p.id && p.id <= pPartition * 2) {
                g2.setColor(Color.BLUE);
            } else {
                g2.setColor(Color.GREEN);
            }

            switch (boundary) {
                case OPEN:
                    g2.fill(new Ellipse2D.Double(
                            p.x * scale,
                            p.y * scale,
                            pSize * scale, pSize * scale));
                case PERIODIC:
                    g2.fill(new Ellipse2D.Double(
                            p.x * scale * 8,
                            p.y * scale * 8,
                            pSize * scale, pSize * scale));

            }
        }
    }


    // ------------------- Private --------------------

    // TODO: Move to Metrics class.
    private int isLike(double preDis, double newDis) {
        if (newDis <= preDis) {
            return 1;
        }
        return -1;
    }

    // TODO: Move to Metrics class.
    private boolean isBalance(double preDisIJ, double newDisIJ, double preDisIK, double newDisIK, double preDisJK, double newDisJK) {
        return isLike(preDisIJ, newDisIJ) * isLike(preDisIK, newDisIK) * isLike(preDisJK, newDisJK) > 0;
    }

    // TODO: Move to Metrics class.
    private int[] nbal(List<Double> preX, List<Double> preY, List<Double> newX, List<Double> newY) {
        /*
         * > The function nbal counted the number of all triads that were balanced,
         * > imbalanced or incomplete (contained at least one null relation).
         * > The nbal function was used only to determine how many iterations were
         * > needed inside the balance function before changes in relations ceased.
         * > It was not a part of the regular simulation.
         * 2.4 (http://jasss.soc.surrey.ac.uk/6/3/2.html)
         *
         * @return counts The first element means the number of balanced triads and
         *  the second one means the number of imbalanced triads.
         */

        int counts[] = {0, 0};
        for (int i = 0; i < pNum; i++) {
            for (int j = i + 1; j < pNum; j++) {
                for (int k = j + 1; k < pNum; k++) {
                    // TODO: This function only be correct when an open boundary state.
                    switch (boundary) {
                        case OPEN:
                            if (isBalance(
                                    distance(preX.get(i), preY.get(i), preX.get(j), preY.get(i)),
                                    distance(newX.get(i), newY.get(i), newX.get(j), newY.get(j)),
                                    distance(preX.get(i), preY.get(i), preX.get(k), preY.get(k)),
                                    distance(newX.get(i), newY.get(i), newX.get(k), newY.get(k)),
                                    distance(preX.get(j), preY.get(j), preX.get(k), preY.get(k)),
                                    distance(newX.get(j), newY.get(j), newX.get(k), newY.get(k))
                            )) {
                                counts[0] += 1;
                            } else {
                                counts[1] += 1;
                            }
                            break;
                        case PERIODIC:
                            if (isBalance(
                                    distanceClosest(preX.get(i), preY.get(i), preX.get(j), preY.get(i)),
                                    distanceClosest(newX.get(i), newY.get(i), newX.get(j), newY.get(j)),
                                    distanceClosest(preX.get(i), preY.get(i), preX.get(k), preY.get(k)),
                                    distanceClosest(newX.get(i), newY.get(i), newX.get(k), newY.get(k)),
                                    distanceClosest(preX.get(j), preY.get(j), preX.get(k), preY.get(k)),
                                    distanceClosest(newX.get(j), newY.get(j), newX.get(k), newY.get(k))
                            )) {
                                counts[0] += 1;
                            } else {
                                counts[1] += 1;
                            }
                            break;
                        default:
                            if (isBalance(
                                    distance(preX.get(i), preY.get(i), preX.get(j), preY.get(i)),
                                    distance(newX.get(i), newY.get(i), newX.get(j), newY.get(j)),
                                    distance(preX.get(i), preY.get(i), preX.get(k), preY.get(k)),
                                    distance(newX.get(i), newY.get(i), newX.get(k), newY.get(k)),
                                    distance(preX.get(j), preY.get(j), preX.get(k), preY.get(k)),
                                    distance(newX.get(j), newY.get(j), newX.get(k), newY.get(k))
                            )) {
                                counts[0] += 1;
                            } else {
                                counts[1] += 1;
                            }
                    }
                }
            }
        }
        return counts;
    }

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
        // TODO: Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException happens when the number of particles cannot divide |pType|
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

    private double diffXClosest(Particle pi, Particle pj) {
        /*
         * Pi doesn't change its position and Pj changes its position.
         * Return the closest X difference between Pi and moved 9 types Pj.
         */
        double tmp;
        double iX, jX;
        iX = pi.x % l;
        jX = pj.x % l;
        int d[] = {-1, 0, 1};
        double diffX = diffX(pi, pj);
        for (int i = 0; i < 3; i++) {
            tmp = jX + l * d[i] - iX;
            if (Math.abs(tmp) < Math.abs(diffX)) {
                diffX = tmp;
            }
        }
        return diffX;
    }

    private double diffYClosest(Particle pi, Particle pj) {
        /*
         * Pi doesn't change its position and Pj changes its position.
         * Return the closest Y difference between Pi and moved 9 types Pj.
         */
        double tmp;
        double iY, jY;
        iY = pi.y % l;
        jY = pj.y % l;
        int d[] = {-1, 0, 1};
        double diffY = diffY(pi, pj);
        for (int i = 0; i < 3; i++) {
            tmp = jY + l * d[i] - iY;
            if (Math.abs(tmp) < Math.abs(diffY)) {
                diffY = tmp;
            }
        }
        return diffY;
    }

    private double distanceClosest(double x1, double y1, double x2, double y2) {
        double tmp;
        double iX, iY, jX, jY;
        iX = x1 % l;
        iY = y1 % l;
        jX = x2 % l;
        jY = y2 % l;
        int d[] = {-1, 0, 1};
        double closest = distance(x1, y1, x2, y2);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tmp = distance(iX, iY, l * d[i] + jX, l * d[j] + jY);
                if (tmp < closest) {
                    closest = tmp;
                }
            }
        }
        return closest;
    }

    private double distanceClosest(Particle pi, Particle pj) {
        /*
         * Pi doesn't change its position and Pj changes its position.
         * Return the closest distance between Pi and moved 9 types Pj.
         */
        return distanceClosest(pi.x, pi.y, pj.x, pj.y);
    }

    private double imaging(double x) {
        if (x < 0) return (x % l) + l;
        if (x > l) return x % l;
        return x;
    }

    private double calcRungeKutta(double x) {
        double k1 = x;
        double k2 = x + k1 * timeStep * 0.5;
        double k3 = x + k2 * timeStep * 0.5;
        double k4 = x + k3 * timeStep;
        return (k1 + 2 * k2 + 2 * k3 + k4) * (timeStep / 6.0);
    }
}
