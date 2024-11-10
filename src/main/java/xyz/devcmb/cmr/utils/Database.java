package xyz.devcmb.cmr.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.UUID;

public class Database {
    private static Connection connection;

    public static void connect() {
        FileConfiguration config = CmbMinigamesRandom.getPlugin().getConfig();
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String username = config.getString("database.username");
        String password = config.getString("database.password");
        String database = config.getString("database.database");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";

        try {
            connection = DriverManager.getConnection(url, username, password);
            CmbMinigamesRandom.LOGGER.info("Successfully connected to the MySQL database.");
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to connect to the MySQL database: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static boolean userExists(Player player) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM Users WHERE uuid = ?");
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to check if user exists by UUID: " + e.getMessage());
            return false;
        }
    }

    public static ResultSet getUser(Player player){
        try {
            if(!userExists(player)){
                return createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE uuid = ?");
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            return statement.executeQuery();
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to query user by UUID: " + e.getMessage());
            return null;
        }
    }

    public static int getUserStars(Player player) {
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("SELECT stars FROM Users WHERE uuid = ?");
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("stars");
            }
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to query user stars by UUID: " + e.getMessage());
        }
        return 0;
    }

    public static ResultSet createUser(Player player){
        try {
            if(userExists(player)){
                return getUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (uuid, stars) VALUES (?, ?)");
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            statement.setInt(2, 0);
            statement.executeUpdate();
            return getUser(player);
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to create user: " + e.getMessage());
        }

        return null;
    }

    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                CmbMinigamesRandom.LOGGER.info("Disconnected from the MySQL database.");
            } catch (SQLException e) {
                CmbMinigamesRandom.LOGGER.severe("Failed to disconnect from the MySQL database: " + e.getMessage());
            }
        }
    }

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }
}
