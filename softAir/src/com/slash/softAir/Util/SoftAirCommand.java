package com.slash.softAir.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.slash.softAir.SoftAir;
import com.iConomy.iConomy;

public class SoftAirCommand implements CommandExecutor{

	private SoftAir plugin;
	
	public SoftAirCommand(SoftAir softAir) {
		plugin=softAir;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("SoftAir")){ // If the player typed /SoftAir then do the following...
			if(args.length==0){
				return false;
			}
			if(args[0].equalsIgnoreCase("ready")){
				plugin.setPlayerReady(sender);
				return true;
			}else
			if(args[0].equalsIgnoreCase("setSpawn")){
				plugin.setSpawn(sender,args[1]);
				return true;
			}else
			if(args[0].equalsIgnoreCase("join")){
				if(plugin.isGameEnabled())
					if(!plugin.isGameInProgress())
						if(!plugin.isPlayerInGame(((Player)sender).getName()))
							plugin.join(sender);
						else
							sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+" You're already in the game");
					else
						sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+" The game is in progress, you can't join");
				else
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+" The game isn't enabled yet");
				return true;
			}else
			if(args[0].equalsIgnoreCase("leave")){
				if(plugin.isPlayerInGame(((Player)sender).getName())){
					plugin.leave(sender);
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+" Leaving the game");
				}
				else{
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+" You are not in the game");
				}
				return true;
			}else
			if(args[0].equalsIgnoreCase("enable")){
				if(sender.isOp()){
					if(plugin.GAME.isSpawnSet()){
						plugin.enableGame();
						sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+" Game succesfully enabled");
						plugin.getServer().broadcastMessage(ChatColor.YELLOW+"[Softair Enabled]"+ChatColor.GREEN+" Type /softair join to start the game");
					}
					else
						sender.sendMessage("Set the spawn point first");
					return true;
				}
			}else
			if(args[0].equalsIgnoreCase("disable")){
				if(sender.isOp()){
					plugin.disableGame();
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+" Game succesfully disabled");
					plugin.getServer().broadcastMessage(ChatColor.RED+"[Softair Disabled]");
					return true;
				}
			}else	
			if(args[0].equalsIgnoreCase("initialize")){
				if(sender.isOp()){
					try {
						plugin.initialize();
						sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+" Initialization complete");
					} catch (Exception e) {
						sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+" Failed to initialize");
						e.printStackTrace();
					}
					return true;
				}
			}
			else
			if(args[0].equalsIgnoreCase("players")){
				plugin.playersNumber(sender);
				return true;
			}
			else
			if(args[0].equalsIgnoreCase("setFee")&&sender.isOp()){
					plugin.setGameFee(SoftAirConverter.stringToDouble(args[1]));
					plugin.getServer().broadcastMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+" Fee is set to "+iConomy.format(plugin.GAME.getFee()));
					return true;
			}
			else
			if(args[0].equalsIgnoreCase("getFee")){
				sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+" The fee is "+iConomy.format(plugin.GAME.getFee()));
				return true;
			}
			else
			if(args[0].equalsIgnoreCase("status")){
				sender.sendMessage("Enalbed     ="+plugin.isGameEnabled());
				sender.sendMessage("In Progress ="+plugin.isGameInProgress());
				sender.sendMessage("Waiting     ="+plugin.isGameWaiting());
				return true;
			}
		}
		return false; 
	}
	
}
