import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class DrawingCanvas extends JPanel {
    private BufferedImage canvas;
    private Graphics2D g2d;
    private DrawingTools drawingTools;

    public DrawingCanvas() {
        drawingTools = new DrawingTools();
        setupCanvas();
        setupMouseListener();
    }

    private void setupCanvas() {
        canvas = new BufferedImage(800, 500, BufferedImage.TYPE_INT_ARGB);
        g2d = canvas.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        setBackground(Color.WHITE);
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                drawingTools.startDrawing(evt.getX(), evt.getY());
                repaint();
            }

            public void mouseReleased(MouseEvent evt) {
                drawingTools.stopDrawing(evt.getX(), evt.getY());
                repaint();
            }
        });
    }

    public void setTool(String tool) {
        drawingTools.selectTool(tool);
    }

    public void openColorPicker() {
        Color newColor = JColorChooser.showDialog(null, "Choose a Color", g2d.getColor());
        if (newColor != null) {
            g2d.setColor(newColor);
        }
    }

    public void openImage() {
        BufferedImage img = FileDialogHandler.openImage(this);
        if (img != null) {
            canvas = img;
            g2d = canvas.createGraphics();
            repaint();
        }
    }

    public void saveImage() {
        FileDialogHandler.saveImage(this, canvas);
    }
}
