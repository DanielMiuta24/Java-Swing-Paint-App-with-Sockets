import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class DrawingTools {

    public void pencil(MouseEvent e, Graphics2D g, BasicStroke pen, Point[] points) {
        try {
            if (points[0] == null || points[1] == null) return;

            points[0] = e.getPoint();
            g.setStroke(pen);
            g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
            points[1] = points[0];
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error using pencil tool: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void drawLine(BasicStroke pen, int cX, int cY, int x, int y, Graphics2D g) {
        try {
            g.setStroke(pen);
            g.drawLine(cX, cY, x, y);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error drawing line: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void drawRectangle(BasicStroke pen, int cX, int cY, int sX, int sY, Graphics2D g) {
        try {
            g.setStroke(pen);
            g.drawRect(cX, cY, sX, sY);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error drawing rectangle: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void drawEllipse(BasicStroke pen, int cX, int cY, int sX, int sY, Graphics2D g) {
        try {
            g.setStroke(pen);
            g.drawOval(cX, cY, sX, sY);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error drawing ellipse: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void fill(BufferedImage bm, int x, int y, Color newColor) {
        try {
            if (bm == null || x < 0 || y < 0 || x >= bm.getWidth() || y >= bm.getHeight()) return;

            Color oldColor = new Color(bm.getRGB(x, y));
            if (oldColor.equals(newColor)) return;

            Stack<Point> pixelStack = new Stack<>();
            pixelStack.push(new Point(x, y));
            bm.setRGB(x, y, newColor.getRGB());

            while (!pixelStack.isEmpty()) {
                Point pt = pixelStack.pop();
                if (pt.x > 0 && pt.y > 0 && pt.x < bm.getWidth() - 1 && pt.y < bm.getHeight() - 1) {
                    validate(bm, pixelStack, pt.x - 1, pt.y, oldColor, newColor);
                    validate(bm, pixelStack, pt.x, pt.y - 1, oldColor, newColor);
                    validate(bm, pixelStack, pt.x + 1, pt.y, oldColor, newColor);
                    validate(bm, pixelStack, pt.x, pt.y + 1, oldColor, newColor);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error during fill operation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validate(BufferedImage bm, Stack<Point> pixelStack, int x, int y, Color oldColor, Color newColor) {
        try {
            Color currentColor = new Color(bm.getRGB(x, y));
            if (currentColor.equals(oldColor)) {
                pixelStack.push(new Point(x, y));
                bm.setRGB(x, y, newColor.getRGB());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error validating pixel at (" + x + ", " + y + "): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eraserTool(Point px, Point py, Graphics2D g, BasicStroke eraseStroke, MouseEvent e) {
        try {
            if (px == null || py == null) return;

            px = e.getPoint();
            g.setStroke(eraseStroke);
            g.setColor(Color.WHITE);
            g.drawLine(px.x, px.y, py.x, py.y);
            py = px;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error using eraser tool: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void clearTool(Graphics2D g, JPanel panel) {
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            panel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error clearing canvas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}