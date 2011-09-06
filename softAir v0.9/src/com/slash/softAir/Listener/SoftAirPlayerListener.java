package com.slash.softAir.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.slash.softAir.SoftAir;

public class SoftAirPlayerListener extends PlayerListener{
	
	SoftAir plugin;
	
	public SoftAirPlayerListener(SoftAir plugin){
		this.plugin=plugin;
	}
	
	public void onPlayerMove(PlayerMoveEvent event){
		if(plugin.isPlayerInGame(event.getPlayer().getName())&&plugin.isGameWaiting()){
			plugin.onPlayerMove(event.getPlayer());
		}
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){
		plugin.onPlayerQuit(event.getPlayer().getName());
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		plugin.onPlayerLogin(event.getPlayer());
	}
}
