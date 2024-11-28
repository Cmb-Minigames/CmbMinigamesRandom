package xyz.devcmb.cmr.minigames;

public enum MinigameFlag {
    /**
     * Disables fall damage for all players
     */
    DISABLE_FALL_DAMAGE,
    /**
     * Disables PVP for all players
     */
    PVP_DISABLED,
    /**
     * Gives all placed blocks back to the player that placed them
     */
    UNLIMITED_BLOCKS,
    /**
     * Disables the ability to break blocks for all players
     */
    CANNOT_BREAK_BLOCKS,
    /**
     * Disables the ability to place blocks for all players
     */
    CANNOT_PLACE_BLOCKS,
    /**
     * Disables the ability to place items in your off hand
     */
    DISABLE_OFF_HAND,
    /**
     * Disables the ability to drop blocks
     */
    DISABLE_BLOCK_DROPS,
    /**
     * Doesn't drop any items when you die
     */
    DISABLE_PLAYER_DEATH_DROP,
    /**
     * Displays the person who killed you in the death message (don't include for movement games or ones where the killer should be kept secret)
     */
    DISPLAY_KILLER_IN_DEATH_MESSAGE,
    /**
     * Give all fireworks back 3 seconds after they are used
     */
    DO_NOT_CONSUME_FIREWORKS,
    /**
     * Makes it so you cannot have duplicates of armor and weapons in your inventory
     */
    NO_REPEATED_TOOLS,
    /**
     * Makes it so you cannot trample farmland
     */
    CANNOT_TRAMPLE_FARMLAND,
    /**
     * Use the custom respawn system to prevent the music from stopping
     */
    USE_CUSTOM_RESPAWN
}
