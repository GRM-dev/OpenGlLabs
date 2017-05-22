package eu.grmdev.senryaku.core;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.events.GameEvent;
import eu.grmdev.senryaku.core.handlers.*;

public class LogicThread extends Thread {
	private long lastTickTimeNano;
	private float tps = Config.TARGET_UPS;
	private long tickTime = (long) (1000 / tps);
	private long pastTickNanoDiff;
	private int tickCounter;
	private double lastFullTickNano;
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
		mouseHandler = new MouseHandler();
		eHandler = new EventHandler();
		keyHandler = new KeyboardHandler(eHandler);
	}
	
	public void init() {
		mouseHandler.init(window);
		glfwSetKeyCallback(window.getWindowHandle(), keyHandler);
		isReady = true;
	}
	
	@Override
	public void run() {
		while (!isReady) {
			try {
				Thread.sleep(50);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			game.initLogic(eHandler);
			eHandler.addCycleGameEventListener(event -> {
				System.out.println("TPS: " + lastTickCounter);
			});
			System.out.println("Start Logic Thread");
			loop();
			System.out.println("Stop Logic Thread");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void loop() {
		lastTickTimeNano = System.nanoTime();
		lastFullTickNano = lastTickTimeNano;
		tickLoopEvent = new GameEvent(true, null) {};
		cycleLoopEvent = new GameEvent(true, null) {};
		while (!shouldStop()) {
			input();
			keyHandler.dispatchAllActiveKeyEvents();
			if (tickCounter == 0) {
				eHandler.dispatchCycleGameEvent(cycleLoopEvent);
			}
			eHandler.dispatchTickGameEvent(tickLoopEvent);
			update(pastTickNanoDiff);
			tickLoopEvent.reset();
			sync();
		}
		tickLoopEvent.setConsumed(true);
	}
	
	protected void input() {
		mouseHandler.input(window);
		game.input(window, mouseHandler);
	}
	
	protected void update(float interval) {
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
		long thisTickTimeNano = System.nanoTime();
		long diff = (long) (tickTime - (thisTickTimeNano - lastTickTimeNano) / 1E6);
		if (diff > 0) {
			try {
				Thread.sleep(diff);
				thisTickTimeNano = System.nanoTime();
				pastTickNanoDiff = thisTickTimeNano - lastTickTimeNano;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			pastTickNanoDiff = (long) (-diff / 1E3);
		}
		if (thisTickTimeNano / 1E9 - lastFullTickNano / 1E9 >= 0.98) {
			lastFullTickNano = thisTickTimeNano;
			lastTickCounter = tickCounter;
			tickCounter = 0;
		}
		lastTickTimeNano = thisTickTimeNano;
	}
}
