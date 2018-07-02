import javax.swing.*;
import java.awt.*;
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
            return 1.0;
        } else if (i <= pNum / 2 && j > pNum / 2) {
            return 0.5;
        } else if (i > pNum / 2 & j <= pNum / 2) {
            return 1.0;
        } else {
            return 1.3;
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
        double nextX;
        double nextY;
        double dis;
        double paramK;
        for (Particle p1 : particles) {
            nextX = 0;
            nextY = 0;
            for (Particle p2 : particles) {
                if (p1 == p2) continue;

                dis = distance(p1, p2) / scale;
                paramK = k(p1.getId(), p2.getId());

//                System.out.println("p1 X: " + p1.getX() + " p1 Y: " + p1.getY());
//                System.out.println("p2 X: " + p2.getX() + " p2 Y: " + p2.getY());
                System.out.println("distance: " + dis);
                System.out.println("pow -1: " + Math.pow(dis, -1) + ", pow -2: " + Math.pow(dis, -2));
//                System.out.println("pow -1: " + (1/dis) + ", pow -2: " + (1/(dis * dis)));
//                System.out.println("diff X: " + diffX(p1, p2));
//                System.out.println("diff Y: " + diffY(p1, p2));

                nextX += (diffX(p1, p2) / dis) * (paramK * Math.pow(dis, -1) - Math.pow(dis, -2));
                nextY += (diffY(p1, p2) / dis) * (paramK * Math.pow(dis, -1) - Math.pow(dis, -2));

//                nextX += (diffX(p1, p2) / dis) * (paramK * (1 / dis) - (1 / dis * dis));
//                nextY += (diffY(p1, p2) / dis) * (paramK * (1 / dis) - (1 / dis * dis));
            }

            count++;
            System.out.println("count: " + count);
            System.out.println("nextX: " + nextX + ", nextY: " + nextY);
            System.out.println("x: " + p1.getX() + nextX + ", y: " + p1.getY() + nextY);
            System.out.println("---------------------");
            p1.setX(p1.getX() + nextX);
            p1.setY(p1.getY() + nextY);
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        for (int i=scale; i<h; i+=scale) {
            g.drawLine(0, i, w, i);
            g.drawLine(i, 0, i, h);
        }
        
        for (Particle p : particles) {
            if (p.getId() <= pNum / 2) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.BLUE);
            }
            g.fillOval((int) (p.getX() - (pSize / 2) + w/2),
                    (int) (p.getY() - (pSize / 2) + h/2),
                    pSize, pSize);
        }
    }
}
