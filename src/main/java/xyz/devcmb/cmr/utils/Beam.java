package xyz.devcmb.cmr.utils;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.Objects;
import java.util.Random;

public class Beam {
    public Location start;
    public Location end;
    public ChatColor color;

    private BukkitRunnable updateTime;
    private Location particleLocation;

    public Beam(Location start, Location end){
        this.start = start;
        this.end = end;
        Start();
    }

    private void Start() {
        World world = start.getWorld();

        assert world != null;

        particleLocation = start.clone();

        Vector dir = end.toVector().subtract(start.toVector()).normalize();
        Vector vecOffset = dir.clone().multiply(0.5);

        updateTime = new BukkitRunnable(){
            public void run(){
                particleLocation.add(vecOffset);

                if (particleLocation.distance(end) * 2 >= start.distance(end)) {
                    particleLocation = start.clone();
                }

                world.spawnParticle(Particle.DUST, particleLocation, 10, new Particle.DustOptions(Color.LIME, 2));
            }
        };

        updateTime.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 1);
    }

    public void Remove() {
        updateTime.cancel();
        updateTime = null;
    }
}
