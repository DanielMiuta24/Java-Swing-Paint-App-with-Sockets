## 📘 Project Overview

This project is a **real-time collaborative drawing application** built using **Java Swing** and **WebSocket** technology. It allows multiple users to log in, draw simultaneously on a shared canvas, and see each other's cursor positions with usernames in real time. Drawing tools, file handling, and synchronization are all supported. The project demonstrates advanced concepts such as GUI design, concurrent networking, event handling, and modular software design.

---

## ✅ Implemented Features

| Feature Description                                      | Status |
|----------------------------------------------------------|--------|
| User Registration & Login                                | ✅     |
| WebSocket communication for real-time updates            | ✅     |
| Cursor tracking (with usernames)                         | ✅     |
| Drawing tools: pencil, eraser, rectangle, ellipse, line  | ✅     |
| Fill tool with recursive fill                            | ✅     |
| Canvas clearing                                          | ✅     |
| File open/save with ImageIO                              | ✅     |
| Concurrent clients & thread safety                       | ✅     |
| Exception handling and validation                        | ✅     |

---

## 🧠 Technologies Used

| Component        | Library / Technology                   |
|------------------|----------------------------------------|
| GUI              | Java Swing                             |
| Drawing          | `Graphics2D`, `BufferedImage`          |
| File IO          | `ImageIO`, `JFileChooser`, `FileDialog`|
| Networking       | `org.java_websocket`                   |
| Authentication   | In-memory via WebSocket messages       |
| Data Structures  | `ConcurrentHashMap`, `ArrayList`       |

---
## 📍 Concepts and Where They Were Used

### ✅ 1. **Polymorphism**
- Used in the `DrawingTools` class which defines multiple shape-drawing methods like `drawRectangle`, `drawEllipse`, `drawLine`.
- Each method accepts a common interface (stroke, coordinates, Graphics2D) and executes shape-specific logic.
- Example:
  ```java
  public void drawRectangle(Stroke stroke, int x, int y, int width, int height, Graphics2D g) {
      g.setStroke(stroke);
      g.drawRect(x, y, width, height);
  }
  ```

### ✅ 2. **Exception Handling**
- Extensively used across the project to prevent crashes and handle edge cases safely:
  - File operations: reading/writing PNGs
  - Networking: handling broken WebSocket connections
  - Base64 encoding/decoding errors
- Example:
  ```java
  try {
      ImageIO.write(b_map, "png", baos);
  } catch (IOException ex) {
      System.err.println("Error saving canvas: " + ex.getMessage());
  }
  ```

### ✅ 3. **HashMap / ConcurrentHashMap**
- `ConcurrentHashMap` is used to store and manage other users' cursors safely in a multithreaded environment.
- Ensures thread-safe updates when users draw or move their cursors.
- Example:
  ```java
  private final Map<String, Point> otherUserCursors = new ConcurrentHashMap<>();
  ```

### ✅ 4. **File Handling**
- Implemented using `JFileChooser`, `FileDialog`, and `ImageIO`.
- Supports saving the canvas to an image file and loading images into the canvas.
- Example:
  ```java
  JFileChooser fileChooser = new JFileChooser();
  int result = fileChooser.showSaveDialog(frame);
  if (result == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      ImageIO.write(canvasImage, "png", file);
  }
  ```

### ✅ 5. **Graphics2D**
- Central to all rendering tasks in the application.
- Supports shape drawing, custom stroke thickness, fill colors, and smooth anti-aliasing.
- Used to draw:
  - Freehand lines (pencil)
  - Shapes (rectangles, ellipses)
  - Eraser functionality (clearing small areas)

### ✅ 6. **WebSocket Communication**
- Enables real-time collaboration between clients and the server.
- Used to:
  - Broadcast canvas image updates
  - Sync cursor positions between users
  - Manage login and session authentication
- Example:
  ```java
  webSocket.send("cursor:" + username + ":" + x + ":" + y);
  ```

### ✅ 7. **Encapsulation**
- Project follows encapsulation by hiding internal fields and exposing behavior via methods:
  - `DrawingTools` encapsulates rendering logic
  - `FileDialogHandler` encapsulates file loading/saving
- Fields marked `private`, access provided via getters/setters or public methods.

### ✅ 8. **Inheritance**
- `PaintForm` extends `JFrame` for GUI.
- `WebSocketServerHandler` extends `WebSocketServer` to create custom server logic.
- Example:
  ```java
  public class PaintForm extends JFrame {
      // UI logic here
  }
  ```

### ✅ 9. **Serialization**
- Used for transmitting canvas image data over WebSocket.
- Converts `BufferedImage` to PNG, then to Base64 string for transport.
- Example:
  ```java
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  ImageIO.write(b_map, "png", baos);
  String encoded = Base64.getEncoder().encodeToString(baos.toByteArray());
  ```

### ✅ 10. **Multithreading**
- Application handles multiple clients concurrently.
- `ConcurrentHashMap` ensures thread-safe cursor handling.
- GUI updates on the Swing Event Dispatch Thread (EDT) using `SwingUtilities.invokeLater`.
- WebSocket server runs on a separate thread for each connection.

### ✅ 11. **Sorting Algorithm**
- Implemented in the paired “Grade System” project.
- Used to sort students based on average grades using a comparator:
  ```java
  students.sort(Comparator.comparingDouble(Student::getAverageGrade));
  ```

### ✅ 12. **ArrayList**
- Used in the paint app and class projects for managing collections of elements like students or drawing history.
- Example from Grade System:
  ```java
  List<Student> students = new ArrayList<>();
  ```

### ✅ 13. **Inner Classes**
- Anonymous inner classes are used for handling button clicks and mouse events.
- Keeps logic close to the component using it.
- Example:
  ```java
  clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
          clearCanvas();
      }
  });
  ```


---
