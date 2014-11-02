package net.portalblock.br;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by portalBlock on 11/2/2014.
 */
public class Replacer implements Listener {

    private List<Location> locations = new ArrayList<Location>();
    private final int radius;
    private boolean done = false;
    private Material from, to;

    public Replacer(Material to, Material from, int radius) {
        this.from = from;
        this.to = to;
        this.radius = radius;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(done) return;
        Location l = e.getPlayer().getLocation();
        for (int x = (radius*-1); x <= radius; x++){
            for (int z = (radius*-1); z <= radius; z++){
                for (int y = (radius*-1); y <= radius; y++){
                    Block b = new Location(e.getPlayer().getWorld(), l.getX() + x, l.getY() + y, l.getZ() + z).getBlock();
                    if(b != null){
                        if(b.getType() == from){
                            b.setType(to);
                            locations.add(b.getLocation());
                        }
                    }
                }
            }
        }
    }

    public Entity[] getNearbyEntities(Location l, int radius) {
        HashSet<Entity> radiusEntities = new HashSet< Entity >();

        for (int chX = 0 - radius; chX <= radius; chX++) {
            for (int chZ = 0 - radius; chZ <= radius; chZ++) {
                int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
                for (Entity e: new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
                    if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
                        radiusEntities.add(e);
                }
            }
        }

        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }

    public void onDisable(){
        done = true;
        reset(false);
    }

    public Runnable getReseter(){
        return new Runnable() {
            @Override
            public void run() {
                reset(true);
            }
        };
    }

    private void reset(boolean ignorePlayers){
        Iterator<Location> it = locations.iterator();
        while(it.hasNext()){
            Location l = it.next();
            if(l.getBlock().getType() != to) continue;
            boolean change = true;
            for(Entity entity : getNearbyEntities(l, radius+1)){
                if(entity.getType() == EntityType.PLAYER && !ignorePlayers){
                    change = false;
                }
            }
            if(change){
                l.getBlock().setType(from);
            }
        }
    }
}
