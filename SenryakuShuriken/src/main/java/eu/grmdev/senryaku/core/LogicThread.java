package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.core.handlers.*;

public class LogicThread extends Thread {
	private long lastTickTimeNano;
	private float tps = 20;
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
		System.out.println("Start Logic Thread");
		loop();
		System.out.println("Stop Logic Thread");
	}
	
	private void loop() {
		lastTickTimeNano = System.nanoTime();
		lastFullTickNano = lastTickTimeNano;
		while (!shouldStop()) {
			input();
			update(pastTickNanoDiff);
			keyHandler.dispatchAllActiveKeyEvents();
			sync();
		}
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
			System.out.println("TPS: " + tickCounter);
			lastFullTickNano = thisTickTimeNano;
			tickCounter = 0;
		}
		lastTickTimeNano = thisTickTimeNano;
	}
}
