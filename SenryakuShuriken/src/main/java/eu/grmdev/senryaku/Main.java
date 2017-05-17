package eu.grmdev.senryaku;

import eu.grmdev.senryaku.core.GameEngine;
import eu.grmdev.senryaku.game.Game;
import eu.grmdev.senryaku.jfx.FxGui;

public class Main {
	private static GameEngine gameEng;
	private static Game game;
	private static FxGui gui;
	private static boolean running;
	private static boolean DEBUG = !true;
	private static Config c;
	
	public static void main(String[] args) {
		c = new Config();
		gui = new FxGui();
		if (!DEBUG) {
			gui.startup();
		}
		try {
			game = new Game();
			if (DEBUG) {
				startGame();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void startGame() {
		if (!running) {
			running = true;
			try {
				gameEng = new GameEngine("Senryaku Shuriken", c.vSync, c.opts, game);
				gameEng.start();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	public static void closeApp() {
		gui.closeGui();
		if (running) {
			running = false;
			gameEng.stop();
		}
	}
	
	public static void setGameClosed() {
		running = false;
	}
}
