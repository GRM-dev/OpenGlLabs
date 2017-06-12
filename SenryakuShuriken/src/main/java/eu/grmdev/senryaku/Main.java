package eu.grmdev.senryaku;

import eu.grmdev.senryaku.core.GameEngine;
import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.config.Configuration;
import eu.grmdev.senryaku.game.Game;
import eu.grmdev.senryaku.graphic.Window.WindowOptions;
import eu.grmdev.senryaku.jfx.FxGui;
import javafx.application.Application;
import lombok.Getter;

public class Main {
	private static GameEngine gameEng;
	private static IGame game;
	private static FxGui gui;
	private static boolean running;
	public static final boolean DEBUG = !false;
	private static WindowOptions opts;
	private static @Getter Configuration c;
	
	public static void main(String[] args) {
		c = new Configuration("senryaku.conf");
		if (!c.loadFromFile()) {
			System.err.println("Config file was not loaded!");
		} else {
			System.out.println("Configuration loaded.");
		}
		initWindowOptions();
		start();
	}
	
	private static synchronized void start() {
		try {
			System.out.println("Launcher starting");
			game = new Game();
			FxGui.init(game);
			new Thread(() -> Application.launch(FxGui.class)).start();;
			if (DEBUG) {
				while ((gui = FxGui.getInstance()) == null) {
					synchronized (game) {
						game.wait();
					}
				}
				// startGame();
				FxGui.getInstance().openSettings(true);
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
	
	@SuppressWarnings("unused")
	public static synchronized void startGame() {
		if (!running) {
			running = true;
			
			try {
				game = new Game();
				FxGui.init(game);
				gameEng = new GameEngine("Senryaku Shuriken", opts, game);
				gameEng.start();
				if (Config.CLOSE_GUI_ON_GAME_START.<Boolean> get() && !DEBUG) {
					gui.closeGui();
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	public static synchronized void closeApp(boolean closeGui) {
		if (closeGui) {
			gui.closeGui();
		}
		synchronized (game) {
			game.notifyAll();
		}
		if (running) {
			running = false;
			gameEng.stop();
		}
		if (!c.saveToFile()) {
			System.err.println("Config file was not saved!");
		}
	}
	
	public static void setGameClosed() {
		running = false;
	}
}
