import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

    private void showParams() {
        this.setLayout(null);
        this.add(paramManager.getTitle());
        this.paramsText = paramManager.getParamsText();
        this.add(paramsText);

        JButton updateButton = paramManager.getUpdateButton();
        updateButton.addActionListener(e -> {
            // TODO: Implement update params depends on the content in a textarea.
            paramManager.parseParamsText();
            updateParamsText();
        });
        this.add(updateButton);

        JButton randomButton = paramManager.getRandomButton();
        randomButton.addActionListener(e -> {
            double[][] rnd = paramManager.random();
            paramManager.setParams(rnd);
            this.params = paramManager.getParams();
            updateParamsText();
            printSwarmParam(this.params);
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
        tb.addChangeListener(e -> {
            toggleBoundary();
            tb.setText(boundary.toString());
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

    private double distanceClosest(Particle pi, Particle pj) {
        /*
         * Pi doesn't change its position and Pj changes its position.
         * Return the closest distance between Pi and moved 9 types Pj.
         */
        double tmp;
        double iX, iY, jX, jY;
        iX = pi.x % l;
        iY = pi.y % l;
        jX = pj.x % l;
        jY = pj.y % l;
        int d[] = {-1, 0, 1};
        double closest = distance(pi, pj);
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

    public void run() {
        double sumX, sumY;
        double dis;
        double diffX, diffY;
        double paramK;
        double rungeSumX, rungeSumY;

        double tmpX, tmpY;

        List<Double> newX = new ArrayList<>(pNum);
        List<Double> newY = new ArrayList<>(pNum);

//        double[][] kSums = new double[][]{
//                {0, 0, 0},
//                {0, 0, 0},
//                {0, 0, 0},
//        };

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

            if (count % 1000 == 0) {
                System.out.println(paramManager.getParamChangedCount());
                paramManager.setParamChangedCount(0);
            }
            if (count % 5000 == 0) {
                printSwarmParam();
//                if (count == 50000) {
//                    printSwarmParam();
//                    System.exit(0);
//                }
            }
        }
    }

    public void printSwarmParam() {
        System.out.println("Print current kParams---------------------------");
        System.out.println("count: " + count);
        for (int i = 0; i < pType; i++) {
            for (int j = 0; j < pType; j++) {
                System.out.print(params[i][j] + ", ");
            }
            System.out.println(" ");
        }
        System.out.println("Print current kParams---------------------------");
    }

    public void printSwarmParam(double[][] params) {
        System.out.println("Print current kParams---------------------------");
        System.out.println("count: " + count);
        for (int i = 0; i < pType; i++) {
            for (int j = 0; j < pType; j++) {
                System.out.print(params[i][j] + ", ");
            }
            System.out.println(" ");
        }
        System.out.println("Print current kParams---------------------------");
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

        updateParamsText();
    }
}
