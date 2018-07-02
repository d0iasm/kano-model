import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Swarm extends JPanel {

    private int pSize = 10;
    private List<Particle> particles;

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
        for (Particle p1 : particles) {
            for (Particle p2: particles) {
                if (p1 == p2) continue;

                System.out.println("---diff---");
                System.out.println(diff(p1, p2));
                System.out.println("---dist---");
                System.out.println(distance(p1, p2));
            }
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
