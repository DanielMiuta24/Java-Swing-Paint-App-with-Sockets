import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class DrawingTools {

    private final EraserTools eraserTools = new EraserTools(); // Instance of EraserTools

    /**
     * Freehand pencil drawing tool.
     * Draws a line segment from the previous point to the current point during a mouse drag.
     * Ensures a smooth line by progressively updating the points.
     *
     * @param e       MouseEvent to get the current mouse position.
     * @param g       Graphics2D object to perform the drawing.
     * @param pen     BasicStroke defining the thickness and style of the pencil.
     * @param points  Array to track the previous and current points.
     */
    public void pencil(MouseEvent e, Graphics2D g, BasicStroke pen, Point[] points) {
        try {
            // Set the stroke (pen width, style)
            g.setStroke(pen);

            // Initialize the starting point only if this is the first event
            if (points[0] == null) {
                points[0] = e.getPoint(); // Store the initial mouse position
                return; // Skip drawing on the first point
            }

            // Get the new current point
            points[1] = e.getPoint();

            // Draw a line from the previous point to the current point
            g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);

            // Update the previous point to be the current one
            points[0] = points[1];
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error using pencil tool: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Draws a straight line between two points.
     *
     * @param pen  BasicStroke defining the thickness and style of the line.
     * @param cX   Starting X-coordinate.
     * @param cY   Starting Y-coordinate.
     * @param x    Ending X-coordinate.
     * @param y    Ending Y-coordinate.
     * @param g    Graphics2D object to perform the drawing.
     */
    public void drawLine(BasicStroke pen, int cX, int cY, int x, int y, Graphics2D g) {
        try {
            g.setStroke(pen);
            g.drawLine(cX, cY, x, y);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error drawing line: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Draws a rectangle based on the starting and ending coordinates.
     *
     * @param pen  BasicStroke defining the thickness and style of the rectangle.
     * @param cX   Top-left X-coordinate.
     * @param cY   Top-left Y-coordinate.
     * @param sX   Rectangle width.
     * @param sY   Rectangle height.
     * @param g    Graphics2D object to perform the drawing.
     */
    public void drawRectangle(BasicStroke pen, int cX, int cY, int sX, int sY, Graphics2D g) {
        try {
            g.setStroke(pen);
            g.drawRect(cX, cY, sX, sY);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error drawing rectangle: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Draws an ellipse inside a bounding box defined by the starting and ending coordinates.
     *
     * @param pen  BasicStroke defining the thickness and style of the ellipse.
     * @param cX   Top-left X-coordinate of the bounding box.
     * @param cY   Top-left Y-coordinate of the bounding box.
     * @param sX   Ellipse width.
     * @param sY   Ellipse height.
     * @param g    Graphics2D object to perform the drawing.
     */
    public void drawEllipse(BasicStroke pen, int cX, int cY, int sX, int sY, Graphics2D g) {
        try {
            g.setStroke(pen);
            g.drawOval(cX, cY, sX, sY);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error drawing ellipse: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills an area with a new color using a flood-fill algorithm.
     * Starts at the clicked point and replaces all connected pixels of the same color.
     *
     * @param bm       BufferedImage to perform the fill operation on.
     * @param x        Starting X-coordinate.
     * @param y        Starting Y-coordinate.
     * @param newColor The color to fill with.
     */
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

    /**
     * Helper function to validate a pixel during the fill operation.
     *
     * @param bm         BufferedImage to validate on.
     * @param pixelStack Stack holding the processing points.
     * @param x          Current pixel X-coordinate.
     * @param y          Current pixel Y-coordinate.
     * @param oldColor   Original color to be replaced.
     * @param newColor   New color to replace with.
     */
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

    /**
     * Erases part of the canvas or image by delegating to EraserTools.
     *
     * @param px          Previous point.
     * @param py          Current point.
     * @param g           Graphics2D object to perform the erasing.
     * @param eraseStroke BasicStroke defining the eraser size.
     * @param e           MouseEvent for tracking the current coordinates.
     */
    public void eraserTool(Point px, Point py, Graphics2D g, BasicStroke eraseStroke, MouseEvent e) {
        try {
            eraserTools.erase(px, py, g, eraseStroke, e); // Delegated to EraserTools
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error using eraser tool: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears the entire drawing surface or component by delegating to EraserTools.
     *
     * @param g         Graphics2D object to clear.
     * @param component The UI component (e.g., JPanel) to repaint.
     */
    public void clearTool(Graphics2D g, JComponent component) {
        try {
            eraserTools.clearCanvas(g, component.getWidth(), component.getHeight()); // Delegate to EraserTools
            component.repaint(); // Repaint the component to apply changes
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error clearing canvas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}