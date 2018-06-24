import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Swarm extends JPanel {
    private double range = 100; // Max is 300
    private double normalSpeed = 20;
    private double maxSpeed = 40;
    private double c1 = 1; // Strength of cohesive force
    private double c2 = 1; // Strength of aligning force
    private double c3 = 100; // Strength of separating force
    private double c4 = 0.5; // Probability of random steering
    private double c5 = 1; // Tendency of pace keeping

    private int pSize = 10;
    private List<Particle> particles;

    public Swarm(int num) {
        particles = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            particles.add(new Particle());
        }
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private List<Particle> getNeighbors(Particle p1) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        return particles.stream()
                .filter(p2 -> distance(x1, y1, p2.getX(), p2.getY()) < range)
                .collect(Collectors.toList());
    }

    public void run() {
        double x1, y1, x2, y2;
        double aveX, aveY, aveDx, aveDy;
        double aX, aY;
        double d;

        int neighborsNum;
        List<Particle> neighbors;

//        System.out.println("----");

        for (Particle p1 : particles) {
            x1 = p1.getX();
            y1 = p1.getY();
            aveX = 0;
            aveY = 0;
            aveDx = 0;
            aveDy = 0;
            aX = 0;
            aY = 0;

//            System.out.println(x1);

            neighbors = getNeighbors(p1);
            neighborsNum = neighbors.size();

            if (neighborsNum <= 1) {
                p1.straying();
            } else {
                // TODO: Fix movement way
                for (Particle n : neighbors) {
                    x2 = n.getX();
                    y2 = n.getY();
                    aveX += x2;
                    aveY += y2;
                    aveDx += n.getDx();
                    aveDy += n.getDy();

                    d = distance(x1, y1, x2, y2);
                    if (d == 0) d = 0.001;
                    else d *= d;
                    aX += (x1 - x2) / d * c3;
                    aY += (y1 - y2) / d * c3;
                }
                aveX /= neighborsNum;
                aveY /= neighborsNum;
                aveDx /= neighborsNum;
                aveDy /= neighborsNum;

                aX += c1 * (aveX - x1) + c2 * (aveDx - p1.getDx());
                aY += c1 * (aveY - y1) + c2 * (aveDy - p1.getDy());

                if (Math.random() < c4) {
                    aX += Math.random() * 10 - 5;
                    aY += Math.random() * 10 - 5;
                }

                p1.accelerate(aX, aY, maxSpeed);
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

            g.setColor(new Color(255, 255, 0, 50));
            g.fillOval((int) (p.getX() - (range / 2)),
                    (int) (p.getY() - (range / 2)),
                    (int) range, (int) range);
        }
    }
}
