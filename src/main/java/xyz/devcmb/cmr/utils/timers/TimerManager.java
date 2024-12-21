package xyz.devcmb.cmr.utils.timers;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.HashMap;
import java.util.Map;

public class TimerManager {
    private static final Map<String, Timer> timers = new HashMap<>();
    public static boolean paused = false;

    public static void registerTimer(String name, Timer timer){
        timers.put(name, timer);
    }

    public static void registerAllTimers(){
        registerTimer("intermission", new IntermissionTimer());
    }

    public static Timer runTimer(String name) {
        Timer timer = timers.get(name);

        if (timer == null) {
            CmbMinigamesRandom.LOGGER.warning("Failed to find a timer for " + name);
            return null;
        }

        timer.run();
        return timer;
    }

    public static void pauseTimer(){
        paused = !paused;
    }
}
