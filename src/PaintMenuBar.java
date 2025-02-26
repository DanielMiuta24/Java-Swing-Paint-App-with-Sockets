import javax.swing.*;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaintMenuBar extends JMenuBar {
    private PaintForm parent;

    public PaintMenuBar(PaintForm parent) {
        this.parent = parent;

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu toolsMenu = new JMenu("Tools");

        fileMenu.add(createMenuItem("Open", "open.png", e -> parent.getDrawingCanvas().openImage()));
        fileMenu.add(createMenuItem("Save", "save.png", e -> parent.getDrawingCanvas().saveImage()));
        fileMenu.add(createMenuItem("Exit", "exit.png", e -> System.exit(0)));

        editMenu.add(createMenuItem("Undo", "undo.png", e -> System.out.println("Undo action")));
        editMenu.add(createMenuItem("Redo", "redo.png", e -> System.out.println("Redo action")));

        toolsMenu.add(createMenuItem("Pencil", "pencil.png", e -> parent.getDrawingCanvas().setTool("pencil")));
        toolsMenu.add(createMenuItem("Eraser", "eraser.png", e -> parent.getDrawingCanvas().setTool("eraser")));
        toolsMenu.add(createMenuItem("Color Picker", "color-picker.png", e -> parent.getDrawingCanvas().openColorPicker()));

        add(fileMenu);
        add(editMenu);
        add(toolsMenu);
    }

    private JMenuItem createMenuItem(String title, String iconPath, ActionListener action) {
        ImageIcon icon = loadIcon(iconPath, 24, 24);
        JMenuItem menuItem = new JMenuItem(title, icon);
        menuItem.addActionListener(action);
        return menuItem;
    }

    private ImageIcon loadIcon(String fileName, int width, int height) {
        java.net.URL iconURL = getClass().getClassLoader().getResource("icons/" + fileName);

        if (iconURL == null) {
            System.err.println("⚠️ WARNING: Icon not found - " + fileName);
            return null; // Return null instead of causing a crash
        }

        ImageIcon icon = new ImageIcon(iconURL);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

}
