package com.slash.softAir;

import java.util.ArrayList;

import org.bukkit.Location;

public class SoftAirTeam {

	private ArrayList<String> players;
	private Location spawn;
	private String name;
	private Boolean spawnSet = false;
	
	public SoftAirTeam(String name, SoftAir plugin){
		players = new ArrayList<String>();
		this.name = name;
	}
	
	public ArrayList<String> getPlayers(){
		return (ArrayList<String>) players.clone();
	}
	
	public int getSize(){
		return players.size();
	}
	
	public boolean hasPlayer(String p){
		return players.contains(p);
	}
	
	public void addPlayer(String p){
		players.add(p);
	}
	
	public void removePlayer(String p){
		players.remove(p);
	}
	
	public boolean isEmpty(){
		return players.size()==0;
	}
	
	public Location getCords(){
		return spawn;
	}
	
	public String getCordsString(){
		String cord ="";
		cord+=spawn.getWorld().getName()+"|"+spawn.getX()+"|"+spawn.getY()+"|"+spawn.getZ()+"|"+spawn.getPitch()+"|"+spawn.getYaw();
		return cord;
	}
	
	public void setCords(Location loc){
		spawn=loc;
		setSpawnSet(true);
	}
	
	public String getName(){
		return name;
	}
	
	public boolean equals(SoftAirTeam t){	
		if(this.name.equalsIgnoreCase(t.getName()))
			return true;
		return false;	
	}

	public Boolean getSpawnSet() {
		return spawnSet;
	}

	public void setSpawnSet(Boolean spawnSet) {
		this.spawnSet = spawnSet;
	}
	
	public void clear(){
		players.clear();
	}

}
