package eu.grmdev.senryaku.core;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.map.Tile;
import eu.grmdev.senryaku.core.misc.Timer;
import eu.grmdev.senryaku.graphic.Window;
import lombok.Getter;

public class GameEngine implements Runnable {
	private @Getter static GameEngine instance;
	private final Window window;
	private Thread renderThread;
	private final Timer timer;
	private final IGame game;
	private double lastFps;
	private int fps;
	private String title;
	private LogicThread logicThread;
	private boolean running;
	
	public GameEngine(String windowTitle, Window.WindowOptions opts, IGame game) throws Exception {
		GameEngine.instance = this;
		this.title = windowTitle;
		this.game = game;
		timer = new Timer();
		window = new Window(windowTitle, opts, game.getCamera());
		renderThread = new Thread(this, "GAME_RENDER_LOOP_THREAD");
		logicThread = new LogicThread(game, window);
	}
	
	public void start() {
		if (running) {
			System.err.println("Already running!");
			return;
		}
		renderThread.start();
		logicThread.start();
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Start Render Thread");
			init();
			logicThread.initRender();
			renderLoop();
			System.out.println("Stop Render Thread");
		}
		catch (Exception excp) {
			excp.printStackTrace();
		}
		finally {
			destroy();
		}
	}
	
	private void init() throws Exception {
		window.init();
		timer.init();
		game.initRender(window);
		fps = 0;
	}
	
	private void renderLoop() {
		running = true;
		while (running && !window.windowShouldClose()) {
			render();
			if (!window.isVSync()) {
				sync();
			}
		}
	}
	
	private void render() {
		double dSub = timer.getLastLoopTime() - lastFps;
		if (window.getWindowOptions().showFps && dSub > 0.99f) {
			lastFps = timer.getLastLoopTime();
			window.setWindowTitle(title + " - " + fps + " FPS");
			fps = 0;
		}
		timer.getElapsedTime();
		fps++;
		game.render(window);
		window.update();
	}
	
	private void sync() {
		float loopSlot = 1f / Config.TARGET_FPS.<Integer> get();
		double endTime = timer.getLastLoopTime() + loopSlot;
		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException ie) {}
		}
	}
	
	public void stop() {
		glfwSetWindowShouldClose(window.getWindowHandle(), true);
	}
	
	private void destroy() {
		logicThread.setStop();
		game.destroy();
		GameEngine.instance = null;
		Tile.destroy();
	}
}
