package utils;

import ptolemy.plot.Plot;
import ptolemy.plot.PlotFrame;

import java.awt.*;

/**
 * Log/Log Graph.
 */
public class LogPlot extends Plot {
    public LogPlot(String xLabel, String yLabel) {
        this.setXLog(true);
        this.setYLog(true);
        this.setXLabel(xLabel);
        this.setYLabel(yLabel);
        this.setMarksStyle("none", 0);
        this.setMarksStyle("dots", 1);
    }

    /**
     * Plot a new point.
     * @param x
     * @param y
     */
    public void addPoint(double x, double y) {
        this.setColors(new Color[]{Color.blue, Color.red});
        this.clear(1);
        this.addPoint(0, x, y, true);
        this.addPoint(1, x, y, false);
        this.fillPlot();
    }

    /**
     * Clear 2 data sets.
     */
    public void clearAll() {
        this.clear(0);
        this.clear(1);
    }

    /**
     * This is an example to draw y = x*x + a by using Plot of the parent class.
     */
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
