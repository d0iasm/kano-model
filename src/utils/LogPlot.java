package utils;

import ptolemy.plot.*;

/**
 * Log/Log Ptplot program to draw a y = x*x + a
 */
public class LogPlot {
    public static void main(String args[]) {

        Plot plot = new Plot();             // Make a default plot
        plot.setXLog(true);                 // Set x log axis
        plot.setYLog(true);                 // Set y log axis

        // Add plot the dataset 0
        double a = 0.0;
        for (int i = 0; i < 100; i++) {
            double x = i;                  // Set x/y values
            double y = x * x + a;
            plot.addPoint(0, x, y, true);  // Add to data 0

        }

        plot.setTitle("Log Plot");      // Set plot title (optional)
        plot.setXLabel("Time");         // Set the x/y labels
        plot.setYLabel("Velocity");

        // Make a frame to display the plot in
        PlotFrame frame = new PlotFrame("A Log plot", plot);
        frame.setSize(600, 400);              // Set size of frame (in pixels)
        frame.setVisible(true);              // Make frame visible
    }
}
