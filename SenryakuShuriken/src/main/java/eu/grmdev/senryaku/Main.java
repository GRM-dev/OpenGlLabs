package eu.grmdev.senryaku;

import eu.grmdev.senryaku.core.GameEngine;
import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.game.Game;
import eu.grmdev.senryaku.graphic.Window.WindowOptions;
import eu.grmdev.senryaku.jfx.FxGui;

public class Main {
	private static GameEngine gameEng;
	private static IGame game;
	private static FxGui gui;
	private static boolean running;
	public static final boolean DEBUG = !false;
	private static WindowOptions opts;
	
	public static void main(String[] args) {
		initWindowOptions();
		
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
	
	private static void initWindowOptions() {
		opts = new WindowOptions();
		opts.cullFace = false;
		opts.showFps = true;
		opts.compatibleProfile = true;
		opts.antialiasing = true;
		opts.frustumCulling = false;
		opts.maximized = false;
		opts.width = 800;
		opts.height = 600;
		opts.vSync = true;
	}
	
	public static synchronized void startGame() {
		if (!running) {
			running = true;
			try {
				gameEng = new GameEngine("Senryaku Shuriken", opts, game);
				gameEng.start();
				gui.closeGui();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	public static synchronized void closeApp() {
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
