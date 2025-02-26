import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileDialogHandler {
    public static BufferedImage openImage(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                return ImageIO.read(fileChooser.getSelectedFile());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error loading image: " + e.getMessage(), "Open Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    public static void saveImage(Component parent, BufferedImage image) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(image, "png", new File(fileChooser.getSelectedFile().getAbsolutePath() + ".png"));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error saving image: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
