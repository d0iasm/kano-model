package sps_p;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * sps_p.Environment is a base class for displaying a window. The main task of this class is to initialize two main components, JFrame and JPanel, and combine them.
 */
class Environment {
    private JFrame frame;
    private Swarm swarm;

    Environment(int width, int height, int num, int type) {
        this.frame = initFrame(width, height);
        this.swarm = initSwarm(width, height, num, type);
        frame.add(swarm, BorderLayout.CENTER);
    }

    /**
     * Keep to update sps_p.Swarm's state.
     */
    void run() {
        // TODO: Keep the screen position to chase the middle of particles while running.
        while (true) swarm.run();
    }

    /**
     * Initialize JFrame.
     *
     * @param width The width of a window.
     * @param height The height of a window.
     * @return Initialized JFrame.
     */
    private JFrame initFrame(int width, int height) {
        JFrame frame = new JFrame();
        frame.setTitle("SPS-P");
        frame.setSize(new Dimension(width, height));
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

    /**
     * Initialize sps_p.Swarm that extends JPanel.
     *
     * @param width The width of a window.
     * @param height The height of a window.
     * @param num The number of particles.
     * @param type The number of type of particles.
     * @return Initialized sps_p.Swarm.
     */
    private Swarm initSwarm(int width, int height, int num, int type) {
        Swarm swarm = new Swarm(width, height, num, type);
        swarm.setBackground(Color.WHITE);
        swarm.setSize(new Dimension(width, height));
        return swarm;
    }
}
