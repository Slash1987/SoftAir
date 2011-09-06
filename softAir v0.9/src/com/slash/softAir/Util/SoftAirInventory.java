package com.slash.softAir.Util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoftAirInventory {
	
	public static String InventoryToString(ItemStack[] inventory){
		if(inventory==null)
			return null;
		String inventorychain ="";
	    for (ItemStack item : inventory) {
			if (item != null) {
				Integer id = 1;
				Integer damage = 0;
				Integer stackSize = 0;
				id = item.getTypeId();
				stackSize = item.getAmount();
				damage = (int) item.getDurability();
				inventorychain = inventorychain + id.toString()+":"+stackSize.toString()+":"+damage.toString()+";";
			}
			else {
				inventorychain = inventorychain + "0:0:0;";
			}
		}
	    return inventorychain;
	}
	
	public static String ArmorToString(ItemStack[] armor){
		if(armor == null)
			return null;
		String armorchain ="";
	    for (ItemStack item : armor) {
			if (item != null) {
				Integer id = 1;
				Integer damage = 0;
				Integer stackSize = 0;
				id = item.getTypeId();
				stackSize = item.getAmount();
				damage = (int) item.getDurability();
				armorchain = armorchain + id.toString()+":"+stackSize.toString()+":"+damage.toString()+";";
			}
			else {
				armorchain = armorchain + "0:0:0;";
			}
	    }
	    return armorchain;
	}

	public static void StringToInventory(String inventory,Player player){
		if(inventory==null)
			return;
		String[] inventoryParts = inventory.split(";");
	    for(String invslot : inventoryParts){
	    	String[] thisItem = invslot.split(":");
	    	Integer id = Integer.parseInt(thisItem[0]);
	    	Integer stackSize = Integer.parseInt(thisItem[1]);
	    	Short damage = Short.parseShort(thisItem[2]);
	    	if(id != 0){
	    		ItemStack thisStack = new ItemStack(id,stackSize,damage);
	    		player.getInventory().addItem(thisStack);
	    	}
	    }
	}
	
	public static void StringToArmor(String armor, Player player){
		if(armor==null)
			return;
		String[] armorParts = armor.split(";");
	    ItemStack[] arm = new ItemStack[4];
	    int i= 0;
	    for(String invslot : armorParts){
	    	String[] thisItem = invslot.split(":");
	    	Integer id = Integer.parseInt(thisItem[0]);
	    	Integer stackSize = Integer.parseInt(thisItem[1]);
	    	Short damage = Short.parseShort(thisItem[2]);
	    	if(id != 0)
	    		arm[i] = new ItemStack(id,stackSize,damage);
	    	else
	    		arm[i] = new ItemStack(0);
	    	i++;
	    }
	    player.getInventory().setArmorContents(arm);
	}

	public static void setSoftAirItem(Player player){
		player.getInventory().setItemInHand(new ItemStack(261,1));
		player.getInventory().addItem(new ItemStack(262,256));
	}
	
	public static void getGoldArmor(Player player){
		player.getInventory().setHelmet(new ItemStack(314,1));
		player.getInventory().setChestplate(new ItemStack(315,1));
		player.getInventory().setLeggings(new ItemStack(316,1));
		player.getInventory().setBoots(new ItemStack(317,1));
	}
	
	public static void getDiamondArmor(Player player){
		player.getInventory().setHelmet(new ItemStack(310,1));
		player.getInventory().setChestplate(new ItemStack(311,1));
		player.getInventory().setLeggings(new ItemStack(312,1));
		player.getInventory().setBoots(new ItemStack(313,1));
	}

}
