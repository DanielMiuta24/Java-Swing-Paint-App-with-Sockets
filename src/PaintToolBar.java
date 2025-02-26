import javax.swing.*;
import java.awt.*;

public class PaintToolBar extends JPanel {
    private PaintForm parent;
    private JLabel colorBox;

    public PaintToolBar(PaintForm parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        // Left: Color Preview Box
        JPanel colorPanel = new JPanel();
        colorBox = new JLabel();
        colorBox.setPreferredSize(new Dimension(50, 50));
        colorBox.setOpaque(true);
        colorBox.setBackground(Color.BLACK);
        colorBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parent.getDrawingCanvas().openColorPicker();
            }
        });
        colorPanel.add(colorBox);

        // Right: Tool Buttons
        JPanel toolButtonsPanel = new JPanel(new GridLayout(2, 4));
        toolButtonsPanel.add(createToolButton("Pencil", "pencil.png", "pencil"));
        toolButtonsPanel.add(createToolButton("Eraser", "eraser.png", "eraser"));
        toolButtonsPanel.add(createToolButton("Fill", "fill.png", "fill"));
        toolButtonsPanel.add(createToolButton("Line", "line.png", "line"));
        toolButtonsPanel.add(createToolButton("Rectangle", "rectangle.png", "rectangle"));
        toolButtonsPanel.add(createToolButton("Circle", "circle.png", "circle"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, colorPanel, toolButtonsPanel);
        splitPane.setDividerLocation(60);

        add(splitPane, BorderLayout.NORTH);
    }

    private JButton createToolButton(String title, String iconPath, String toolName) {
        ImageIcon icon = loadIcon(iconPath, 24, 24);
        JButton button = new JButton(icon);
        button.setToolTipText(title);
        button.addActionListener(e -> parent.getDrawingCanvas().setTool(toolName));
        return button;
    }

    private ImageIcon loadIcon(String fileName, int width, int height) {
        return new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/" + fileName))
                .getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
}
