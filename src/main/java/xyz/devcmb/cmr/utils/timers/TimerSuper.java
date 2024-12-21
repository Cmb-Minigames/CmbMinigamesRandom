package xyz.devcmb.cmr.utils.timers;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;

public abstract class TimerSuper {
    private Integer time;
    private final Integer initialTime;
    private final Integer clockTickTime;
    private final TimerAction action;
    private BukkitRunnable runnable;


    @FunctionalInterface
    public interface TimerAction {
        void execute(Boolean early);
    }

    public TimerSuper(Integer time, Integer clockTickTime, TimerAction action){
        this.time = time;
        this.initialTime = time;
        this.clockTickTime = clockTickTime;
        this.action = action;
    }

    public Integer getTime(){
        return time;
    }
    public Integer getClockTickTime(){
        return clockTickTime;
    }
    public TimerAction getAction() {
        return action;
    }

    public BukkitRunnable run(){
        runnable = new BukkitRunnable(){
            @Override
            public void run(){
                if(TimerManager.paused) return;

                if(time <= 0){
                    cancel();
                    finishTimer();
                    return;
                }

                time--;
            }
        };
        runnable.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, clockTickTime);

        return runnable;
    }

    private void finishTimer(){
        action.execute(false);
        time = initialTime;
        TimerManager.activeTimers.remove(this);
    }

    public void end(){
        if(runnable != null){
            runnable.cancel();
            action.execute(true);
            time = initialTime;
        }
    }
}


