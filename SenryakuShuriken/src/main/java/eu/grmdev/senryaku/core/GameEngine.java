package eu.grmdev.senryaku.core;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.misc.Timer;
import lombok.Getter;

public class GameEngine implements Runnable {
	private @Getter static GameEngine instance;
	private final Window window;
	private final Thread renderThread;
	private final Timer timer;
	private final IGame gameLogic;
	private double lastFps;
	private int fps;
	private String title;
	private LogicThread logicThread;
	
	public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, IGame gameLogic) throws Exception {
		GameEngine.instance = this;
		this.title = windowTitle;
		this.gameLogic = gameLogic;
		timer = new Timer();
		window = new Window(windowTitle, vSync, opts);
		renderThread = new Thread(this, "GAME_RENDER_LOOP_THREAD");
		logicThread = new LogicThread(gameLogic, window);
	}
	
	public void start() {
		renderThread.start();
		logicThread.start();
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Start Render Thread");
			init();
			logicThread.init();
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
	
	protected void init() throws Exception {
		window.init();
		timer.init();
		gameLogic.init(window);
		lastFps = timer.getTime();
		fps = 0;
	}
	
	protected void renderLoop() {
		boolean running = true;
		while (running && !window.windowShouldClose()) {
			render();
			if (!window.isVSync()) {
				sync();
			}
		}
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
	
	public void stop() {
		glfwSetWindowShouldClose(window.getWindowHandle(), true);
	}
	
	protected void destroy() {
		logicThread.setStop();
		gameLogic.destroy();
	}
}
