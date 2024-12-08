package xyz.devcmb.cmr.minigames.teleporters;

/**
 * Interface for teleporters events
 */
public interface TeleportersEvent {
    /**
     * Run the event
     */
    void run();

    /**
     * Get the name of the event
     * @return The name of the event
     */
    String getName();

    /**
     * Get the description of the event
     * @return The description of the event
     */
    String getDescription();
}
