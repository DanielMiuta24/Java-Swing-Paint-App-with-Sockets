import javax.swing.*;
import java.awt.*;

public class PaintForm extends JFrame {
    private PaintMenuBar paintMenuBar;
    private PaintToolBar paintToolBar;
    private DrawingCanvas drawingCanvas;

    public PaintForm() {
        setTitle("Java Swing Paint App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add components following the requested pattern
        paintMenuBar = new PaintMenuBar(this);
        setJMenuBar(paintMenuBar);

        paintToolBar = new PaintToolBar(this);
        add(paintToolBar, BorderLayout.NORTH);

        drawingCanvas = new DrawingCanvas();
        add(drawingCanvas, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public DrawingCanvas getDrawingCanvas() {
        return drawingCanvas;
    }
}
