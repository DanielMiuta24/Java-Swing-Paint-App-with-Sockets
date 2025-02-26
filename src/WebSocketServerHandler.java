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

    // Add constructor that takes an int (port)
    public WebSocketServerHandler(int port) {
        super(new InetSocketAddress(port)); // Pass the port to the superclass constructor
        setupLogging();
    }

    private void setupLogging() {
        // Configure logging setup
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
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        LOGGER.info("Client disconnected: " + conn.getRemoteSocketAddress() + ". Reason: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LOGGER.info("Received message: " + message);
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
}