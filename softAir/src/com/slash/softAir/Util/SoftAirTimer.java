package com.slash.softAir.Util;

import org.bukkit.ChatColor;

import com.slash.softAir.SoftAir;
import com.slash.softAir.SoftAirPlayers;

public class SoftAirTimer implements Runnable{

    public static SoftAir plugin;
    public SoftAirPlayers players;


    public SoftAirTimer(SoftAir instance,SoftAirPlayers players) {
        plugin = instance;
        this.players = players;
    }
 
    @Override
    public void run() {
    	if(plugin.x>0){
    		players.sendMessageToAll(ChatColor.GREEN+"Ready in "+plugin.x);
    		plugin.x--;
    	}
    	else{
    		plugin.setGameWaiting(false);
    		plugin.setGameInProgress(true);
    		players.sendMessageToAll(ChatColor.RED+"Start the fight");
    		plugin.x=5;
    		plugin.getServer().getScheduler().cancelTask(plugin.timerID);
    	}
    }
 
}
