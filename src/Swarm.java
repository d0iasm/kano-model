import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Swarm extends JPanel {

    private int pSize = 10;
    private List<Particle> particles;

    private double k = 0.1;

    public Swarm(int num) {
        particles = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            particles.add(new Particle());
        }
    }

    private Point2D.Double diff(double x1, double y1, double x2, double y2) {
        return new Point2D.Double(x2 - x1, y2 - y1);
    }

    private Point2D.Double diff(Particle pi, Particle pj) {
        double x1 = pi.getX();
        double y1 = pi.getY();
        double x2 = pj.getX();
        double y2 = pj.getY();
        return diff(x1, y1, x2, y2);
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
        for (Particle p1 : particles) {
            nextX = 0;
            nextY = 0;
            for (Particle p2 : particles) {
                if (p1 == p2) continue;

                dis = distance(p1, p2);
                System.out.println("p1 X: " + p1.getX() + " p1 Y: " + p1.getY());
                System.out.println("p2 X: " + p2.getX() + " p2 Y: " + p2.getY());
                System.out.println("distance: " + dis);
                System.out.println("diff X: " + diffX(p1, p2));
                System.out.println("diff Y: " + diffY(p1, p2));

                nextX += (diffX(p1, p2) / dis) * (k * (1 / dis) - (1 / dis * dis));
                nextY += (diffY(p1, p2) / dis) * (k * (1 / dis) - (1 / dis * dis));
            }

            System.out.println("X: " + nextX + " Y: " + nextY);
            System.out.println("---------------------");
            p1.setX(nextX);
            p1.setY(nextY);
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Particle p : particles) {
            g.setColor(Color.ORANGE);
            g.fillOval((int) (p.getX() - (pSize / 2)),
                    (int) (p.getY() - (pSize / 2)),
                    pSize, pSize);
        }
    }
}
