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
    private int pSize = 4;
    private int scale = 200;
    private List<Particle> particles;

    private int count = 0;

    public Swarm(int num, int w, int h) {
        this.pNum = num;
        this.w = w;
        this.h = h;
        particles = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            particles.add(new Particle(i));
        }
    }

    private double k(int i, int j) {
        if (i <= pNum / 2 && j <= pNum / 2) {
            return 0.8;
        } else if (i <= pNum / 2 && j > pNum / 2) {
            return 1.1;
        } else if (i > pNum / 2 & j <= pNum / 2) {
            return 0.6;
        } else {
            return 1.0;
        }
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
        double timeStep = 0.002;
        double k1 = x;
        double k2 = x + k1 * timeStep * 0.5;
        double k3 = x + k2 * timeStep * 0.5;
        double k4 = x + k3 * timeStep;
        return (k1 + 2*k2 + 2*k3 + k4) * (timeStep / 6.0);
    }

    public void run() {
        double sumX;
        double sumY;
        double dis;
        double paramK;

        double rungeSumX;
        double rungeSumY;

        List<Double> newX = new ArrayList<>(pNum);
        List<Double> newY = new ArrayList<>(pNum);

        for (Particle p1 : particles) {
            sumX = 0;
            sumY = 0;

            for (Particle p2 : particles) {
                if (p1 == p2) continue;

                // TODO: 事前に距離の計算をしておく
                dis = distance(p1, p2);
                paramK = k(p1.id, p2.id);

//                System.out.println("p1 X: " + p1.x + " p1 Y: " + p1.y);
//                System.out.println("p2 X: " + p2.x + " p2 Y: " + p2.y);
//                System.out.println("distance: " + dis);
//                System.out.println("pow -0.8: " + Math.pow(dis, -0.8) + ", pow -1: " + Math.pow(dis, -1));
//                System.out.println("diff X: " + diffX(p1, p2));
//                System.out.println("diff Y: " + diffY(p1, p2));

//                System.out.println("plus X: " + ((diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - Math.pow(dis, -1))));
//                System.out.println("plus Y: " + ((diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - Math.pow(dis, -1))));

                sumX += (diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - (1/dis));
                sumY += (diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - (1/dis));
            }

            rungeSumX = calcRungeKutta(sumX);
            rungeSumY = calcRungeKutta(sumY);

            newX.add(p1.x + rungeSumX);
            newY.add(p1.y + rungeSumY);
        }

        count++;
//        System.out.println("count: " + count);
//        System.out.println("---------------------");

        for (int i = 0; i < pNum; i++) {
            particles.get(i).x = newX.get(i);
            particles.get(i).y = newY.get(i);
        }

        if (count % 100 == 0) {
            System.out.println("count: " + count);
            repaint();
        }
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
            if (p.id <= pNum / 2) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.BLUE);
            }

            g2.fill(new Ellipse2D.Double(
                    p.x * 4 - (pSize / 2) + w / 2,
                    p.y * 4 - (pSize / 2) + h / 2,
                    pSize, pSize));
        }
    }
}
