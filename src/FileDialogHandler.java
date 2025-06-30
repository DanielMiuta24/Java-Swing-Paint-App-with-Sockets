import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileDialogHandler {

    public BufferedImage openImage(JFrame parent) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            return ImageIO.read(file); // Reads the image from the selected file
        }
        return null; // No file was selected
    }


    public void saveImage(JFrame parent, BufferedImage image) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png"); // Defaults to PNG
            }
            ImageIO.write(image, "png", file); // Saves the image as a PNG file
        }
    }
}