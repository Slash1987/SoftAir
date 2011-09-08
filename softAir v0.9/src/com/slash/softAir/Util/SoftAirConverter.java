package com.slash.softAir.Util;

public class SoftAirConverter {
	
	public static int stringToInt(String s){
		try{
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e){
			return 0;
		}
	}
	
	public static Double stringToDouble(String s){
		try{
			return Double.parseDouble(s);
		}
		catch(NumberFormatException e){
			return 0.0;
		}
	}
}
