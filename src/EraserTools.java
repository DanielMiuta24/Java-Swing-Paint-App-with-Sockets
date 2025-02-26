import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

public class EraserTools {

    /**
     * Simulates an erase action by drawing a white line (background color) between two points.
     *
     * @param px          Previous point.
     * @param py          Current point.
     * @param g           Graphics2D object to perform the erasing.
     * @param eraseStroke BasicStroke defining the eraser size.
     * @param e           MouseEvent to track the current point coordinates.
     */
    public void erase(Point px, Point py, Graphics2D g, BasicStroke eraseStroke, MouseEvent e) {
        if (px == null || py == null) return;

        try {
            // Set the stroke and color to "erase" (background color)
            g.setStroke(eraseStroke);
            g.setColor(Color.WHITE); // Assuming WHITE is the background color

            // Draw the erasing path
            g.drawLine(px.x, px.y, py.x, py.y);

            // Update the previous point for the next drag event
            px.setLocation(e.getPoint());
        } catch (Exception ex) {
            // Safeguard against errors during the erasing operation
            JOptionPane.showMessageDialog(null, "Error during erasing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears the entire canvas by filling it with white.
     *
     * @param g      Graphics2D object tied to the canvas.
     * @param width  The width of the canvas.
     * @param height The height of the canvas.
     */
    public void clearCanvas(Graphics2D g, int width, int height) {
        try {
            g.setColor(Color.WHITE); // Background color
            g.fillRect(0, 0, width, height); // Fill the entire canvas
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error clearing canvas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}