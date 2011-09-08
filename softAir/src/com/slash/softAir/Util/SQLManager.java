package com.slash.softAir.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;

import com.slash.softAir.SoftAir;

public class SQLManager {
	
	private Connection conn;
	private SoftAir plugin;

	public SQLManager(SoftAir plugin){
		this.plugin = plugin;
	}
	
	public void connect() throws Exception{
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:SoftAir.db");
		try{
			prepareDB();
		}catch(Exception e){
 
		}
	}
	
	public void disconnect() throws Exception{
		conn.close();
	}
	
	public void prepareDB() throws Exception{
		Statement stat = conn.createStatement();
		stat.executeUpdate("create table if not exists TEAM (name CHAR(7) PRIMARY KEY,world VARCHAR(20),x DOUBLE,y DOUBLE,z DOUBLE)");
		stat.executeUpdate("create table if not exists PLAYER(name VARCHAR(30) PRIMARY KEY,inventory VARCHAR(1000),armor VARCHAR(1000))");
		stat.executeUpdate("create table if not exists GAME(name varchar(30) PRIMARY KEY,enabled integer, waiting integer, inprogress integer,fee integer)");
		stat.executeUpdate("create table if not exists LOCATION(name VARCHAR(30) PRIMARY KEY,world VARCHAR(20),x DOUBLE,y DOUBLE,z DOUBLE)");
		stat.close();
		try{
			stat = conn.createStatement();
			stat.executeUpdate("insert into GAME values ('softair',0,0,0,0)");
			stat.executeUpdate("insert into TEAM (name) values ('gold')");
			stat.executeUpdate("insert into TEAM (name) values ('diamond')");
			stat.close();
		}catch(Exception e){}

	}
	
	public boolean[] getGameStatus() throws Exception{
		ResultSet rs = conn.prepareStatement("select * from GAME where name='softair'").executeQuery();
		boolean[] status = new boolean[3];
		while(rs.next()){
			status[0]=plugin.intToBool(rs.getInt(2));
			status[1]=plugin.intToBool(rs.getInt(3));
			status[2]=plugin.intToBool(rs.getInt(4));
		}
		return status;
	}

	public void setEnabled(int enabled) throws Exception{
		conn.prepareStatement("update GAME set enabled ='"+enabled+"' where name='softair'").execute();
	}
	
	public void setWaiting(int waiting) throws Exception{
		conn.prepareStatement("update GAME set waiting ='"+waiting+"' where name='softair'").execute();
	}
	
	public void setInProgress(int inprogress) throws Exception{
		conn.prepareStatement("update GAME set inprogress ='"+inprogress+"'where name='softair'").execute();
	}
	
	public int getFee() throws Exception{
		ResultSet rs = conn.prepareStatement("select * from GAME where name='softair'").executeQuery();
		while(rs.next())
			return rs.getInt(5);
		return 0;
	}
	
	public void setFee(int fee) throws Exception{
		conn.prepareStatement("update Game set fee ='"+fee+"' where name='softair'").execute();
	}
	
	public void removePlayer(String name) throws Exception{
		conn.prepareStatement("delete from LOCATION where name = '"+name+"'").execute();
		conn.prepareStatement("delete from PLAYER where name = '"+name+"'").execute();
	}
	
	public void addPlayer(String name, String inventory,String armor,Location loc) throws Exception{
		conn.createStatement().executeUpdate("insert into PLAYER values ('"+name+"','"+inventory+"','"+armor+"')");
		conn.createStatement().executeUpdate("insert into LOCATION values ('"+name+"','"+loc.getWorld().getName()+"','"+loc.getX()+"','"+loc.getY()+"','"+loc.getZ()+"')");
	}
	
	public ArrayList<String> getPlayers() throws Exception{
		ArrayList<String> players = new ArrayList<String>();
		ResultSet rs = conn.prepareStatement("select name from PLAYER").executeQuery();
		while(rs.next()){
			players.add(rs.getString(1));
		}
		return players;
	}
	
	public void setSpawn(String name,String world,Double x,Double y,Double z) throws Exception{
		String query="update TEAM set world ='"+world+"',x = "+x+",y = "+y+",z = "+z+" where name = '"+name+"'";
		conn.createStatement().executeUpdate(query);
	}
	
	public Location getSpawn(String name) throws Exception{
		String query="select * from TEAM where NAME ='"+name+"'";
		PreparedStatement ps = conn.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		Location loc = null;
		while (rs.next()) {
			if(rs.getString(2)!=null)
				loc = SoftAirLocation.dataToLocation(plugin.getServer().getWorld(rs.getString(2)), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5));
		}
		return loc;
	}
	
	public HashMap<String,String> getInventory() throws Exception{
		ResultSet rs = conn.prepareStatement("select * from PLAYER").executeQuery();
		HashMap<String,String> inventory = new HashMap<String,String>();
		while(rs.next()){
			inventory.put(rs.getString(1), rs.getString(2));
		}
		return inventory;
	}
	
	public HashMap<String,String> getArmor() throws Exception{
		ResultSet rs = conn.prepareStatement("select * from PLAYER").executeQuery();
		HashMap<String,String> inventory = new HashMap<String,String>();
		while(rs.next()){
			inventory.put(rs.getString(1), rs.getString(3));
		}
		return inventory;
	}

	public HashMap<String,Location> getLocations() throws Exception{
		ResultSet rs = conn.prepareStatement("select * from LOCATION").executeQuery();
		HashMap<String,Location> loc = new HashMap<String,Location>();
		while(rs.next()){
			World w = plugin.getServer().getWorld(rs.getString(2));
			double x = rs.getDouble(3);
			double y = rs.getDouble(4);
			double z = rs.getDouble(5);
			loc.put(rs.getString(1), SoftAirLocation.dataToLocation(w,x,y,z));
		}
		return loc;
	}

	public void initialize() throws Exception{
		conn.prepareStatement("DROP TABLE TEAM").executeUpdate();
		conn.prepareStatement("DROP TABLE GAME").executeUpdate();
		conn.prepareStatement("DROP TABLE PLAYER").executeUpdate();
		conn.prepareStatement("DROP TABLE LOCATION").executeUpdate();
		prepareDB();
	}
	

}
	

