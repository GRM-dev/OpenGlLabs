package eu.grmdev.senryaku;

import eu.grmdev.senryaku.jfx.FxGui;

public class Main {
	private static FxGui gui;
	private static Game game;
	private static boolean running;
	
	public static void main(String[] args) {
		gui = new FxGui();
		gui.startup();
		game = new Game();
	}
	
	public static void startGame() {
		if (!running) {
			running = true;
			game.start();
		}
	}
	
	public static void closeApp() {
		gui.close();
		if (running) {
			running = false;
			game.stop();
		}
	}
	
	public static void setGameClosed() {
		running = false;
	}
}
