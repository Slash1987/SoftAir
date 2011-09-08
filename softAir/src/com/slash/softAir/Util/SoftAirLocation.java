package com.slash.softAir.Util;

import org.bukkit.Location;
import org.bukkit.World;

public class SoftAirLocation {

	public static Location dataToLocation(World world,Double x,Double y,Double z){
		if(world!=null)
			return new Location(world,x,y,z);
		return null;
	}
	
}
