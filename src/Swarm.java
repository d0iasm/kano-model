import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class Swarm extends JPanel {
    private int w;
    private int h;

    private int pNum;
    private int pSize = 8;
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
        return pj.getX() - pi.getX();
    }

    private double diffY(Particle pi, Particle pj) {
        return pj.getY() - pi.getY();
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private double distance(Particle pi, Particle pj) {
        double x1 = pi.getX();
        double y1 = pi.getY();
        double x2 = pj.getX();
        double y2 = pj.getY();
        return distance(x1, y1, x2, y2);
    }

    public void run() {
        double sumX;
        double sumY;
        double dis;
        double paramK;
        List<Double> newX = new ArrayList<>(pNum);
        List<Double> newY = new ArrayList<>(pNum);

        for (Particle p1 : particles) {
            sumX = 0;
            sumY = 0;

            for (Particle p2 : particles) {
//                TODO: Runge–Kutta method
//                このやり方だと差分方程式。微分方程式の積分を求めるべし。
//                CHECK: Vertlet integration, Newton cotes, Euler integration
                if (p1 == p2) continue;

                dis = distance(p1, p2);
                paramK = k(p1.getId(), p2.getId());

//                System.out.println("p1 X: " + p1.getX() + " p1 Y: " + p1.getY());
//                System.out.println("p2 X: " + p2.getX() + " p2 Y: " + p2.getY());
//                System.out.println("distance: " + dis);
//                System.out.println("pow -0.8: " + Math.pow(dis, -0.8) + ", pow -1: " + Math.pow(dis, -1));
//                System.out.println("diff X: " + diffX(p1, p2));
//                System.out.println("diff Y: " + diffY(p1, p2));

//                System.out.println("plus X: " + ((diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - Math.pow(dis, -1))));
//                System.out.println("plus Y: " + ((diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - Math.pow(dis, -1))));

                sumX += (diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - Math.pow(dis, -1));
                sumY += (diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -0.8) - Math.pow(dis, -1));
            }

//            System.out.println("---------i end----------");
//            System.out.println("sumX: " + sumX + ", sumY: " + sumY);
//            System.out.println("x: " + (p1.getX() + sumX) + ", y: " + (p1.getY() + sumY));
//            p1.setX(p1.getX() + sumX);
//            p1.setY(p1.getY() + sumY);
//            System.out.println("---------i end----------");

            newX.add(p1.getX() + sumX);
            newY.add(p1.getY() + sumY);
        }

        count++;
        System.out.println("count: " + count);
        System.out.println("---------------------");

        for (int i = 0; i < pNum; i++) {
            particles.get(i).setX(newX.get(i));
            particles.get(i).setY(newY.get(i));
        }

        repaint();
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
            if (p.getId() <= pNum / 2) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.BLUE);
            }

            g2.fill(new Ellipse2D.Double(
                    p.getX() * 4 - (pSize / 2) + w / 2,
                    p.getY() * 4 - (pSize / 2) + h / 2,
                    pSize, pSize));
        }
    }
}
