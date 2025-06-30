import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

public class EraserTools {


    public void erase(Point px, Point py, Graphics2D g, BasicStroke eraseStroke, MouseEvent e) {
        if (px == null || py == null) return;

        try {

            g.setStroke(eraseStroke);
            g.setColor(Color.WHITE);


            g.drawLine(px.x, px.y, py.x, py.y);


            px.setLocation(e.getPoint());
        } catch (Exception ex) {

            JOptionPane.showMessageDialog(null, "Error during erasing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void clearCanvas(Graphics2D g, int width, int height) {
        try {
            g.setColor(Color.WHITE); // Background color
            g.fillRect(0, 0, width, height); // Fill the entire canvas
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error clearing canvas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}