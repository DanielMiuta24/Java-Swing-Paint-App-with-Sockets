import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileDialogHandler {

    public void saveFileDialog(BufferedImage bufferedImage, JComponent component) {
        try {
            if (bufferedImage == null || component == null) {
                throw new IllegalArgumentException("BufferedImage or Component cannot be null.");
            }

            // Create and configure file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save an Image File");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG Image", "jpg"));

            // Show save dialog
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".jpg");

                // Save the image as JPEG
                BufferedImage tempImage = bufferedImage.getSubimage(0, 0, component.getWidth(), component.getHeight());
                ImageIO.write(tempImage, "jpg", fileToSave);

                JOptionPane.showMessageDialog(null, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error saving the image file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An unexpected error occurred while saving the file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openFileDialog(BufferedImage[] bufferedImage, JLabel imageLabel, Graphics2D[] graphics) {
        try {
            if (imageLabel == null) {
                throw new IllegalArgumentException("Image label cannot be null.");
            }

            // Create and configure file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Insert an Image");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files (*.jpg, *.jpeg)", "jpg", "jpeg"));

            // Show open dialog
            int userSelection = fileChooser.showOpenDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                // Load the selected image
                BufferedImage loadedImage = ImageIO.read(selectedFile);
                if (bufferedImage != null && bufferedImage.length > 0) {
                    bufferedImage[0] = loadedImage;
                }

                // Update the image label
                imageLabel.setIcon(new ImageIcon(loadedImage));

                // Update the graphics context
                if (graphics != null && graphics.length > 0) {
                    graphics[0] = (Graphics2D) loadedImage.getGraphics();
                }
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Open Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "The file could not be loaded as an image: " + ex.getMessage(), "Open Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An unexpected error occurred while opening the file: " + ex.getMessage(), "Open Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}