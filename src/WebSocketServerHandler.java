import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketServerHandler extends WebSocketServer {
    private static final Logger LOGGER = Logger.getLogger(WebSocketServerHandler.class.getName());
    private final Set<WebSocket> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private String latestCanvasState = null;
    private final Map<WebSocket, String> authenticatedUsers = new ConcurrentHashMap<>();
    private final UserDatabaseManager userDB;

    public WebSocketServerHandler(int port) throws SQLException {
        super(new InetSocketAddress(port));
        this.userDB = new UserDatabaseManager();
        setupLogging();
    }

    private void setupLogging() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
        LOGGER.info("New client connected: " + conn.getRemoteSocketAddress());


        if (latestCanvasState != null) {
            conn.send("update_img:" + latestCanvasState);
            LOGGER.info("Sent latest canvas state to new client.");
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        LOGGER.info("Client disconnected: " + conn.getRemoteSocketAddress() + ". Reason: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LOGGER.info("Received message: " + message);

        try {
            if (message.startsWith("register:")) {
                String[] parts = message.split(":");
                if (parts.length == 3) {
                    String username = parts[1];
                    String password = parts[2];
                    boolean success = userDB.registerUser(username, password);
                    conn.send(success ? "register_success" : "register_failed");
                }
                return;
            }

            if (message.startsWith("login:")) {
                String[] parts = message.split(":");
                if (parts.length == 3) {
                    String username = parts[1];
                    String password = parts[2];
                    boolean success = userDB.loginUser(username, password);
                    if (success) {
                        authenticatedUsers.put(conn, username);
                        conn.send("login_success");
                        if (latestCanvasState != null) {
                            conn.send("update_img:" + latestCanvasState);
                        }
                    } else {
                        conn.send("login_failed");
                    }
                }
                return;
            }


            if (!authenticatedUsers.containsKey(conn)) {
                conn.send("unauthorized");
                return;
            }


            if (message.startsWith("update_img:")) {
                latestCanvasState = message.substring("update_img:".length());
            }

            for (WebSocket client : connections) {
                if (client != conn) {
                    client.send(message);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error handling message: " + e.getMessage());
            conn.send("server_error");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.severe("WebSocket error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        LOGGER.info("WebSocket server started on " + getAddress());
    }

    public static void main(String[] args) throws SQLException {
        int port = 8081; // Change the port if necessary
        WebSocketServerHandler server = new WebSocketServerHandler(port);
        server.start();
        LOGGER.info("WebSocket server is running on port: " + port);
    }
}
