package com.slash.softAir;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import com.iConomy.iConomy;
import com.slash.softAir.Listener.SoftAirEntityListener;
import com.slash.softAir.Listener.SoftAirPlayerListener;
import com.slash.softAir.Util.SQLManager;
import com.slash.softAir.Util.SoftAirCommand;
import com.slash.softAir.Util.SoftAirEconomy;
import com.slash.softAir.Util.SoftAirTimer;

public class SoftAir extends org.bukkit.plugin.java.JavaPlugin{

	//Service Class Declaration
	public static SoftAir instance;
	public Logger log = Logger.getLogger("Minecraft");
	private PluginManager pm;
	private SoftAirEntityListener entityListener;
	private SoftAirPlayerListener playerListener;
	private SoftAirServerListener serverListener;
	private SQLManager sql;
	private SoftAirTimer timer;
	public int x=5;
	public int timerID;
	public ChatColor color;
	
	
	//Teams Declaration
	public SoftAirTeam gold = new SoftAirTeam("gold",this);      //GOLD TEAM
	public SoftAirTeam diamond = new SoftAirTeam("diamond",this);//DIAMOND TEAM
	
	//Player Related Class
	private SoftAirPlayers players = new SoftAirPlayers();

	
	public SoftAirGame GAME = new SoftAirGame();
	public iConomy iCo = null;
	
