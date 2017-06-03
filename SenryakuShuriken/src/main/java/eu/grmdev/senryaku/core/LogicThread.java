package eu.grmdev.senryaku.core;

import static org.lwjgl.glfw.GLFW.*;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.events.GameEvent;
import eu.grmdev.senryaku.core.handlers.*;
import eu.grmdev.senryaku.graphic.Window;

public class LogicThread extends Thread {
	private long startTickTime;
	private float tps = Config.TARGET_UPS;
	private long tickTime = (long) (1000 / tps);
	private long lastTickSpan;
	private int tickCounter;
	private double startCycleTime;
	private IGame game;
	private final EventHandler eHandler;
	private final MouseHandler mouseHandler;
	private final KeyboardHandler keyHandler;
	private boolean indicateStop;
	private Window window;
	private boolean isReady;
	private GameEvent tickLoopEvent;
	private GameEvent cycleLoopEvent;
	private int lastTickCounter;
	
	public LogicThread(IGame game, Window window) {
		this.game = game;
		this.window = window;
		setName("GAME_LOGIC_LOOP_THREAD");
		eHandler = new EventHandler(game);
		mouseHandler = new MouseHandler(game, eHandler);
		keyHandler = new KeyboardHandler(eHandler, game);
	}
	
	public void init() {
		mouseHandler.init(window);
		glfwSetKeyCallback(window.getWindowHandle(), keyHandler);
		glfwSetMouseButtonCallback(window.getWindowHandle(), mouseHandler);
		isReady = true;
	}
	
	@Override
	public void run() {
		while (!isReady) {
			try {
				if (shouldStop()) { return; }
				Thread.sleep(50);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			game.initLogic(eHandler, mouseHandler);
			tickLoopEvent = new GameEvent(window, game) {};
			cycleLoopEvent = new GameEvent(window, game) {};
			eHandler.addCycleGameEventListener(event -> {
				System.out.println("TPS: " + lastTickCounter);
			});
			System.out.println("Start Logic Thread");
			startTickTime = System.currentTimeMillis();
			startCycleTime = startTickTime;
			while (!shouldStop()) {
				loop();
			}
			tickLoopEvent.setConsumed(true);
			System.out.println("Stop Logic Thread");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void loop() {
		input();
		keyHandler.dispatchAllActiveKeyEvents();
		if (tickCounter == 0) {
			eHandler.dispatchCycleGameEvent(cycleLoopEvent);
		}
		eHandler.dispatchTickGameEvent(tickLoopEvent);
		update(lastTickSpan);
		tickLoopEvent.reset();
		sync();
	}
	
	private void input() {
		mouseHandler.input(window);
		game.input(window, mouseHandler);
	}
	
	private void update(float interval) {
		game.update(interval, mouseHandler, window);
	}
	
	private boolean shouldStop() {
		return indicateStop;
	}
	
	public void setStop() {
		indicateStop = true;
	}
	
	private void sync() {
		tickCounter++;
		long endTickTime = System.currentTimeMillis();
		long durationSpan = endTickTime - startTickTime;
		long durationToSleep = tickTime - durationSpan;
		if (durationToSleep > 0) {
			sleepFor(durationToSleep);
			endTickTime = System.currentTimeMillis();
			durationSpan = endTickTime - startTickTime;
		}
		if (endTickTime - startCycleTime >= 998) {
			startCycleTime = endTickTime;
			lastTickCounter = tickCounter;
			tickCounter = 0;
		}
		lastTickSpan = durationSpan;
		startTickTime = endTickTime;
	}
	
	private void sleepFor(long duration) {
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
