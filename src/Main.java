import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {

            WebSocketServerHandler server = new WebSocketServerHandler(8081);
            server.start();
        } catch (Exception ex) {

            JOptionPane.showMessageDialog(null, "Error starting the WebSocket server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


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