	//Constructor
	public SoftAir(){
		SoftAir.instance=this;
	}
	
	
	//onEnable & onDisable method
	public void onDisable() {
		try {
			sql.disconnect();
		} catch (Exception e) {

		}
		log.info("[SoftAir] Plugin Disabled");
	}
	public void onEnable() {
		entityListener = new SoftAirEntityListener(this);
		playerListener = new SoftAirPlayerListener(this);
		serverListener = new SoftAirServerListener(this);
		pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Lowest, this);
		getCommand("softair").setExecutor(new SoftAirCommand(this));
		sql = new SQLManager(this);
		try {
			sql.connect();
			loadConfiguration();
		} catch (Exception e) {

		}
		log.info("[SoftAir] Plugin Enabled");
	}

	
	//Configuration loader
	private void loadConfiguration() throws Exception{
		log.info("[SoftAir] Loading Configuration");
		/**
		 * Retrive Information of the GAME (isEnabled,isWaiting,isInProgress)
		 * save it to the class GAME
		 */
		boolean[] status = sql.getGameStatus();
		GAME.setGameEnabled(status[0]);
		GAME.setGameWaiting(status[1]);
		GAME.setGameInProgress(status[2]);
		/**
		 * Retrive Teams Information (Spawn point)
		 */
		Location gold=sql.getSpawn("gold");
		Location diamond=sql.getSpawn("diamond");
		log.info("[SoftAir] Configuration Loaded");
		/**
		 * If game isInProgress || gameWaiting
		 * Load players in game before the server crash
		 * Restore GAME status to NOTINPROGRESS and NOTWAITING
		 */
		if(GAME.isGameInProgress()||GAME.isGameWaiting()){
			log.info("[SoftAir]Server Crashed During a Play. Retriving Players Inventory");
			try{
				players.addToRestoreOnCrash(sql.getPlayers(),sql.getInventory(),sql.getArmor(),sql.getLocations());
				log.info("[SoftAir]Information retrived. Inventory will be restored on player login");
				GAME.setGameInProgress(false);
				GAME.setGameWaiting(false);
			}
			catch (Exception e){
				log.info("[SoftAir]Failed to retrive players information. Please check the DB status and reload the plugin");
				log.info("[SoftAir]To initialize the game(and loose all the information stored) type /softair initialize");
			}
		}
		GAME.setFee((double) sql.getFee());
		/**
		 * If spawn for team are not set disableTheGame
		 */
		if(gold!=null){
			this.gold.setCords(gold);
			log.info("[SoftAir]Spawn Point for GOLD team retrieved");
		}
		if(diamond!=null){
			this.diamond.setCords(diamond);
			log.info("[SoftAir]Spawn Point for DIAMOND team retrieved");
		}
		if(gold!=null&&diamond!=null){
			GAME.setSpawnSet(true);
			log.info("[SoftAir]All spawn point retrieved");
		}
		else
			GAME.setSpawnSet(false);
	}

	public void setPlayerReady(CommandSender sender) {
		/**
		 * Player typed /softair ready
		 * Control if the player joined the game
		 * set it's status to ready
		 * control if all players are ready
		 */
		String player = ((Player) sender).getName();
		if(isPlayerInGame(player)){
			players.setReady(player);
			sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"You set your status to READY");
			getServer().broadcastMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_PURPLE+player+ChatColor.DARK_GREEN+"has set it's status to READY");
			controlStatus();
		}
		else
			sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+"You must join the game first");
	}
	public void controlStatus(){
		/**
		 * if all players are ready and there are minimum 2 players, set the game ready to start and start a countdown
		 */
		if(players.isAllReady()){
			if(gold.getSize()>=1&&diamond.getSize()>=1){
				players.sendMessageToAll(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"Ready To Start The Battle");
				timer = new SoftAirTimer(this,players);
				timerID = this.getServer().getScheduler().scheduleSyncRepeatingTask(this,timer, 20L, 20L);
				try{
					sql.setWaiting(0);
					sql.setInProgress(1);
				}
				catch(Exception e){}
			}
			else
			players.sendMessageToAll(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+"Need at least 1 player per team");
		}
	}

	public void playersNumber(CommandSender sender) {
		/**
		 * If player typped /softair Players send him the number of players in game and in each team
		 */
		int[] playersNumber = players.getPlayersInGame();
		sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"Total Players In Game   : "+ChatColor.YELLOW+playersNumber[0]);
		sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GOLD+"Players In Gold Team    : "+ChatColor.YELLOW+playersNumber[1]);
		sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.AQUA+"Players In Diamond Team : "+ChatColor.YELLOW+playersNumber[2]);
	}
	public boolean isPlayerInGame(String player){
		return players.isPlayerInGame(player);
	}

	public void disableGame() {
		/**
		 * Disable the game (players can't join)
		 */
		GAME.setGameEnabled(false);
		try {
			sql.setEnabled(boolToInt(false));
		} catch (Exception e) {

		}
	}
	public void enableGame() {
		/**
		 * Enable the game  (players can join)
		 */
		GAME.setGameEnabled(true);
		try {
			sql.setEnabled(boolToInt(true));
		} catch (Exception e) {

		}
	}


	public void leave(CommandSender sender) {
		/**
		 * Player typed /softair leave
		 * remove the player from the team
		 * remove player from the players list
		 * restore it's inventory
		 */
		Player player = (Player)sender;
		SoftAirTeam team = getTeam(players.getTeam(player.getName()));
		SoftAirTeam oTeam = getOtherTeam(team);
		players.removePlayer(player);
		players.sendMessageToAll(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_PURPLE+player.getName()+ChatColor.DARK_GREEN+" leaved the game");
		team.removePlayer(player.getName());
		if(team.isEmpty()&&!oTeam.isEmpty()){
			notifyVictory(oTeam);
		}
		try {
			sql.removePlayer(player.getName());
		} catch (Exception e) {

		}
	}
	
	public void join(CommandSender sender) {
		/**
		 * player typed /softair join
		 * put the player in the team with less members(to keep teams balanced)
		 * add player to the players list
		 * add player to the team
		 * teleport the player to the team location
		 */
		Player p = (Player)sender;
			if(iCo!=null){
				if(SoftAirEconomy.canAffor(p.getName(),GAME.getFee())){
					SoftAirEconomy.pay(p.getName(), GAME.getFee());
					GAME.setPool(GAME.getPool()+GAME.getFee());
					sender.sendMessage(ChatColor.YELLOW+"[SoftAir]"+ChatColor.GREEN+iConomy.format(GAME.getFee())+" subtracted from your holdings");
				}
				else{
					sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"You can't affor the game fee");
					return;
				}
			}
			GAME.setGameWaiting(true);
			if(gold.getSize()<=diamond.getSize()){
				players.addPlayer(p, "gold");
				gold.addPlayer(p.getName());
				sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"Succesfully joined"+ChatColor.GOLD+" GOLD team");
				sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"Type '/softair ready' to begin the game");
				p.teleport(gold.getCords());
			}
			else{
				players.addPlayer(p, "diamond");
				diamond.addPlayer(p.getName());
				sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"Succesfully joined"+ChatColor.AQUA+" DIAMOND team");
				sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"Type '/softair ready' to begin the game");
				p.teleport(diamond.getCords());
			}
			String name=p.getName();
			try {
				sql.addPlayer(name, players.getInventory(name), players.getArmor(name),((Player)sender).getLocation());
				sql.setWaiting(1);
			} catch (Exception e) {}
	}
	public void hit(Player player,Player hitter){
		/**
		 * When a player is hit by an arrow
		 * remove player from the players list
		 * remove player from the team
		 * check if the team is empty and notify the victory to the other team
		 */
		SoftAirTeam team = getTeam(players.getTeam(player.getName()));
		SoftAirTeam hTeam = getOtherTeam(team);
		player.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+"You have been killed by "+ChatColor.DARK_PURPLE+hitter.getName());
		hitter.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+"You have killed "+ChatColor.DARK_PURPLE+player.getName());
		try {
			sql.removePlayer(player.getName());
		} catch (Exception e) {

		}
		team.removePlayer(player.getName());
		players.removePlayer(player);
		if(team.isEmpty()){
			notifyVictory(hTeam);
		}
	}
	
	public void notifyVictory(SoftAirTeam team){
		ArrayList<String> player = team.getPlayers();
		ChatColor tColor = null;
		if(team.getName().equalsIgnoreCase("gold"))
			tColor=ChatColor.GOLD;
		else
			tColor=ChatColor.AQUA;
		if(player.isEmpty()){
			return;
		}
		else{
			double reward = GAME.getPool()/player.size();
			for(int i=0; i<player.size();i++){
				Player p = getServer().getPlayer(player.get(i));
				p.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"Congratulation, you're the winner");
				if(iCo!=null)
					SoftAirEconomy.give(p.getName(), reward);
				else
					p.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.RED+"Lost connection with iConomy");
				team.removePlayer(player.get(i));
				this.players.removePlayer(getServer().getPlayer(player.get(i)));
				try{
					sql.removePlayer(player.get(i));
				}
				catch(Exception e){}
			}
		}
		getServer().broadcastMessage(ChatColor.YELLOW+"[SoftAir]"+ChatColor.DARK_GREEN+"The "+tColor+team.getName()+ChatColor.DARK_GREEN+" team has won the battle");
		getServer().broadcastMessage(ChatColor.YELLOW+"[SoftAir]"+ChatColor.DARK_GREEN+"Ready to start a new game");
		gold.clear();
		diamond.clear();
		this.players.clear();
		initializeGame();
	}
	public void onPlayerQuit(String name){
		/**
		 * player left the game before leaving the game
		 * remove player from the players list and add it to the player crashed
		 */
		if(!players.isPlayerInGame(name))
			return;
		if(players.getTeam(name).equalsIgnoreCase("gold")){
			gold.removePlayer(name);
		}
		else{
			diamond.removePlayer(name);
		}
		players.removePlayerOnQuit(name);
		players.sendMessageToAll(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_PURPLE+name+ChatColor.DARK_GREEN+" disconnected");
	}
	public void onPlayerLogin(Player player){
		/**
		 * a crashed Player has joined Minecraft
		 * restore the player inventory and it's location
		 */
		if(players.isPlayerToRestore(player.getName()))
			try{
				System.out.println(player.toString());
				players.restore(player);
				sql.removePlayer(player.getName());
				player.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.DARK_GREEN+"All your stuff has been restored");
			} catch(Exception e){
	
			}
	}
	
	public SoftAirTeam getTeam(String team){
		if(team.equalsIgnoreCase("gold"))
			return gold;
		if(team.equalsIgnoreCase("diamond"))
			return diamond;
		return null;
	}
	public SoftAirTeam getOtherTeam(SoftAirTeam team){
		if(team.getName().equalsIgnoreCase("gold"))
			return diamond;
		else if(team.getName().equalsIgnoreCase("diamond"))
			return gold;
		return null;
	}

	public boolean isGameWaiting(){
		/**
		 * return if the Game is in the wait status
		 */
		return GAME.isGameWaiting();
	}
	public boolean isGameInProgress(){
		/**
		 * return if the game is in progress
		 */
		return GAME.isGameInProgress();
	}
	public boolean isGameEnabled(){
		/**
		 * return if the game is enabled
		 */
		return GAME.isGameEnabled();
	}
	
	public void setSpawn(CommandSender sender,String team) {
		/**
		 * Admin typed /softair setspawn "team"
		 * set the spawn of the team selected to the admin location
		 * if both spawn are set, tell the game that can be enabled
		 */
		if(team.equalsIgnoreCase("gold")){
			gold.setCords(((Player)sender).getLocation());
			sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.GOLD+" Gold spawn set");
			try {
				sql.setSpawn("gold", gold.getCords().getWorld().getName(), gold.getCords().getX(), gold.getCords().getY(), gold.getCords().getZ());
			} catch (Exception e) {
	
			}
		}
		else if(team.equalsIgnoreCase("diamond")){
			diamond.setCords(((Player)sender).getLocation());
			sender.sendMessage(ChatColor.YELLOW+"[Softair]"+ChatColor.AQUA+" Diamond spawn set");
			try {
				sql.setSpawn("diamond", diamond.getCords().getWorld().getName(), diamond.getCords().getX(), diamond.getCords().getY(), diamond.getCords().getZ());
			} catch (Exception e) {
	
			}
		}
		if(gold.getSpawnSet()&&diamond.getSpawnSet()){
			GAME.setSpawnSet(true);
			sender.sendMessage(ChatColor.YELLOW+"[SoftAir]"+ChatColor.DARK_GREEN+" Game can be enabled");
		}
	}

	public String getPlayerTeam(String name) {
		/**
		 * return the team name the player belong to
		 */
		return players.getTeam(name);
	}

	public void initialize(){
		initializeGame();
		gold = new SoftAirTeam("gold",this);
		diamond = new SoftAirTeam("diamond",this);
		players.clear();
		try{
			sql.initialize();
		}
		catch(Exception e){}
		try{
			loadConfiguration();
		}
		catch(Exception e){}
	}

	public void setGameInProgress(boolean b) {
		GAME.setGameInProgress(b);
		try{
			sql.setInProgress(boolToInt(b));
		}
		catch(Exception e){}
	}

	public void setGameWaiting(boolean b){
		GAME.setGameWaiting(b);
		try{
			sql.setWaiting(boolToInt(b));
		}
		catch(Exception e){}
	}

	public void setGameFee(Double fee){
		GAME.setFee(fee);
		try{
			sql.setFee(fee.intValue());
		}
		catch(Exception e){}
	}
	
	public boolean intToBool(int i){
		return (i!=0);
	}


	public int boolToInt(boolean b){
		if(b)
			return 1;
		else
			return 0;
	}
	
	public void initializeGame(){
		setGameInProgress(false);
		setGameWaiting(false);
		setGameFee(0.0);
		GAME.setPool(0.0);
	}


	public void onPlayerMove(Player player) {
		if(players.getTeam(player.getName()).equalsIgnoreCase("GOLD"))
			player.teleport(gold.getCords());
		else
			player.teleport(diamond.getCords());
	}
}