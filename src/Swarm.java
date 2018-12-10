import metrics.KanoKBalanceMetrics;
import metrics.Metrics;
import thirdparty.anc.org.nevec.rjm.BigDecimalMath;
import utils.Extension;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;


public class Swarm extends JPanel {
    private int w;
    private int h;

    private int scale = 10;
    private int l = 10;
    private BigDecimal timeStep = new BigDecimal(0.002);

    private int pNum;
    private int pType;
    private int pPartition;
    private int pSize = 1;
    private List<Particle> particles;

    private int count = 0;
    Kij paramManager;
    JTextArea paramsText;
    BigDecimal[][] params;

    private Boundary boundary;

    private Metrics metrics = KanoKBalanceMetrics.getInstance();

    final BigDecimal MINUS_ZERO_POINT_EIGHT = new BigDecimal(-0.8);
    final BigDecimal MINUS_ONE = BigDecimal.ONE.negate();
    final BigDecimal MINUS_TWO = new BigDecimal(-2);
    final BigDecimal ZERO_POINT_FIVE = new BigDecimal(0.5);
    final BigDecimal TWO = new BigDecimal(2);
    final BigDecimal SIX = new BigDecimal(6);


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
        // |pPartition| means the first index for second type.
        // i.e. 1. pNum = 4, pType = 2, pPartition = 2
        //      The second type's index starts 2( = pParition).
        //      2. pNum = 5, pType = 2, pPartition = 3
        //      The second type's index starts 3( = pParition).
        this.pPartition = (pNum + pType - 1) / pType;

        this.paramManager = new Kij(pType);
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
        BigDecimal sumX, sumY;
        BigDecimal dis;
        BigDecimal diffX, diffY;
        BigDecimal paramK;
        BigDecimal rungeSumX, rungeSumY;

        BigDecimal tmpX, tmpY;
        BigDecimal RhatX, RhatY;
        BigDecimal excludedVolumeEffect;
        BigDecimal reputation;


        List<BigDecimal> newX = new ArrayList<>(pNum);
        List<BigDecimal> newY = new ArrayList<>(pNum);

        // TODO: These two variables are for metrics. Remove these after a measurement.
//        List<Double> preX = new ArrayList<>(pNum);
//        List<Double> preY = new ArrayList<>(pNum);

//        double[][] kSums = new double[][]{
//                {0, 0, 0},
//                {0, 0, 0},
//                {0, 0, 0},
//        };

        for (Particle p1 : particles) {
            sumX = BigDecimal.ZERO;
            sumY = BigDecimal.ZERO;

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

                // Rˆij : Rˆij = Rij / |Rij|
                RhatX = diffX.divide(dis, MathContext.DECIMAL32);
                RhatY = diffY.divide(dis, MathContext.DECIMAL32);

                //|Rij|^(−1)
                excludedVolumeEffect = BigDecimalMath.pow(dis, MINUS_ONE);

                // kij*|Rij|^(−μ)
                reputation = paramK.multiply(BigDecimalMath.pow(dis, MINUS_ZERO_POINT_EIGHT));

                // Rˆij{kij*|Rij|^(−μ) - |Rij|^(−1)}
                tmpX = RhatX.multiply(reputation.subtract(excludedVolumeEffect));
                tmpY = RhatY.multiply(reputation.subtract(excludedVolumeEffect));

//                kSums[(p1.id - 1) / pPartition][(p2.id - 1) / pPartition] = tmpX + tmpY;

                // ∑(j!=i) Rˆij{kij*|Rij|^(−μ) - |Rij|^(−1)}
                sumX = sumX.add(tmpX);
                sumY = sumY.add(tmpY);
            }

            rungeSumX = rungeKutta(sumX);
            rungeSumY = rungeKutta(sumY);

            newX.add(BigDecimal.valueOf(p1.x).add(rungeSumX));
            newY.add(BigDecimal.valueOf(p1.y).add(rungeSumY));
        }

        for (int i = 0; i < pNum; i++) {
            switch (boundary) {
                case OPEN:
                    particles.get(i).x = newX.get(i).doubleValue();
                    particles.get(i).y = newY.get(i).doubleValue();
                    break;
                case PERIODIC:
                    particles.get(i).x = imaging(newX.get(i).doubleValue());
                    particles.get(i).y = imaging(newY.get(i).doubleValue());
                    break;
                default:
                    particles.get(i).x = newX.get(i).doubleValue();
                    particles.get(i).y = newY.get(i).doubleValue();
            }
        }

//        flipKParamHeider(kSums);
//        paramManager.changeKParamHeider(kSums);
        this.params = paramManager.getParams();
