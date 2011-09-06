package com.slash.softAir;

public class SoftAirGame {
	
	private boolean gameWaiting;
	private boolean gameEnabled;
	private boolean gameInProgress;
	private boolean spawnSet;
	public boolean isGameWaiting() {
		return gameWaiting;
	}
	public void setGameWaiting(boolean gameWaiting) {
		this.gameWaiting = gameWaiting;
	}
	public boolean isGameEnabled() {
		return gameEnabled;
	}
	public void setGameEnabled(boolean gameEnabled) {
		this.gameEnabled = gameEnabled;
	}
	public boolean isGameInProgress() {
		return gameInProgress;
	}
	public void setGameInProgress(boolean gameInProgress) {
		this.gameInProgress = gameInProgress;
	}
	public boolean isSpawnSet() {
		return spawnSet;
	}
	public void setSpawnSet(boolean spawnSet) {
		this.spawnSet = spawnSet;
	}

}