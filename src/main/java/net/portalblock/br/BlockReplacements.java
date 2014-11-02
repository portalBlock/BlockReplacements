package net.portalblock.br;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by portalBlock on 11/2/2014.
 */
public class BlockReplacements extends JavaPlugin {

    private List<Replacer> replacers = new ArrayList<Replacer>();

    @Override
    public void onEnable(){
        saveResource("config.yml", false);
        List<String> things = getConfig().getStringList("replace");
        for(String s : things) {
            String[] vals = s.split(":");
            Replacer replacer = new Replacer(
                    Material.valueOf(vals[1].toUpperCase().trim()),
                    Material.valueOf(vals[0].toUpperCase().trim()),
                    Integer.parseInt(vals[2].trim()));
            getServer().getPluginManager().registerEvents(replacer, this);
            getServer().getScheduler().scheduleSyncRepeatingTask(this, replacer.getReseter(), 10L, 10L);
            replacers.add(replacer);
        }
    }

    @Override
    public void onDisable() {
        for(Replacer replacer : replacers){
            replacer.onDisable();
        }
    }
}
