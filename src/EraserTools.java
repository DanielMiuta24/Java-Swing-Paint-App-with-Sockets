import java.awt.*;

public class EraserTools {
    public static void erase(Graphics2D g2d, int x1, int y1, int x2, int y2, int size) {
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(size));
        g2d.drawLine(x1, y1, x2, y2);
    }

    public static void clearCanvas(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
    }
}
