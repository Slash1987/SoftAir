package com.slash.softAir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.slash.softAir.Util.SoftAirInventory;

public class SoftAirPlayers {

	private HashMap<String,Location> pLocation = new HashMap<String,Location>();			//Coordinates where to teleport Players after a play
	private HashMap<String, String> pInventory = new HashMap<String,String>();			//Inventory to restore to playing Players
	private HashMap<String, String> pArmor = new HashMap<String,String>();				//Armor to restore to playing Players
	private HashMap<String, String> crashedInventory = new HashMap<String,String>();		//Inventory to restore to crashed Players
	private HashMap<String, String> crashedArmor = new HashMap<String,String>();			//Armor to restore to crashed Players
	private HashMap<String,Location> crashedLocation  = new HashMap<String,Location>();
	private HashMap<String, Boolean> status = new HashMap<String,Boolean>();				//Players ready to play
	private HashMap<String, String> playersPlaying = new HashMap<String,String>();			//Players not yet eliminated + Team
	private ArrayList<Player> allPlayersInGame = new ArrayList<Player>();
	private ArrayList<String> playerToRestore = new ArrayList<String>();
	public SoftAirPlayers (){
	}
	
	
	//Save the inventory of the player that joined the game in the HASHMAP pInventory e pArmor
	public void saveInventory(Player player){
		pInventory.put(player.getName(),SoftAirInventory.InventoryToString(player.getInventory().getContents().clone()));
		pArmor.put(player.getName(),SoftAirInventory.ArmorToString(player.getInventory().getArmorContents().clone()));
		player.getInventory().clear();
	}
	//Retrive the inventory of the player from pInventory
	public String getInventory(String player){
		return pInventory.get(player);
	}
	//Retrive the armor of the player from pArmor
	public String getArmor(String player){
		return pArmor.get(player);
	}
	
	
	//Restore the inventory of a player hitted by the arrow
	public void restoreInventory(Player player){
		player.getInventory().clear();
		SoftAirInventory.StringToInventory(pInventory.get(player.getName()),player);
		SoftAirInventory.StringToArmor(pArmor.get(player.getName()),player);
		pInventory.remove(player.getName());
		pArmor.remove(player.getName());
	}
	
	//Add a player to the game, save inventory and location
	public void addPlayer(Player player,String team){
		playersPlaying.put(player.getName(),team);
		allPlayersInGame.add(player);
		status.put(player.getName(), false);
		pLocation.put(player.getName(), player.getLocation());
		saveInventory(player);
		setTeamItems(player,team);

	}
	
	public void setTeamItems(Player player,String team){
		if(team.equalsIgnoreCase("gold")){
			SoftAirInventory.getGoldArmor(player);
		}
		else{
			SoftAirInventory.getDiamondArmor(player);
		}
		SoftAirInventory.setSoftAirItem(player);
	}
	
	//Remove a player from the game if hit by arrow or if used the command leave
	public void removePlayer(Player player){
		String name = player.getName();
		restoreInventory(player);
		player.teleport(pLocation.get(name));
		pLocation.remove(name);
		playersPlaying.remove(name);
		status.remove(name);
	}
	
	
	//Send a message to all the players that have joined the game
	public void sendMessageToAll(String message){
		for(Player p : allPlayersInGame)
			p.sendMessage(message);
	}
	
	
	//Remove a player from the game if he lose connection
	public void removePlayerOnQuit(String player){
		playerToRestore.add(player);
		crashedInventory.put(player, pInventory.get(player));
		crashedArmor.put(player, pArmor.get(player));
		crashedLocation.put(player, pLocation.get(player));
		pInventory.remove(player);
		pArmor.remove(player);
		pLocation.remove(player);
		playersPlaying.remove(player);
		allPlayersInGame.remove(player);
		status.remove(player);
	}

	
	//Retrieve players in game after a crash or if they logged out during a game and add them to the restore list
	public void addToRestoreOnCrash(ArrayList<String> players, HashMap<String, String> inventory, HashMap<String, String> armor, HashMap<String, Location> location){
		playerToRestore=players;
		crashedInventory=inventory;
		crashedArmor=armor;
		crashedLocation=location;
	}
	
	
	//Restore a player  on Login if in the restore list
	public void restore(Player player){
		String name=player.getName();
		restoreInventoryOnLogin(player);
		player.teleport(crashedLocation.get(name));
	}
	
	public void restoreInventoryOnLogin(Player player){
		player.getInventory().clear();
		SoftAirInventory.StringToArmor(crashedArmor.get(player.getName()),player);
		SoftAirInventory.StringToInventory(crashedInventory.get(player.getName()),player);
	    crashedInventory.remove(player);
	    crashedArmor.remove(player);		
	}
	
	public String getRestoreArmor(String player){
		return crashedArmor.get(player);
	}
	public String getRestoreInventory(String player){
		return crashedInventory.get(player);
	}

	
	//Return the name of the team the player is in
	public String getTeam(String player){
		return playersPlaying.get(player);
	}
	
	
	//Set ready status
	public void setReady(String player){
		status.put(player, true);
	}

	
	//Control if all players are ready
	public boolean isAllReady(){
		Iterator<Boolean> it = status.values().iterator();
		Boolean ready = true;
		while(it.hasNext()){
			ready=it.next();
			if(!ready)
				return false;
		}
		return true;
	}


	//Control players inGame
	public int[] getPlayersInGame(){
		int[] players = {0,0,0};
		Iterator<String> it = playersPlaying.values().iterator();
		while(it.hasNext()){
			players[0]++;
			if(it.next().equalsIgnoreCase("GOLD")){
				players[1]++;
			}
			else
				players[2]++;
		}
		return players;
	}


	public boolean isPlayerInGame(String player){
		return playersPlaying.containsKey(player);
	}


	public boolean isPlayerToRestore(String name) {
		return playerToRestore.contains(name);
	}

	public void clear() {
		playersPlaying.clear();
		pInventory.clear();
		pArmor.clear();
		pLocation.clear();
		status.clear();
		allPlayersInGame.clear();
	}
}
