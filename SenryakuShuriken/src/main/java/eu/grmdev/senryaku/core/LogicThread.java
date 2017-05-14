package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.data.EntitiesData;
import eu.grmdev.senryaku.graphic.GameWindow;

public class LogicThread extends Thread {
	private GameWindow graphic;
	private EventHandler eventHandler;
	private Game game;
	private long lastTickTimeNano;
	private float tps = 20;
	private long tickTime = (long) (1000 / tps);
	private long pastTickNanoDiff;
	private int tickCounter;
	private double lastFullTickNano;
	
	public LogicThread(Game game, GameWindow graphic, EventHandler eventHandler) {
		this.game = game;
		this.graphic = graphic;
		this.eventHandler = eventHandler;
		setName("Logic Thread");
	}
	
	@Override
	public void run() {
		init();
		System.out.println("Start Logic Thread");
		loop();
		System.out.println("Stop Logic Thread");
	}
	
	private void init() {
		EntitiesData.load(game);
		while (!graphic.isReady()) {
			try {
				Thread.sleep(50);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loop() {
		lastTickTimeNano = System.nanoTime();
		lastFullTickNano = lastTickTimeNano;
		while (!graphic.shouldClose()) {
			Game.getInstance().getGraphic().getKeyCallback().dispatchAllActiveKeyEvents();
			sync();
		}
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
		}
		else {
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
