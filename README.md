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
  public void drawRectangle(Stroke stroke, int x, int y, int width, int height, Graphics2D g) { ... }
  ```

### ✅ 2. **Exception Handling**
- Extensively used to ensure safe execution:
  - File open/save (`IOException`)
  - Image processing (`Base64 decoding`, `ImageIO.read`)
  - WebSocket communication (`URISyntaxException`, `InterruptedException`)
- Example:
  ```java
  try {
      ImageIO.write(b_map, "png", baos);
  } catch (IOException ex) {
      System.err.println("Error broadcasting canvas state: " + ex.getMessage());
  }
  ```

### ✅ 3. **HashMap / ConcurrentHashMap**
- Used to store cursor positions of other users:
  ```java
  private final Map<String, Point> otherUserCursors = new ConcurrentHashMap<>();
  ```
- This ensures **thread-safe updates** when multiple users are moving their cursors. The UI updates based on the latest cursor data received via WebSocket.
- Displayed in `paintComponent`:
  ```java
  g2.drawString(name, p.x + 12, p.y - 12);
  g2.fillOval(p.x, p.y, 6, 6);
  ```

### ✅ 4. **File Handling**
- Used to open and save drawings using `JFileChooser` and `ImageIO`.
- Open: Reads image from file and converts to `BufferedImage`.
- Save: Exports `BufferedImage` as PNG.

### ✅ 5. **Graphics2D**
- Central to all drawing operations:
  - Tools like pencil, eraser, and shapes are implemented using `Graphics2D` methods: `drawLine`, `drawOval`, `fillRect`, etc.
- Custom stroke width and anti-aliasing for smooth visuals.

### ✅ 6. **WebSocket Communication**
- Real-time data exchange for:
  - Authentication (`login:username:password`)
  - Canvas image sync (`update_img:<base64>`)
  - Cursor sync (`cursor:username:x:y`)
- Implemented using `org.java_websocket.client.WebSocketClient`.
- Supports multiple users connected to the same canvas.

---
