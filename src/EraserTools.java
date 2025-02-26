import java.awt.*;
import java.awt.image.BufferedImage;

public class EraserTools {
    private Graphics2D graphics;
    private BufferedImage canvas;
    private int eraserSize = 20;

    public EraserTools(BufferedImage canvas) {
        this.canvas = canvas;
        this.graphics = canvas.createGraphics();
        this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void setEraserSize(int size) {
        this.eraserSize = size;
    }

    public void erase(int x1, int y1, int x2, int y2) {
        graphics.setColor(Color.WHITE);
        graphics.setStroke(new BasicStroke(eraserSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(x1, y1, x2, y2);
    }

    public void clearCanvas() {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
