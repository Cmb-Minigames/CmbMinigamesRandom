package xyz.devcmb.cmr.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.interfaces.ActionBar;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.*;

/**
 * A utility class for interacting with the MySQL database
 */
public class Database {
    private static Connection connection;

    /**
     * Connect to the MySQL database
     */
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

    /**
     * Check if a user exists in the database
     * @param player The player to check
     * @return Whether the user exists
     */
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

    /**
     * Get a user from the database
     * @param player The player to get
     * @return The user
     */
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

    /**
     * Get the stars of a user
     * @param player The player to get the stars of
     * @return The stars of the user
     */
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

    /**
     * Set the stars of a user
     * @param player The player to set the stars of
     * @param stars The stars to set
     */
    public static void setUserStars(Player player, int stars) {
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE Users SET stars = ? WHERE uuid = ?");
            statement.setInt(1, stars);
            statement.setBytes(2, uuidToBytes(player.getUniqueId()));
            statement.executeUpdate();

            ActionBar.userStars.put(player, stars);
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to update user stars by UUID: " + e.getMessage());
        }
    }

    /**
     * Get the cosmetics of a user
     * @param player The player to get the cosmetics of
     * @return The cosmetics of the user
     */
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

    /**
     * Get all cosmetics
     * @return All cosmetics
     */
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

    /**
     * Give a cosmetic to a user
     * @param player The player to give the cosmetic to
     * @param name The name of the cosmetic
     */
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


    /**
     * Get all crates
     * @return All crates
     */
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

    /**
     * Get the crates a user has
     * @param player The player to get the crates of
     * @return The player's crates
     */
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

    /**
     * Give a crate to a user
     * @param player The player to give the crate to
     * @param name The name of the crate
     */
    public static void giveCrate(Player player, String name){
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE Users SET crates = CONCAT(coalesce(crates, ''), ?, '|') WHERE uuid = ?");
            statement.setString(1, name);
            statement.setBytes(2, uuidToBytes(player.getUniqueId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to give user crate by UUID: " + e.getMessage());
        }
    }

    /**
     * Remove a crate from a user
     * @param player The player to remove the crate from
     * @param name The name of the crate
     */
    public static void removeCrate(Player player, String name){
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE Users SET crates = " +
                            "CASE " +
                            "WHEN crates LIKE ? THEN CONCAT(SUBSTRING_INDEX(crates, ?, 1), SUBSTRING(crates FROM LENGTH(SUBSTRING_INDEX(crates, ?, 1)) + LENGTH(?) + 1)) " +
                            "ELSE crates " +
                            "END " +
                            "WHERE uuid = ?"
            );
            statement.setString(1, "%" + name + "|%");
            statement.setString(2, name + "|");
            statement.setString(3, name + "|");
            statement.setString(4, name + "|");
            statement.setBytes(5, uuidToBytes(player.getUniqueId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to remove user crate by UUID: " + e.getMessage());
        }
    }

    /**
     * Check if a cosmetic is equipped by a user
     * @param player The player to check
     * @param cosmeticName The name of the cosmetic to check
     * @return Whether the cosmetic is equipped
     */
    public static boolean isCosmeticEquipped(Player player, String cosmeticName) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT equipped FROM Users WHERE uuid = ?"
            );
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return cosmeticName.equals(resultSet.getString("equipped"));
            }
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to check if cosmetic is equipped by UUID: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get the equipped cosmetic of a user
     * @param player The player to check
     * @return The cosmetic they have equipped
     */
    public static String getEquipped(Player player){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT equipped FROM Users WHERE uuid = ?");
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("equipped");
            }
        } catch (SQLException e){
            CmbMinigamesRandom.LOGGER.severe("Failed to get equipped cosmetic by UUID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Equip a cosmetic for a user
     * @param player The player to equip the cosmetic for
     * @param cosmetic The name of the cosmetic to equip
     */
    public static void equipCosmetic(Player player, String cosmetic){
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE Users SET equipped = ? WHERE uuid = ?");
            statement.setString(1, cosmetic);
            statement.setBytes(2, uuidToBytes(player.getUniqueId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to equip cosmetic by UUID: " + e.getMessage());
        }
    }

    /**
     * Get a rarity sets
     * @param id The id of the rarity set
     * @return The rarity set
     */
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

    /**
     * Give a user stars
     * @param player The player to give the stars to
     * @param stars The amount of stars to give
     */
    public static void addUserStars(Player player, int stars){
        try {
            if (!userExists(player)) {
                createUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE Users SET stars = stars + ? WHERE uuid = ?");
            statement.setInt(1, stars);
            statement.setBytes(2, uuidToBytes(player.getUniqueId()));
            statement.executeUpdate();

            ActionBar.userStars.put(player, ActionBar.userStars.get(player) + stars);
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to add user stars by UUID: " + e.getMessage());
        }
    }

    /**
     * Register a new user
     * @param player The player to create the uer for
     * @return The created user
     */
    public static ResultSet createUser(Player player){
        try {
            if(userExists(player)){
                return getUser(player);
            }
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (uuid, stars, cosmetics, crates) VALUES (?, ?, ?, ?)");
            statement.setBytes(1, uuidToBytes(player.getUniqueId()));
            statement.setInt(2, 0);
            statement.setString(3, "");
            statement.setString(4, "");
            statement.executeUpdate();
            return getUser(player);
        } catch (SQLException e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to create user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Disconnect from the MySQL database
     */
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

    /**
     * Convert a UUID to a byte array
     * @param uuid The UUID to convert
     * @return The byte array
     */
    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }
}
