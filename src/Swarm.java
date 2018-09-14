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

    private int count = 0;
    double[][] matrics = new double[3][3];

//    double[][] matrics = {
//            {0.0, 0.8, 1.8},
//            {-1.0, 0.0, 0.7},
//            {-1.0, 0.3, 0.0},
//    };

//    Balance
//    double[][] matrics = {
//                {0.0, 1.0, 1.0},
//                {1.0, 0.0, 1.0},
//                {1.0, 1.0, 0.0},
//        };

//    Balance
//    double[][] matrics = {
//            {0.0, 1.0, -1.0},
//            {1.0, 0.0, -1.0},
//            {-1.0, -1.0, 0.0},
//    };

//    Balance same to the above
//    double[][] matrics = {
//            {0.0, -1.0, 1.0},
//            {-1.0, 0.0, -1.0},
//            {1.0, -1.0, 0.0},
//    };

//    Balance same to the above
//    double[][] matrics = {
//            {0.0, -1.0, -1.0},
//            {-1.0, 0.0, 1.0},
//            {-1.0, 1.0, 0.0},
//    };

//    Unbalance ?
//    double[][] matrics = {
//            {0.0, -1.0, -1.0},
//            {-1.0, 0.0, -1.0},
//            {-1.0, -1.0, 0.0},
//    };


//    Spin as one big cluster at the center
//        double[][] matrics = {
//                {0.0, 1.4, 1.5},
//                {1.5, 0.0, 1.4},
//                {1.4, 1.5, 0.0},
//        };

    //        Attack and complicated movement NOT SAME to a paper
//    double[][] matrics = {
//            {1.1, 0.0, 1.5},
//            {1.5, 1.1, 0.0},
//            {0.0, 1.5, 1.1},
//    };

    //        Spin as a small cluster
    // GOOD with changeKParamNewcomb()
//    double[][] matrics = {
//            {-0.5, 1.0, 1.4},
//            {1.4, -0.5, 1.0},
//            {1.0, 1.4, -0.5},
//    };

//        Spin like a film
//    GOOD with changeKParamNewcomb()
//        double[][] matrics = {
//                {-0.1, 1.0, 1.4},
//                {1.4, -0.1, 1.0},
//                {1.0, 1.4, -0.1},
//        };

