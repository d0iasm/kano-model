import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Environment {
    private JFrame frame;
    private Swarm swarm;

    Environment(int w, int h, int n, int type) {
        this.frame = initFrame(w, h);
        this.swarm = initSwarm(w, h, n, type);
        frame.add(swarm, BorderLayout.CENTER);
    }

    void run() {
        // TODO: Keep the screen position to chase the middle of particles while running.
        while (true) swarm.run();
    }

    private JFrame initFrame(int w, int h) {
        JFrame frame = new JFrame();
        frame.setTitle("SPS-P");
        frame.setSize(new Dimension(w, h));
        frame.setBackground(Color.WHITE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        frame.setVisible(true);
        return frame;
    }

    private Swarm initSwarm(int w, int h, int n, int type) {
        Swarm swarm = new Swarm(n, w, h, type);
        swarm.setBackground(Color.WHITE);
        swarm.setSize(new Dimension(w, h));
        return swarm;
    }
}
