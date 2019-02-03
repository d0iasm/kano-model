package sps_p;

/**
 * Entry point for this program. Just create an environment and run it.
 */
public class Main {
    public static void main(String[] args) {
        Environment env = new Environment(800, 800, 50, 2);
        env.run();
    }
}