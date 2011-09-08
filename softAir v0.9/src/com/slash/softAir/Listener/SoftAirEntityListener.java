package com.slash.softAir.Listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

import com.slash.softAir.SoftAir;

public class SoftAirEntityListener extends EntityListener{

	private SoftAir plugin;
	
	public SoftAirEntityListener(SoftAir plugin){
		this.plugin = plugin;
	}

	public void onEntityDamage(EntityDamageEvent e) {
		if (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) e).getCause() == DamageCause.PROJECTILE) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if(event.getDamager() instanceof Arrow){
            	Arrow arrow= (Arrow) event.getDamager();
            	if(arrow.getShooter() instanceof Player && event.getEntity() instanceof Player){
            		Player shooter=(Player)arrow.getShooter();
            		Player shoot =(Player) event.getEntity();
            		if(plugin.isPlayerInGame(shooter.getName())&&plugin.isPlayerInGame(shoot.getName())){
            			plugin.hit(shoot, shooter);
            			event.setCancelled(true);
            		}
            	}
            }
        }
    }

}