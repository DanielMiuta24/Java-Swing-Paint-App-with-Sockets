import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Start WebSocket server at port 8081
            WebSocketServerHandler server = new WebSocketServerHandler(8081); // Constructor expects an integer (port)
            server.start();
        } catch (Exception ex) {
            // Show error if WebSocket server fails to start
            JOptionPane.showMessageDialog(null, "Error starting the WebSocket server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Start Swing GUI
        SwingUtilities.invokeLater(() -> {
            try {
                PaintForm firstWindow = new PaintForm();
                firstWindow.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error initializing the application: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}