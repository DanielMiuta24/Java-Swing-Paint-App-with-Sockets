import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDatabaseManager {
    private final Connection connection;

    public UserDatabaseManager() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        createTable();
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password_hash TEXT NOT NULL)";
        connection.createStatement().execute(sql);
    }

    public boolean registerUser(String username, String password) throws SQLException {
        if (userExists(username)) return false;
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, hash);
        return stmt.executeUpdate() > 0;
    }

    public boolean loginUser(String username, String password) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return BCrypt.checkpw(password, rs.getString("password_hash"));
        }
        return false;
    }

    private boolean userExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        return stmt.executeQuery().next();
    }
}
