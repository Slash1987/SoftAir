package com.slash.softAir.Util;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;

public class SoftAirEconomy {

	public static boolean canAffor(String name,Double amount){
		Holdings h = iConomy.getAccount(name).getHoldings();
		return (h.balance()>=amount);
	}
	
	public static void pay(String name,Double amount){
		Holdings h = iConomy.getAccount(name).getHoldings();
		h.subtract(amount);
	}
	
	public static void give(String name,Double amount){
		Holdings h = iConomy.getAccount(name).getHoldings();
		h.add(amount);
	}
}
