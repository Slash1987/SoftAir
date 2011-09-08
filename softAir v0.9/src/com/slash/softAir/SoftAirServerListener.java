package com.slash.softAir;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import com.iConomy.iConomy;

public class SoftAirServerListener extends ServerListener {

	private SoftAir plugin;
	
	public SoftAirServerListener(SoftAir instance){
		plugin=instance;
	}
	
	public void onPluginDisable(PluginDisableEvent event) {
        if (plugin.iCo != null) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                plugin.iCo = null;
                System.out.println("[SoftAir]Un-hooked from iConomy.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (plugin.iCo == null) {
            Plugin iCo = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (iCo != null) {
                if (iCo.isEnabled() && iCo.getClass().getName().equals("com.iConomy.iConomy")) {
                    plugin.iCo = (iConomy)iCo;
                    System.out.println("[SoftAir]Hooked into iConomy.");
                }
            }
        }
    }
}