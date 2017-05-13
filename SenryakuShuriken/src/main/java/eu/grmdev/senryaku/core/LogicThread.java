package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.data.EntitiesData;
import eu.grmdev.senryaku.graphic.GameWindow;

public class LogicThread extends Thread {
	private GameWindow graphic;
	private EventHandler eventHandler;
	private Game game;
	
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
		while (!graphic.shouldClose()) {
			Game.getInstance().getGraphic().getKeyCallback().dispatchAllActiveKeyEvents();
		}
	}
}
