package xyz.devcmb.cmr.utils.timers;

import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TimerManager {
    private static final Map<String, Timer> timers = new HashMap<>();
    public static boolean paused = false;
    public static final Set<Timer> activeTimers = ConcurrentHashMap.newKeySet();

    public static void registerTimer(String name, Timer timer){
        timers.put(name, timer);
    }

    public static void registerAllTimers(){
        registerTimer("intermission", new IntermissionTimer());
        registerTimer("ctf", new CTFTimer());
        registerTimer("kaboomers", new KaboomersTimer());
        registerTimer("brawl", new BrawlTimer());
    }

    public static Timer runTimer(String name) {
        Timer timer = timers.get(name);

        if (timer == null) {
            CmbMinigamesRandom.LOGGER.warning("Failed to find a timer for " + name);
            return null;
        }

        activeTimers.add(timer);

        timer.run();
        return timer;
    }

    public static void endActiveTimers(){
        activeTimers.forEach(Timer::end);
        activeTimers.clear();
    }
}