//        Spin speedy with making a cluster with the same type
//        double[][] matrics = {
//                {1.3, 0.0, 1.5},
//                {1.5, 1.3, 0.0},
//                {0.0, 1.5, 1.3}
//        };

    public Swarm(int num, int w, int h) {
        this.pNum = num;
        this.w = w;
        this.h = h;
        this.pType = 2;
        this.pPartition = pNum / pType;
        particles = new ArrayList<>(num);
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
        particles = new ArrayList<>(num);
        for (int i = 1; i <= num; i++) {
            particles.add(new Particle(i));
        }

//        Initialize random
        double tmp;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tmp = -2.0 + Math.random() * 4.0;
                tmp *= 10;
                tmp = Math.floor(tmp);
                tmp /= 10;
                matrics[i][j] = tmp;
            }
        }
    }

    private double kDouble(int i, int j) {
//        Separation and fusion
//        double[][] matrics = {
//                {0.8, 1.1},
//                {0.6, 1.0}
//        };

//        Like >--< this form
        double[][] matrics = {
                {0.8, 1.7},
                {0.5, 1.2}
        };

//        Chase blue -> red -> blue
//        double[][] matrics = {
//                {1.0, 1.0},
//                {0.5, 1.3}
//        };
        return matrics[(i - 1) / pPartition][(j - 1) / pPartition];
    }

    private double kTriple(int i, int j) {
//        Spin as one big cluster at the center
//        double[][] matrics = {
//                {0.0, 1.4, 1.5},
//                {1.5, 0.0, 1.4},
//                {1.4, 1.5, 0.0}
//        };

//        Attack and complicated movement NOT SAME to a paper
//        double[][] matrics = {
//                {1.1, 0.0, 1.5},
//                {1.5, 1.1, 0.0},
//                {0.0, 1.5, 1.1}
//        };

//        Spin as a small cluster
//        double[][] matrics = {
//                {-0.5, 1.0, 1.4},
//                {1.4, -0.5, 1.0},
//                {1.0, 1.4, -0.5}
//        };

//        Spin like a film
//        double[][] matrics = {
//                {-0.1, 1.0, 1.4},
//                {1.4, -0.1, 1.0},
//                {1.0, 1.4, -0.1}
//        };

//        Spin speedy with making a cluster with the same type
//        double[][] matrics = {
//                {1.3, 0.0, 1.5},
//                {1.5, 1.3, 0.0},
//                {0.0, 1.5, 1.3}
//        };

        return matrics[(i - 1) / pPartition][(j - 1) / pPartition];
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
            matrics[base][base + 1 > 2 ? 0 : base + 1] = -matrics[base][base + 1 > 2 ? 0 : base + 1];
            System.out.println("1: Flip k param " + base);
            return;
        }

        if (kSums[0][2] * kSums[2][1] * kSums[1][0] < 0) {
            int base = (int) (Math.random() * 3);
            matrics[base][base + 1 > 2 ? 0 : base + 1] = -matrics[base][base + 1 > 2 ? 0 : base + 1];
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
            System.out.println("Flip k param " + perceiver + " -> " + other + " : " + matrics[perceiver][other]);
            matrics[perceiver][other] = -matrics[perceiver][other];
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
            System.out.println("Change k param " + perceiver + " -> " + other + " : " + matrics[perceiver][other]);
            if (kSums[perceiver][other] < 0 && matrics[perceiver][other] < 2.0) {
                matrics[perceiver][other] += offset;
            } else if (-2.0 < matrics[perceiver][other]) {
                matrics[perceiver][other] -= offset;
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
            System.out.println("Flip k param " + a + " -> " + x + " : " + matrics[a][x]);
            matrics[a][x] = -matrics[a][x];
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
            System.out.println("Change k param " + a + " -> " + x + " : " + matrics[a][x]);
            if (kSums[a][x] < 0 && matrics[a][x] < 2.0) {
                matrics[a][x] += offset;
            } else if (-2.0 < matrics[a][x]) {
                matrics[a][x] -= offset;
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
            System.out.println("Change k param " + a + " -> " + x + " : " + matrics[a][x]);
            if (kSums[a][x] < 0) {
                offset = matrics[b][x] / 10;
                matrics[a][x] += offset;
                matrics[b][x] -= offset;
            } else {
                offset = matrics[a][x] / 10;
                matrics[a][x] -= offset;
                matrics[b][x] += offset;
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
//                paramK = kDouble(p1.id, p2.id);
                paramK = kTriple(p1.id, p2.id);

                // TODO: Bug? |Rij|^-1 and |Rij|^-2
//                sumX += (diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -1.0) - Math.pow(dis, -2.0));
//                sumY += (diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -1.0) - Math.pow(dis, -2.0));
                tmpX = (diffX(p1, p2) / dis);
                tmpY = (diffY(p1, p2) / dis);
                kSums[(p1.id - 1) / pPartition][(p2.id - 1) / pPartition] = tmpX + tmpY;

                tmpX = (diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - (1 / dis));
                tmpY = (diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - (1 / dis));
//                sumX += (diffX(p1, p2) / dis) * (paramK * (1/dis) - (1/dis) * (1/dis));
//                sumY += (diffY(p1, p2) / dis) * (paramK * (1/dis) - (1/dis) * (1/dis));

//                System.out.println("--------------------------");
//                System.out.println("R^: " + (diffX(p1, p2) / dis));
//                if ((diffX(p1, p2) / dis) < 0) {
//                    System.out.println("R^ is minus!!!!!!!!!!!!!!");
//                }
//                System.out.println("|Rij|^-1: " + (1 / dis));
//                if ((1 / dis) < 0) {
//                    System.out.println("|Rij|^-1 is minus!!!!!!!!!!!!!!");
//                }
//                System.out.println("kij{...}: " + (paramK * Math.pow(dis, -0.8) - (1 / dis)));
//                if ((paramK * Math.pow(dis, -0.8) - (1 / dis)) < 0) {
//                    System.out.println("kij{....} is minus!!!!!!!!!!!!!!");
//                }
//                System.out.println("tmpX: " + tmpX);
//                if (tmpX < 0) {
//                    System.out.println("tmpX is minus!!!!!!!!!!!!!!!!");
//                }

                sumX += tmpX;
                sumY += tmpY;
            }

            rungeSumX = calcRungeKutta(sumX);
            rungeSumY = calcRungeKutta(sumY);

            newX.add(p1.x + rungeSumX);
            newY.add(p1.y + rungeSumY);
        }

//        for (Particle p1 : particles) {
//            for (Particle p2 : particles) {
//                if (p1 == p2) continue;
//
//            }
//        }


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
//        flipKParamNewcomb(kSums);
//        changeKParamNewcomb(kSums);
//        memeNewcomb(kSums);

        count++;
        if (count % 100 == 0) {
            repaint();

//        flipKParamHeider(kSums);
//            changeKParamHeider(kSums);
//        flipKParamNewcomb(kSums);
//        changeKParamNewcomb(kSums);
            if (count % 1000 == 0) {
                System.out.println("count: " + count);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        System.out.print(matrics[i][j] + ", ");
                    }
                    System.out.println(" ");
                }
            }
        }
    }

    public void printSwarmParam() {
        System.out.println("END---------------------------");
        System.out.println("count: " + count);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(matrics[i][j] + ", ");
            }
            System.out.println(" ");
        }
        System.out.println("END---------------------------");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
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
