package xyz.devcmb.cmr.utils.timers;

import org.bukkit.scheduler.BukkitRunnable;

public interface Timer {
    Integer getTime();
    Integer getClockTickTime();
    TimerSuper.TimerAction getAction();
    BukkitRunnable run();
    void end();
}
