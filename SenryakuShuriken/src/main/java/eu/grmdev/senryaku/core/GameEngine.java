package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.Main.Config;
import eu.grmdev.senryaku.core.handlers.MouseInput;
import eu.grmdev.senryaku.core.misc.Timer;

public class GameEngine implements Runnable {
	private final Window window;
	private final Thread gameLoopThread;
	private final Timer timer;
	private final IGame gameLogic;
	private final MouseInput mouseInput;
	private double lastFps;
	private int fps;
	private String title;
	
	public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, IGame gameLogic) throws Exception {
		this.title = windowTitle;
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		window = new Window(windowTitle, vSync, opts);
		mouseInput = new MouseInput();
		this.gameLogic = gameLogic;
		timer = new Timer();
	}
	
	public void start() {
		gameLoopThread.start();
	}
	
	@Override
	public void run() {
		try {
			init();
			gameLoop();
		}
		catch (Exception excp) {
			excp.printStackTrace();
		}
		finally {
			destroy();
		}
	}
	
	protected void init() throws Exception {
		window.init();
		timer.init();
		mouseInput.init(window);
		gameLogic.init(window);
		lastFps = timer.getTime();
		fps = 0;
	}
	
	protected void gameLoop() {
		float elapsedTime;
		float accumulator = 0f;
		float interval = 1f / Config.TARGET_UPS;
		boolean running = true;
		
		while (running && !window.windowShouldClose()) {
			elapsedTime = timer.getElapsedTime();
			accumulator += elapsedTime;
			
			input();
			
			while (accumulator >= interval) {
				update(interval);
				accumulator -= interval;
			}
			
			render();
			
			if (!window.isVSync()) {
				sync();
			}
		}
	}
	
	protected void destroy() {
		gameLogic.destroy();
	}
	
	private void sync() {
		float loopSlot = 1f / Config.TARGET_FPS;
		double endTime = timer.getLastLoopTime() + loopSlot;
		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException ie) {}
		}
	}
	
	protected void input() {
		mouseInput.input(window);
		gameLogic.input(window, mouseInput);
	}
	
	protected void update(float interval) {
		gameLogic.update(interval, mouseInput, window);
	}
	
	protected void render() {
		if (window.getWindowOptions().showFps && timer.getLastLoopTime() - lastFps > 1) {
			lastFps = timer.getLastLoopTime();
			window.setWindowTitle(title + " - " + fps + " FPS");
			fps = 0;
		}
		fps++;
		gameLogic.render(window);
		window.update();
	}
	
}
