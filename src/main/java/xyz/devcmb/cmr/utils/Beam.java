package xyz.devcmb.cmr.utils;

import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.devcmb.cmr.CmbMinigamesRandom;

/**
 * A utility class to create a beam of particles
 */
public class Beam {
    private final Location start;
    private final Location end;

    private BukkitRunnable updateTime;
    private Location particleLocation;

    /**
     * Create a new beam
     * @param start The start location
     * @param end The end location
     */
    public Beam(Location start, Location end){
        this.start = start;
        this.end = end;
        Start();
    }

    /**
     * Start the beam
     */
    private void Start() {
        World world = this.start.getWorld();

        assert world != null;

        particleLocation = this.start.clone();

        Vector dir = this.end.toVector().subtract(this.start.toVector()).normalize();
        Vector vecOffset = dir.clone().multiply(0.5);

        Location theEnd = this.end;

        this.updateTime = new BukkitRunnable(){
            @Override
            public void run(){
                particleLocation.add(vecOffset);

                if (particleLocation.distance(theEnd) <= vecOffset.length()) {
                    particleLocation = start.clone();
                }

                world.spawnParticle(Particle.DUST, particleLocation.clone().add(0.5, 0.5, 0.5), 10, new Particle.DustOptions(Color.LIME, 2));
            }
        };

        this.updateTime.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 1);
    }

    /**
     * Remove the beam
     */
    public void Remove() {
        this.updateTime.cancel();
        this.updateTime = null;
    }
}
