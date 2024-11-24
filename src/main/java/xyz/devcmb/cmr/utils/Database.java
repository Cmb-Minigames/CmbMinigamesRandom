package xyz.devcmb.cmr.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.*;

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

    public static void setUserStars(Player player, int stars) {
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE Users SET stars = ? WHERE uuid = ?");
            statement.setInt(1, stars);
            statement.setBytes(2, uuidToBytes(player.getUniqueId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to update user stars by UUID: " + e.getMessage());
        }
    }

    public static List<String> getUserCosmetics(Player player){
        try {
            if (!userExists(player)) {
                createUser(player);
            }

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE uuid = ?");
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String cosmetics = resultSet.getString("cosmetics");
                if (cosmetics == null || cosmetics.isEmpty()) {
                    return List.of();
                }
                return Arrays.asList(cosmetics.split("\\|"));
            }
        } catch (SQLException e){
            CmbMinigamesRandom.LOGGER.severe("Failed to get user cosmetics by UUID: " + e.getMessage());
        }

        return List.of();
    }

    public static Map<String, Map<String, Object>> getAllCosmetics(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Cosmetics");
            ResultSet resultSet = statement.executeQuery();
            Map<String, Map<String, Object>> resultMap = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                Map<String, Object> fieldMap = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    fieldMap.put(columnName, value);
                }

                resultMap.put(name, fieldMap);
            }

            return resultMap;
        } catch (SQLException e){
            CmbMinigamesRandom.LOGGER.severe("Failed to get all cosmetics: " + e.getMessage());
        }

        return new HashMap<>();
    }

    public static Map<String, Object> getCosmetic(String name){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Cosmetics WHERE name = ?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            Map<String, Object> resultMap = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    resultMap.put(columnName, value);
                }
            }

            return resultMap;
        } catch (SQLException e){
            CmbMinigamesRandom.LOGGER.severe("Failed to get cosmetic: " + e.getMessage());
        }
        return null;
    }

    public static void giveCosmetic(Player player, String name){
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE Users SET cosmetics = CONCAT(coalesce(cosmetics, ''), ?, '|') WHERE uuid = ?");
            statement.setString(1, name);
            statement.setBytes(2, uuidToBytes(player.getUniqueId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to give user cosmetic by UUID: " + e.getMessage());
        }
    }

    public static Map<String, Map<String, Object>> getAllCrates(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Crates");
            ResultSet resultSet = statement.executeQuery();
            Map<String, Map<String, Object>> resultMap = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                Map<String, Object> fieldMap = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    fieldMap.put(columnName, value);
                }

                resultMap.put(name, fieldMap);
            }

            return resultMap;
        } catch (SQLException e){
            CmbMinigamesRandom.LOGGER.severe("Failed to get all crates: " + e.getMessage());
        }

        return new HashMap<>();
    }

    public static List<String> getUserCrates(Player player){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE uuid = ?");
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String crates = resultSet.getString("crates");
                if (crates == null || crates.isEmpty()) {
                    return List.of();
                }
                return Arrays.asList(crates.split("\\|"));
            }
        } catch (SQLException e){
            CmbMinigamesRandom.LOGGER.severe("Failed to get user crates by UUID: " + e.getMessage());
        }
        return List.of();
    }

    public static Map<String, Object> getCrate(String name){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Crates WHERE name = ?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            Map<String, Object> resultMap = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    resultMap.put(columnName, value);
                }
            }

            return resultMap;
        } catch (SQLException e){
            CmbMinigamesRandom.LOGGER.severe("Failed to get crate: " + e.getMessage());
        }
        return null;
    }

    public static Map<String, Number> getRaritySet(Integer id){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM RaritySets WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            Map<String, Number> resultMap = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    resultMap.put(columnName, (Number) value);
                }
            }

            return resultMap;
        } catch (SQLException e){
            CmbMinigamesRandom.LOGGER.severe("Failed to get rarity set: " + e.getMessage());
        }
        return null;
    }

    public static void addUserStars(Player player, int stars){
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE Users SET stars = stars + ? WHERE uuid = ?");
            statement.setInt(1, stars);
            statement.setBytes(2, uuidToBytes(player.getUniqueId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to add user stars by UUID: " + e.getMessage());
        }
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
