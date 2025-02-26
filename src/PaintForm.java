import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class PaintForm extends JFrame {

    // WebSocket for server communication
    private WebSocketClient webSocket;

    // BufferedImage and Graphics for drawing on the canvas
    private BufferedImage b_map;
    private Graphics2D g;

    // Drawing state variables
    private boolean paint = false;
    private Point px, py;
    private int index, x, y, sX, sY, cX, cY;

    // Tools for drawing and erasing
    private final BasicStroke p = new BasicStroke(1);
    private final BasicStroke erase = new BasicStroke(20);

    // Color management
    private final JColorChooser colorChooser = new JColorChooser();
    private Color newColor = Color.BLACK;

    private JLabel pic;
    private JLabel colorPreview;

    // Constructor
    public PaintForm() {
        // Initialize components
        initComponents();

        // Application metadata
        String appVersion = "1.0"; // Replace with actual version
        String author = "Daniel"; // Replace with actual author name
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        // Set the frame title
        this.setTitle(String.format("Paint - v%s | © %d %s. All rights reserved.", appVersion, currentYear, author));

        // Set form size
        this.setSize(900, 700);

        // Center the form on the screen
        this.setLocationRelativeTo(null);

        // Initialize drawing surface and WebSocket
        initializeDrawingSurface();
        initializeWebSocket();
    }

    // Initialize UI components
    private void initComponents() {
        pic = new JLabel();
        colorPreview = new JLabel();
        colorPreview.setOpaque(true);
        colorPreview.setBackground(newColor);

        JPanel toolPanel = new JPanel();
        JButton btnPencil = new JButton("Pencil");
        JButton btnEraser = new JButton("Eraser");
        JButton btnRectangle = new JButton("Rectangle");
        JButton btnEllipse = new JButton("Ellipse");
        JButton btnLine = new JButton("Line");
        JButton btnColor = new JButton("Color");
        JButton btnClear = new JButton("Clear");
        JButton btnSave = new JButton("Save");
        JButton btnOpen = new JButton("Open");
        JButton btnNewWindow = new JButton("New Window");

        toolPanel.add(btnPencil);
        toolPanel.add(btnEraser);
        toolPanel.add(btnRectangle);
        toolPanel.add(btnEllipse);
        toolPanel.add(btnLine);
        toolPanel.add(btnColor);
        toolPanel.add(btnClear);
        toolPanel.add(btnSave);
        toolPanel.add(btnOpen);
        toolPanel.add(btnNewWindow);

        // Add listeners
        btnPencil.addActionListener(e -> index = 1);
        btnEraser.addActionListener(e -> index = 2);
        btnRectangle.addActionListener(e -> index = 4);
        btnEllipse.addActionListener(e -> index = 3);
        btnLine.addActionListener(e -> index = 5);
        btnColor.addActionListener(e -> chooseColor());
        btnClear.addActionListener(e -> clearCanvas());
        btnSave.addActionListener(e -> saveCanvas());
        btnOpen.addActionListener(e -> openCanvas());
        btnNewWindow.addActionListener(e -> openNewWindow());

        pic.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paint = true;
                py = e.getPoint();
                cX = e.getX();
                cY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                paint = false;
                sX = x - cX;
                sY = y - cY;

                if (index == 3) { // Ellipse
                    g.drawOval(Math.min(cX, x), Math.min(cY, y), Math.abs(sX), Math.abs(sY));
                    broadcastCanvasState();
                } else if (index == 4) { // Rectangle
                    g.drawRect(Math.min(cX, x), Math.min(cY, y), Math.abs(sX), Math.abs(sY));
                    broadcastCanvasState();
                } else if (index == 5) { // Line
                    g.drawLine(cX, cY, x, y);
                    broadcastCanvasState();
                }
                pic.repaint();
            }
        });

        pic.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!paint) return;

                if (index == 1) { // Pencil
                    g.drawLine(px.x, px.y, e.getX(), e.getY());
                    px = e.getPoint();
                    broadcastCanvasState();
                } else if (index == 2) { // Eraser
                    g.setStroke(erase);
                    g.setColor(Color.WHITE);
                    g.drawLine(px.x, px.y, e.getX(), e.getY());
                    px = e.getPoint();
                    broadcastCanvasState();
                    g.setStroke(p);
                    g.setColor(newColor); // Reset color
                }
                pic.repaint();
            }
        });

        this.setLayout(new BorderLayout());
        this.add(toolPanel, BorderLayout.NORTH);
        this.add(pic, BorderLayout.CENTER);
    }

    // Initialize drawing surface
    private void initializeDrawingSurface() {
        b_map = new BufferedImage(900, 700, BufferedImage.TYPE_INT_ARGB);
        g = b_map.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, b_map.getWidth(), b_map.getHeight());
        g.setColor(newColor);
        pic.setIcon(new ImageIcon(b_map));
    }

    // WebSocket setup
    private void initializeWebSocket() {
        try {
            webSocket = new WebSocketClient(new URI("ws://localhost:8081/paint")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("WebSocket connection established.");
                }

                @Override
                public void onMessage(String message) {
                    if (message.startsWith("update_img:")) {
                        String base64Image = message.substring("update_img:".length());
                        try {
                            byte[] bytes = Base64.getDecoder().decode(base64Image);
                            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
                            g.drawImage(img, 0, 0, null);
                            pic.repaint();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("WebSocket error: " + ex.getMessage());
                }
            };
            webSocket.connect();
        } catch (URISyntaxException e) {
            System.out.println("WebSocket setup failed: " + e.getMessage());
        }
    }

    // Update the canvas state
    private void broadcastCanvasState() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(b_map, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            if (webSocket != null && webSocket.isOpen()) {
                webSocket.send("update_img:" + base64Image);
            }
        } catch (IOException ex) {
            System.out.println("Error broadcasting state: " + ex.getMessage());
        }
    }

    private void chooseColor() {
        Color selected = JColorChooser.showDialog(this, "Choose Color", newColor);
        if (selected != null) {
            newColor = selected;
            g.setColor(newColor);
            colorPreview.setBackground(newColor);
        }
    }

    private void clearCanvas() {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, b_map.getWidth(), b_map.getHeight());
        g.setColor(newColor);
        pic.repaint();
        broadcastCanvasState();
    }

    private void saveCanvas() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(b_map, "png", fileChooser.getSelectedFile());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openCanvas() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                b_map = ImageIO.read(fileChooser.getSelectedFile());
                g = b_map.createGraphics();
                pic.setIcon(new ImageIcon(b_map));
                pic.repaint();
                broadcastCanvasState();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Open Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openNewWindow() {
        new PaintForm().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaintForm paintForm = new PaintForm();
            paintForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            paintForm.setVisible(true);
        });
    }
}