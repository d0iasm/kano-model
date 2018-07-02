import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Environment extends JFrame implements ActionListener {
    private Swarm swarm;

    public Environment() {
        initFrame(600, 600);
        new Timer(10, (ActionEvent e) -> {
            swarm.run();
        }).start();
    }

    private void initFrame(int w, int h) {
        setTitle("Swarm Chemistry Simulator");
        setSize(new Dimension(w, h));
        setBackground(Color.WHITE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        swarm = new Swarm(10);
        swarm.setBackground(Color.WHITE);
        Container container = getContentPane();
        container.add(swarm, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Implement action event
    }
}