//        balanceKParamHeider(kSums);


        count++;
        Extension.printSwarmParam(params, count);
        if (count % 100 == 0) {
            repaint();
            // TODO: This is for metrics. Remove these after a measurement.
//            metrics.addNbals(nbal(preX, preY, newX, newY));

            if (count % 500 == 0) {
                Extension.printSwarmParam(params, count);
                BigDecimal result = ((KanoKBalanceMetrics) metrics).calcHeiderBalanceBasedOnAllTriangle(params, pNum, pType);
                Extension.printArgs(
                        new Extension.Pair<>("HB result", result.toString())
                );
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
            } else if (p.id < pPartition * 2) {
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
            BigDecimal[][] rnd = paramManager.random();
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

    private BigDecimal getKParam(int i, int j) {
        return params[(i - 1) / pPartition][(j - 1) / pPartition];
    }

    private BigDecimal diffX(Particle pi, Particle pj) {
        // FIXME: 12/10/18 Particle should have BigDecimal instead of double.
        return new BigDecimal(pj.x - pi.x);
    }

    private BigDecimal diffY(Particle pi, Particle pj) {
        // FIXME: 12/10/18 Particle should have BigDecimal instead of double.
        return new BigDecimal(pj.y - pi.y);
    }

    private BigDecimal distance(double x1, double y1, double x2, double y2) {
        // FIXME: 12/10/18 Particle should have BigDecimal instead of double.
        return new BigDecimal(Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
    }

    private BigDecimal distance(Particle pi, Particle pj) {
        // FIXME: 12/10/18 Particle should have BigDecimal instead of double.
        double x1 = pi.x;
        double y1 = pi.y;
        double x2 = pj.x;
        double y2 = pj.y;
        return distance(x1, y1, x2, y2);
    }

    /**
     * Pi doesn't change its position and Pj changes its position.
     * Return the closest X difference between Pi and moved 9 types Pj.
     */
    private BigDecimal diffXClosest(Particle pi, Particle pj) {
        BigDecimal tmp;
        double iX, jX;
        iX = pi.x % l;
        jX = pj.x % l;
        int d[] = {-1, 0, 1};
        BigDecimal diffX = diffX(pi, pj);
        for (int i = 0; i < 3; i++) {
            tmp = new BigDecimal(jX + l * d[i] - iX);
            if (tmp.abs().compareTo(diffX.abs()) < 0) {
                diffX = tmp;
            }
        }
        return diffX;
    }

    /**
     * Pi doesn't change its position and Pj changes its position.
     * Return the closest Y difference between Pi and moved 9 types Pj.
     */
    private BigDecimal diffYClosest(Particle pi, Particle pj) {
        BigDecimal tmp;
        double iY, jY;
        iY = pi.y % l;
        jY = pj.y % l;
        int d[] = {-1, 0, 1};
        BigDecimal diffY = diffY(pi, pj);
        for (int i = 0; i < 3; i++) {
            tmp = new BigDecimal(jY + l * d[i] - iY);
            if (tmp.abs().compareTo(diffY.abs()) < 0) {
                diffY = tmp;
            }
        }
        return diffY;
    }

    private BigDecimal distanceClosest(double x1, double y1, double x2, double y2) {
        BigDecimal tmp;
        double iX, iY, jX, jY;
        iX = x1 % l;
        iY = y1 % l;
        jX = x2 % l;
        jY = y2 % l;
        int d[] = {-1, 0, 1};
        BigDecimal closest = distance(x1, y1, x2, y2);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tmp = distance(iX, iY, l * d[i] + jX, l * d[j] + jY);
                if (tmp.compareTo(closest) < 0) {
                    closest = tmp;
                }
            }
        }
        return closest;
    }

    /**
     * Pi doesn't change its position and Pj changes its position.
     * Return the closest distance between Pi and moved 9 types Pj.
     */
    private BigDecimal distanceClosest(Particle pi, Particle pj) {
        return distanceClosest(pi.x, pi.y, pj.x, pj.y);
    }

    private double imaging(double x) {
        if (x < 0) return (x % l) + l;
        if (x > l) return x % l;
        return x;
    }

    /**
     * k1 = f(tn, yn)
     * k2 = f(tn + h/2, yn + h/2*k1)
     * k3 = f(tn + h/2, yn + h/2*k2)
     * k4 = f(tn + h, yn + hk3)
     * yn+1 = yn + ((k1 + 2*k2 + 2*k3 + k4) * (h/6)).
     *
     * @param x original number.
     * @return The difference between yn+1 and yn.
     */
    private BigDecimal rungeKutta(BigDecimal x) {
        BigDecimal k1 = x;
        BigDecimal k2 = x.add(k1.multiply(timeStep).multiply(ZERO_POINT_FIVE));
        BigDecimal k3 = x.add(k2.multiply(timeStep).multiply(ZERO_POINT_FIVE));
        BigDecimal k4 = x.add(k3.multiply(timeStep));
        // (k1 + 2 * k2 + 2 * k3 + k4) * (timeStep / 6.0)
        return k1.add(TWO.multiply(k2)).add(TWO.multiply(k3)).add(k4).multiply(timeStep.divide(SIX, MathContext.DECIMAL32));
    }
}
