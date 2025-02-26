import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class PaintForm extends JFrame {
    private final DrawingTools drawingTools = new DrawingTools(); // Handling drawing
    private final FileDialogHandler fileHandler = new FileDialogHandler(); // File actions

    // WebSocket for communication
    private WebSocketClient webSocket;

    // BufferedImage and Graphics
    private BufferedImage b_map;
    private Graphics2D g;

    // Tool states
    private boolean paint = false;
    private int index; // Tracks selected tool
    private int x, y, cX, cY, sX, sY;
    private Point px, py; // Previous and current points for drawing

    // Tool-specific parameters
    private final BasicStroke pencilStroke = new BasicStroke(1);
    private final BasicStroke eraserStroke = new BasicStroke(20);

    // Current color configuration
    private Color newColor = Color.BLACK;

    private JLabel pic;         // Drawing canvas
    private JLabel colorPreview; // Color preview box

    public PaintForm() {
        // Initialize GUI components
        initComponents();

        // Set JFrame properties
        setTitle("Paint Application");
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Initialize drawing area and WebSocket
        initializeDrawingSurface();
        initializeWebSocket();
    }

    private void initComponents() {
        pic = new JLabel();
        colorPreview = new JLabel();
        colorPreview.setOpaque(true);
        colorPreview.setBackground(newColor);

        // Tool panel for tools and actions
        JPanel toolPanel = new JPanel(new GridLayout(8, 1, 5, 5));

        // Buttons for tools
        JButton btnPencil = createIconButton("Pencil", "resources/icons/pencil.png");
        JButton btnEraser = createIconButton("Eraser", "resources/icons/eraser.png");
        JButton btnRectangle = createIconButton("Rectangle", "resources/icons/rectangle.png");
        JButton btnEllipse = createIconButton("Ellipse", "resources/icons/ellipse.png");
        JButton btnLine = createIconButton("Line", "resources/icons/line.png");
        JButton btnColor = createIconButton("Color", "resources/icons/color.png");
        JButton btnFill = createIconButton("Fill", "resources/icons/fill.png");
        JButton btnClear = createIconButton("Clear", "resources/icons/clear.png");

        // Add actions to tool buttons
        btnPencil.addActionListener(e -> {
            index = 1; // Pencil Tool
            g.setColor(newColor);
            g.setStroke(pencilStroke);
        });
        btnEraser.addActionListener(e -> {
            index = 2; // Eraser Tool
            g.setColor(Color.WHITE);
            g.setStroke(eraserStroke);
        });
        btnRectangle.addActionListener(e -> index = 4); // Rectangle Tool
        btnEllipse.addActionListener(e -> index = 3); // Ellipse Tool
        btnLine.addActionListener(e -> index = 5); // Line Tool
        btnColor.addActionListener(e -> chooseColor()); // Color selection
        btnFill.addActionListener(e -> index = 6); // Fill Tool
        btnClear.addActionListener(e -> clearCanvas()); // Clear Canvas

        // Add buttons to tool panel
        toolPanel.add(btnPencil);
        toolPanel.add(btnEraser);
        toolPanel.add(btnRectangle);
        toolPanel.add(btnEllipse);
        toolPanel.add(btnLine);
        toolPanel.add(btnColor);
        toolPanel.add(btnFill);
        toolPanel.add(btnClear);

        // Mouse listeners for handling canvas drawing
        pic.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paint = true;
                px = e.getPoint();
                py = e.getPoint();
                cX = e.getX();
                cY = e.getY();

                // Reset Graphics2D settings based on the tool
                if (index == 1) { // Pencil Tool
                    g.setColor(newColor);
                    g.setStroke(pencilStroke);
                } else if (index == 2) { // Eraser Tool
                    g.setColor(Color.WHITE); // Background color
                    g.setStroke(eraserStroke);
                }

                if (index == 6) { // Fill Tool
                    drawingTools.fill(b_map, px.x, px.y, newColor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                paint = false;
                sX = x - cX;
                sY = y - cY;

                // Ensure newColor is applied
                g.setColor(newColor);

                switch (index) {
                    case 3: // Ellipse Tool
                        drawingTools.drawEllipse(pencilStroke, Math.min(cX, x), Math.min(cY, y),
                                Math.abs(sX), Math.abs(sY), g);
                        break;
                    case 4: // Rectangle Tool
                        drawingTools.drawRectangle(pencilStroke, Math.min(cX, x), Math.min(cY, y),
                                Math.abs(sX), Math.abs(sY), g);
                        break;
                    case 5: // Line Tool
                        drawingTools.drawLine(pencilStroke, cX, cY, x, y, g);
                        break;
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

                switch (index) {
                    case 1: // Pencil Tool
                        g.setColor(newColor); // Dynamically use newColor
                        drawingTools.pencil(e, g, pencilStroke, new Point[]{px, py});
                        px = py;
                        py = e.getPoint();
                        break;
                    case 2: // Eraser Tool
                        g.setColor(Color.WHITE); // Background color for eraser
                        drawingTools.eraserTool(px, py, g, eraserStroke, e);
                        px = py;
                        py = e.getPoint();
                        break;
                }

                pic.repaint();
                broadcastCanvasState();
            }
        });

        // File menu setup for open/save/new
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

        this.setJMenuBar(menuBar);
        this.setLayout(new BorderLayout());
        this.add(toolPanel, BorderLayout.WEST);
        this.add(colorPreview, BorderLayout.NORTH);
        this.add(pic, BorderLayout.CENTER);
    }

    private JButton createIconButton(String tooltip, String iconPath) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            button.setText(tooltip); // Fallback if icon fails
        }
        return button;
    }

    private void initializeDrawingSurface() {
        b_map = new BufferedImage(900, 700, BufferedImage.TYPE_INT_ARGB);
        g = b_map.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, b_map.getWidth(), b_map.getHeight());
        g.setColor(newColor);
        pic.setIcon(new ImageIcon(b_map));
    }

    private void chooseColor() {
        Color selectedColor = JColorChooser.showDialog(this, "Choose a color", newColor);
        if (selectedColor != null) {
            newColor = selectedColor;
            colorPreview.setBackground(newColor);
        }
    }

    private void clearCanvas() {
        drawingTools.clearTool(g, pic);
        broadcastCanvasState();
    }

    private void saveCanvas() {
        try {
            fileHandler.saveImage(this, b_map);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCanvas() {
        try {
            BufferedImage newImage = fileHandler.openImage(this);
            if (newImage != null) {
                b_map = newImage;
                g = b_map.createGraphics();
                pic.setIcon(new ImageIcon(b_map));
                pic.repaint();
                broadcastCanvasState();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(),
                    "Open Error", JOptionPane.ERROR_MESSAGE);
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