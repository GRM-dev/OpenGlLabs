package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.graphic.GameWindow;

public class LogicThread extends Thread {
	private GameWindow graphic;
	private EventHandler eventHandler;
	
	public LogicThread(GameWindow graphic, EventHandler eventHandler) {
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
