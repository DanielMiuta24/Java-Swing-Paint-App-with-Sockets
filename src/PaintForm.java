import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;
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
    private int index; // Keeps track of selected tool
    private int x, y, sX, sY, cX, cY;

    // Tools for drawing and erasing
    private final BasicStroke pencilStroke = new BasicStroke(1);
    private final BasicStroke eraserStroke = new BasicStroke(20);

    // Color management
    private Color newColor = Color.BLACK;

    private JLabel pic; // Canvas label
    private JLabel colorPreview; // Label displaying the selected color

    public PaintForm() {
        // Initialize components
        initComponents();

        // Application metadata
        String appVersion = "1.0";
        String author = "Daniel";
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        // Set the frame title
        this.setTitle(String.format("Paint - v%s | © %d %s. All rights reserved.", appVersion, currentYear, author));

        // Set window size and center it
        this.setSize(900, 700);
        this.setLocationRelativeTo(null);

        // Initialize drawing surface and WebSocket
        initializeDrawingSurface();
        initializeWebSocket();
    }

    private void initComponents() {
        pic = new JLabel();
        colorPreview = new JLabel();
        colorPreview.setOpaque(true);
        colorPreview.setBackground(newColor);

        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new GridLayout(8, 1, 5, 5)); // Use a GridLayout for the tool buttons

        // Create buttons with icons
        JButton btnPencil = createIconButton("Pencil", "resources/icons/pencil.png");
        JButton btnEraser = createIconButton("Eraser", "resources/icons/eraser.png");
        JButton btnRectangle = createIconButton("Rectangle", "resources/icons/rectangle.png");
        JButton btnEllipse = createIconButton("Ellipse", "resources/icons/ellipse.png");
        JButton btnLine = createIconButton("Line", "resources/icons/line.png");
        JButton btnColor = createIconButton("Color", "resources/icons/color.png");
        JButton btnFill = createIconButton("Fill", "resources/icons/fill.png");
        JButton btnClear = createIconButton("Clear", "resources/icons/clear.png");

        // Add buttons to the tool panel
        toolPanel.add(btnPencil);
        toolPanel.add(btnEraser);
        toolPanel.add(btnRectangle);
        toolPanel.add(btnEllipse);
        toolPanel.add(btnLine);
        toolPanel.add(btnColor);
        toolPanel.add(btnFill);
        toolPanel.add(btnClear);

        // Add action listeners for tools
        btnPencil.addActionListener(e -> index = 1); // Pencil Tool
        btnEraser.addActionListener(e -> index = 2); // Eraser Tool
        btnRectangle.addActionListener(e -> index = 4); // Rectangle Tool
        btnEllipse.addActionListener(e -> index = 3); // Ellipse Tool
        btnLine.addActionListener(e -> index = 5); // Line Tool
        btnColor.addActionListener(e -> chooseColor()); // Select color
        btnFill.addActionListener(e -> index = 6); // Fill Tool
        btnClear.addActionListener(e -> clearCanvas()); // Clear canvas

        // Add Mouse Listeners for canvas
        pic.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paint = true;
                px = e.getPoint();
                py = e.getPoint();
                cX = e.getX();
                cY = e.getY();

                if (index == 6) { // Fill tool
                    fillArea(px.x, px.y, newColor); // Perform flood fill
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                paint = false;
                sX = x - cX;
                sY = y - cY;

                // Draw shapes when the mouse is released
                if (index == 3) { // Ellipse Tool
                    g.drawOval(Math.min(cX, x), Math.min(cY, y), Math.abs(sX), Math.abs(sY));
                } else if (index == 4) { // Rectangle Tool
                    g.drawRect(Math.min(cX, x), Math.min(cY, y), Math.abs(sX), Math.abs(sY));
                } else if (index == 5) { // Line Tool
                    g.drawLine(cX, cY, x, y);
                }
                pic.repaint();
                broadcastCanvasState();
            }
        });

        pic.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!paint) return;

                x = e.getX();
                y = e.getY();

                if (index == 1) { // Pencil Tool
                    g.setStroke(pencilStroke);
                    g.drawLine(px.x, px.y, x, y);
                    px = e.getPoint();
                } else if (index == 2) { // Eraser Tool
                    g.setStroke(eraserStroke);
                    g.setColor(Color.WHITE);
                    g.drawLine(px.x, px.y, x, y);
                    px = e.getPoint();
                    g.setColor(newColor); // Reset to current color
                    g.setStroke(pencilStroke); // Reset stroke size
                }
                pic.repaint();
                broadcastCanvasState();
            }
        });

        // File Menu (including New Window)
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem newWindowItem = new JMenuItem("New Window");

        openItem.addActionListener(e -> openCanvas());
        saveItem.addActionListener(e -> saveCanvas());
        newWindowItem.addActionListener(e -> new PaintForm().setVisible(true));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(newWindowItem);
        menuBar.add(fileMenu);

        // Add components to the JFrame
        this.setJMenuBar(menuBar);
        this.setLayout(new BorderLayout());
        this.add(toolPanel, BorderLayout.WEST);
        this.add(colorPreview, BorderLayout.NORTH);
        this.add(pic, BorderLayout.CENTER);
    }

    // Method to create a button with an icon
    private JButton createIconButton(String tooltip, String iconPath) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            button.setText(tooltip); // Fallback if the icon file cannot be loaded
        }
        return button;
    }

    // Initialize the drawing surface
    private void initializeDrawingSurface() {
        b_map = new BufferedImage(900, 700, BufferedImage.TYPE_INT_ARGB);
        g = b_map.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, b_map.getWidth(), b_map.getHeight()); // Fill white background
        g.setColor(newColor);
        pic.setIcon(new ImageIcon(b_map));
    }

    private void fillArea(int x, int y, Color replacementColor) {
        int targetColor = b_map.getRGB(x, y);
        if (targetColor == replacementColor.getRGB()) return;

        Queue<Point> pointsQueue = new LinkedList<>();
        pointsQueue.add(new Point(x, y));

        while (!pointsQueue.isEmpty()) {
            Point point = pointsQueue.poll();
            int currentX = point.x;
            int currentY = point.y;

            if (currentX < 0 || currentY < 0 || currentX >= b_map.getWidth() || currentY >= b_map.getHeight())
                continue;
            if (b_map.getRGB(currentX, currentY) != targetColor) continue;

            b_map.setRGB(currentX, currentY, replacementColor.getRGB());
            pointsQueue.add(new Point(currentX + 1, currentY));
            pointsQueue.add(new Point(currentX - 1, currentY));
            pointsQueue.add(new Point(currentX, currentY + 1));
            pointsQueue.add(new Point(currentX, currentY - 1));
        }

        pic.repaint();
        broadcastCanvasState();
    }

    private void chooseColor() {
        Color selectedColor = JColorChooser.showDialog(this, "Choose Color", newColor);
        if (selectedColor != null) {
            newColor = selectedColor;
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

    private void initializeWebSocket() {
        try {
            webSocket = new WebSocketClient(new URI("ws://localhost:8081/paint")) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("WebSocket connected!");
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
                    System.err.println("WebSocket error: " + ex.getMessage());
                }
            };
            webSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void broadcastCanvasState() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(b_map, "png", baos);
            String encodedImage = Base64.getEncoder().encodeToString(baos.toByteArray());
            if (webSocket != null && webSocket.isOpen()) {
                webSocket.send("update_img:" + encodedImage);
            }
        } catch (IOException ex) {
            System.err.println("Error broadcasting canvas state: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaintForm paintForm = new PaintForm();
            paintForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            paintForm.setVisible(true);
        });
    }
}