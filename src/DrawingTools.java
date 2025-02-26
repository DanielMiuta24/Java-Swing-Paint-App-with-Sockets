import java.awt.Color;

public class DrawingTools {
    private String selectedTool = "pencil";
    private Color selectedColor = Color.BLACK;

    public void setColor(Color color) {
        this.selectedColor = color;
        System.out.println("Selected Color: " + selectedColor);
    }

    public void selectTool(String tool) {
        this.selectedTool = tool;
        System.out.println("Selected Tool: " + selectedTool);
    }

    public void startDrawing(int x, int y) {
        System.out.println("Start Drawing at: (" + x + ", " + y + ") with " + selectedTool + " in " + selectedColor);
    }

    public void stopDrawing(int x, int y) {
        System.out.println("Stop Drawing at: (" + x + ", " + y + ")");
    }
}
