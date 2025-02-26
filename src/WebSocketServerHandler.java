import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketServerHandler extends WebSocketServer {
    private static final Logger LOGGER = Logger.getLogger(WebSocketServerHandler.class.getName());
    private final Set<WebSocket> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private String latestCanvasState = null; // Stores the last known canvas state

    public WebSocketServerHandler(int port) {
        super(new InetSocketAddress(port));
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

        // Send the latest canvas state to the newly connected client
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

        // If the message is a canvas update, store the latest state
        if (message.startsWith("update_img:")) {
            latestCanvasState = message.substring("update_img:".length());
        }

        // Broadcast to all clients (except the sender)
        for (WebSocket client : connections) {
            if (client != conn) {
                client.send(message);
            }
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

    public static void main(String[] args) {
        int port = 8081; // Change the port if necessary
        WebSocketServerHandler server = new WebSocketServerHandler(port);
        server.start();
        LOGGER.info("WebSocket server is running on port: " + port);
    }
}
