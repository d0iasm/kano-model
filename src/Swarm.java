import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;


public class Swarm extends JPanel {
    private int w;
    private int h;

    private double timeStep = 0.002;
    private int pNum;
    private int pType;
    private int pPartition;
    private int pSize = 6;
    private List<Particle> particles;

    private int paramChangedCount = 0;

    private int count = 0;
    Parameter paramManager;
    JTextArea paramsText;
    double[][] params;

    public Swarm(int num, int w, int h) {
        this.pNum = num;
        this.w = w;
        this.h = h;
        this.pType = 2;
        this.pPartition = pNum / pType;

        this.paramManager = new Parameter(3);
        this.params = paramManager.getParams();
        showParams();

        this.particles = new ArrayList<>(num);
        for (int i = 1; i <= num; i++) {
            particles.add(new Particle(i));
        }
    }

    public Swarm(int num, int w, int h, int type) {
        this.pNum = num;
        this.w = w;
        this.h = h;
        this.pType = type;
        this.pPartition = pNum / pType;

        this.paramManager = new Parameter(3);
        this.params = paramManager.getParams();
        showParams();

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
        JButton button = paramManager.getButton();
        button.addActionListener(e -> {
            this.remove(paramsText);
            this.paramsText = paramManager.getRandomParamsText();
            this.add(paramsText);
            printSwarmParam(paramManager.getParams());
        });
        this.add(button);
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

    private double calcRungeKutta(double x) {
        double k1 = x;
        double k2 = x + k1 * timeStep * 0.5;
        double k3 = x + k2 * timeStep * 0.5;
        double k4 = x + k3 * timeStep;
        return (k1 + 2 * k2 + 2 * k3 + k4) * (timeStep / 6.0);
    }

    private void flipKParamSimpleHeider(double[][] kSums) {
        if (kSums[0][1] * kSums[1][2] * kSums[2][0] < 0) {
            int base = (int) (Math.random() * 3);
            params[base][base + 1 > 2 ? 0 : base + 1] = -params[base][base + 1 > 2 ? 0 : base + 1];
            System.out.println("1: Flip k param " + base);
            return;
        }

        if (kSums[0][2] * kSums[2][1] * kSums[1][0] < 0) {
            int base = (int) (Math.random() * 3);
            params[base][base + 1 > 2 ? 0 : base + 1] = -params[base][base + 1 > 2 ? 0 : base + 1];
            System.out.println("2: Flip k param " + base);
            return;
        }
    }

    private void flipKParamHeider(double[][] kSums) {
        int perceiver = (int) (Math.random() * 3);
        int other = (int) (Math.random() * 3);
        while (other == perceiver) {
            other = (int) (Math.random() * 3);
        }
        int x = 3 - perceiver - other;

        if (kSums[perceiver][other] * kSums[perceiver][x] * kSums[other][x] < 0) {
            paramChangedCount += 1;
            params[perceiver][other] = -params[perceiver][other];
        }
    }

    private void changeKParamHeider(double[][] kSums) {
        int perceiver = (int) (Math.random() * 3);
        int other = (int) (Math.random() * 3);
        while (other == perceiver) {
            other = (int) (Math.random() * 3);
        }
        int x = 3 - perceiver - other;
        double offset = 0.1;

        if (kSums[perceiver][other] * kSums[perceiver][x] * kSums[other][x] < 0) {
            paramChangedCount += 1;
            if (kSums[perceiver][other] < 0 && params[perceiver][other] < 2.0 - offset) {
                params[perceiver][other] += offset;
            } else if (-2.0 + offset < params[perceiver][other]) {
                params[perceiver][other] -= offset;
            }
        }
    }

    private void balanceKParamHeiderHelper(int x, int y, double kDiff, boolean isPlus) {
        double tmp = params[x][y];
        if (kDiff != 0) {
            paramChangedCount += 1;
        }
        if (isPlus) {
            params[x][y] = Math.min(2.0, params[x][y] + kDiff);
        } else {
            params[x][y] = Math.max(-2.0, params[x][y] - kDiff);
        }
    }

    private void balanceKParamHeider(double[][] kSums) {
        int perceiver = (int) (Math.random() * 3);
        int other = (int) (Math.random() * 3);
        while (other == perceiver) {
            other = (int) (Math.random() * 3);
        }
        int x = 3 - perceiver - other;
        double kPO, kPX, kDiff;
        boolean isKPOBigger;

        if (kSums[perceiver][other] * kSums[perceiver][x] * kSums[other][x] < 0) {
            kPO = Math.abs(params[perceiver][other]);
            kPX = Math.abs(params[perceiver][x]);
            isKPOBigger = kPO > kPX ? true : false;
            kDiff = isKPOBigger ? kPO - kPX : kPX - kPO;
            kDiff /= 2;
            kDiff *= 10;
            kDiff = Math.floor(kDiff);
            kDiff /= 10;

            if (kSums[perceiver][other] < 0 && kSums[other][x] < 0) {
                if (isKPOBigger) {
                    balanceKParamHeiderHelper(perceiver, x, kDiff, true);
                } else {
                    balanceKParamHeiderHelper(perceiver, other, kDiff, true);
                }
            } else if (kSums[perceiver][other] < 0 && kSums[perceiver][x] >= 0) {
                if (isKPOBigger) {
                    balanceKParamHeiderHelper(perceiver, x, kDiff, false);
                } else {
                    balanceKParamHeiderHelper(perceiver, other, kDiff, true);
                }
            } else if (kSums[perceiver][other] >= 0 && kSums[perceiver][x] < 0) {
                if (isKPOBigger) {
                    balanceKParamHeiderHelper(perceiver, x, kDiff, true);
                } else {
                    balanceKParamHeiderHelper(perceiver, other, kDiff, false);
                }
            } else if (kSums[perceiver][other] >= 0 && kSums[perceiver][x] >= 0) {
                if (isKPOBigger) {
                    balanceKParamHeiderHelper(perceiver, x, kDiff, false);
                } else {
                    balanceKParamHeiderHelper(perceiver, other, kDiff, false);
                }
            }
        }
    }

    private void flipKParamNewcomb(double[][] kSums) {
        int a = (int) (Math.random() * 3);
        int b = (int) (Math.random() * 3);
        while (a == b) {
            b = (int) (Math.random() * 3);
        }
        int x = 3 - a - b;

        if (kSums[a][x] * kSums[b][x] < 0) {
            System.out.println("Flip k param " + a + " -> " + x + " : " + params[a][x]);
            params[a][x] = -params[a][x];
        }
    }

    private void changeKParamNewcomb(double[][] kSums) {
        int a = (int) (Math.random() * 3);
        int b = (int) (Math.random() * 3);
        while (a == b) {
            b = (int) (Math.random() * 3);
        }
        int x = 3 - a - b;
        double offset = 0.1;

        if (kSums[a][x] * kSums[b][x] < 0) {
            System.out.println("Change k param " + a + " -> " + x + " : " + params[a][x]);
            if (kSums[a][x] < 0 && params[a][x] < 2.0) {
                params[a][x] += offset;
            } else if (-2.0 < params[a][x]) {
                params[a][x] -= offset;
            }
        }
    }

    private void memeNewcomb(double[][] kSums) {
        int a = (int) (Math.random() * 3);
        int b = (int) (Math.random() * 3);
        while (a == b) {
            b = (int) (Math.random() * 3);
        }
        int x = 3 - a - b;
        double offset;

        if (kSums[a][x] * kSums[b][x] < 0) {
            System.out.println("Change k param " + a + " -> " + x + " : " + params[a][x]);
            if (kSums[a][x] < 0) {
                offset = params[b][x] / 10;
                params[a][x] += offset;
                params[b][x] -= offset;
            } else {
                offset = params[a][x] / 10;
                params[a][x] -= offset;
                params[b][x] += offset;
            }
        }
    }

    public void run() {
        double sumX;
        double sumY;
        double dis;
        double paramK;
        double rungeSumX;
        double rungeSumY;

        double tmpX, tmpY;

        List<Double> newX = new ArrayList<>(pNum);
        List<Double> newY = new ArrayList<>(pNum);

        double[][] kSums = new double[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
        };

        for (Particle p1 : particles) {
            sumX = 0;
            sumY = 0;

            for (Particle p2 : particles) {
                if (p1 == p2) continue;

                // TODO: 事前に距離の計算をしておく
                dis = distance(p1, p2);
                paramK = getKParam(p1.id, p2.id);

                // TODO: Bug? |Rij|^-1 and |Rij|^-2
//                sumX += (diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -1.0) - Math.pow(dis, -2.0));
//                sumY += (diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -1.0) - Math.pow(dis, -2.0));
//                tmpX = (diffX(p1, p2) / dis);
//                tmpY = (diffY(p1, p2) / dis);

                tmpX = (diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - (1 / dis));
                tmpY = (diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - (1 / dis));

                kSums[(p1.id - 1) / pPartition][(p2.id - 1) / pPartition] = tmpX + tmpY;

                sumX += tmpX;
                sumY += tmpY;
            }

            rungeSumX = calcRungeKutta(sumX);
            rungeSumY = calcRungeKutta(sumY);

            newX.add(p1.x + rungeSumX);
            newY.add(p1.y + rungeSumY);
        }

        for (int i = 0; i < pNum; i++) {
            // TODO: Periodic boundary
//            if (newX.get(i) * 4 - (pSize / 2) + w / 2 < 0) {
//                System.out.println("x: " + newX.get(i));
//                particles.get(i).x = newX.get(i) + w;
//
//                System.out.println("NEW x: " + particles.get(i).x);
//            } else if (newX.get(i) * 4 - (pSize / 2) + w / 2 > w) {
//                particles.get(i).x = newX.get(i) - w;
//            } else {
//                particles.get(i).x = newX.get(i);
//            }
//
//            if (newY.get(i) * 4 - (pSize / 2) + h / 2 < 0) {
//                particles.get(i).y = newY.get(i) + h;
//            } else if (newY.get(i) * 4 - (pSize / 2) + h / 2 > h) {
//                particles.get(i).y = newY.get(i) - h;
//            } else {
//                particles.get(i).y = newY.get(i);
//            }

//            System.out.println("X:" + newX.get(i) + " calced X: " + particles.get(i).x);
//            Open boundary
            particles.get(i).x = newX.get(i);
            particles.get(i).y = newY.get(i);
//            particles.get(i).y = 0;
        }
//        flipKParamHeider(kSums);
        changeKParamHeider(kSums);
//        balanceKParamHeider(kSums);

        count++;
        if (count % 100 == 0) {
            repaint();

            if (count % 1000 == 0) {
                System.out.println(paramChangedCount);
                paramChangedCount = 0;
            }
            if (count % 5000 == 0) {
//                printSwarmParam();

                if (count == 50000) {
                    printSwarmParam();
                    System.exit(0);
                }
            }
        }
    }

    public void printSwarmParam() {
        System.out.println("Print current kParams---------------------------");
        System.out.println("count: " + count);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(params[i][j] + ", ");
            }
            System.out.println(" ");
        }
        System.out.println("Print current kParams---------------------------");
    }

    public void printSwarmParam(double[][] params) {
        System.out.println("Print current kParams---------------------------");
        System.out.println("count: " + count);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
//        TODO: Draw gray lines to make it easier to know scale
//        g2.setColor(Color.LIGHT_GRAY);
//        for (int i = scale; i < h; i += scale) {
//            g2.drawLine(0, i, w, i);
//            g2.drawLine(i, 0, i, h);
//        }

        for (Particle p : particles) {
            if (p.id <= pPartition) {
                g2.setColor(Color.RED);
            } else if (pPartition < p.id && p.id <= pPartition * 2) {
                g2.setColor(Color.BLUE);
            } else {
                g2.setColor(Color.GREEN);
            }

            g2.fill(new Ellipse2D.Double(
                    p.x * 10 - (pSize / 2) + w / 2,
                    p.y * 10 - (pSize / 2) + h / 2,
                    pSize, pSize));
        }
    }
}
