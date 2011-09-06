package com.slash.softAir.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.iCo6.iConomy;
import com.slash.softAir.SoftAir;

public class SoftAirCommand implements CommandExecutor{

	private SoftAir plugin;
	
	public SoftAirCommand(SoftAir softAir) {
		plugin=softAir;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("SoftAir")){ // If the player typed /SoftAir then do the following...
			if(args.length==0){
				/**
				 * Player typed no command arguments after /Softair
				 */
				sender.sendMessage("Write /softair help to see command usage");
				sender.sendMessage("Or write /softair opHelp to see admin commands");
				return true;
			}
			if(args[0].equalsIgnoreCase("ready")){
				plugin.setPlayerReady(sender);
				return true;
			}
			if(args[0].equalsIgnoreCase("help")){
				sender.sendMessage("|-------------SoftAir Help-------------|");
				sender.sendMessage("                                        ");
				sender.sendMessage("Command: /softAir join                  ");
				sender.sendMessage("Join a random team to make them balanced");
				sender.sendMessage("                                        ");
				sender.sendMessage("Command: /softAir leave                 ");
				sender.sendMessage("Leave the game, you'll lose the money   ");
				sender.sendMessage("                                        ");
				sender.sendMessage("Command: /softAir players               ");
				sender.sendMessage("Shows numbers of players inGame         ");
				sender.sendMessage("");
				sender.sendMessage("Command :/softAir ready");
				sender.sendMessage("Set your status to ready to play");
				sender.sendMessage("|--------------------------------------|");
				return true;
			}else
			if(sender.isOp()&&args[0].equalsIgnoreCase("opHelp")){
				sender.sendMessage("|----------SoftAir Admin Help----------|");
				sender.sendMessage("                                        ");
				sender.sendMessage("Command: /softair setSpawn team         ");
				sender.sendMessage("Set the spawnpoint for the team selected");
				sender.sendMessage("The available team are Gold and Diamond ");
				sender.sendMessage("                                        ");
				sender.sendMessage("Command: /softair enable                ");
				sender.sendMessage("Set the softair game enabled            ");
				sender.sendMessage("                                        ");
				sender.sendMessage("Command: /softair disable               ");
				sender.sendMessage("Disable the game                        ");
				sender.sendMessage("");
				sender.sendMessage("|--------------------------------------|");
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
							sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"You're already in the game");
					else
						sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"The game is in progress, you can't join");
				else
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"The game isn't enabled yet");
				return true;
			}else
			if(args[0].equalsIgnoreCase("leave")){
				if(plugin.isPlayerInGame(((Player)sender).getName())){
					plugin.leave(sender);
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"Leaving the game");
				}
				else{
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"You are not in the game");
				}
				return true;
			}else
			if(args[0].equalsIgnoreCase("enable")){
				if(sender.isOp()){
					if(plugin.GAME.isSpawnSet()){
						plugin.enableGame();
						sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"Game succesfully enabled");
						plugin.getServer().broadcastMessage(ChatColor.YELLOW+"[Softair Enabled]"+ChatColor.GREEN+"Type /softair join to start the game");
						//plugin.getServer().broadcastMessage("[Softair] The entry fee is "+iConomy.format(plugin.economy.getFee()));
					}
					else
						sender.sendMessage("Set the spawn point first");
					return true;
				}
			}else
			if(args[0].equalsIgnoreCase("disable")){
				if(sender.isOp()){
					plugin.disableGame();
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"Game succesfully disabled");
					plugin.getServer().broadcastMessage(ChatColor.RED+"[Softair Disabled]");
					return true;
				}
			}else	
			if(args[0].equalsIgnoreCase("initialize")){
				if(sender.isOp()){
					try {
						plugin.initialize();
						sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"Initialization complete");
					} catch (Exception e) {
						sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GREEN+"Failed to initialize");
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
				//plugin.economy.setFee(Integer.parseInt(args[1]));
				//plugin.getServer().broadcastMessage("[Softair]Fee is changed to "+iConomy.format(Integer.parseInt(args[1])));
				return true;
			}
			else
			if(args[0].equalsIgnoreCase("getFee")){
				//sender.sendMessage(iConomy.format(plugin.economy.getFee()));
				return true;
			}
			else
			if(args[0].equalsIgnoreCase("status")){
				sender.sendMessage("enalbed ="+plugin.isGameEnabled());
				sender.sendMessage("inProgress="+plugin.isGameInProgress());
				sender.sendMessage("waiting ="+plugin.isGameWaiting());
				return true;
			}
		}
		return false; 
	}
	
}
