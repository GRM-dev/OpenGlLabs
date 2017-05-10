package eu.grmdev.senryaku;

import eu.grmdev.senryaku.jfx.FxGui;

public class Main {
	private static FxGui gui;
	private static Game game;
	private static boolean running;
	private static boolean DEBUG = true;
	
	public static void main(String[] args) {
		gui = new FxGui();
		if (!DEBUG) {
			gui.startup();
		}
		game = new Game();
		if (DEBUG) {
			startGame();
		}
	}
	
	public static void startGame() {
		if (!running) {
			running = true;
			game.start();
		}
	}
	
	public static void closeApp() {
		gui.closeGui();
		if (running) {
			running = false;
			game.stop();
		}
	}
	
	public static void setGameClosed() {
		running = false;
	}
}
