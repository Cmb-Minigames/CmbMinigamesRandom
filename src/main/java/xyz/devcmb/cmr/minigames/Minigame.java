package xyz.devcmb.cmr.minigames;

public interface Minigame {
    void start();
    void stop();
    void playerJoin();
    void playerLeave();

    String getName();
    String getDescription();
}
