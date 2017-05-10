package eu.grmdev.senryaku;

import eu.grmdev.senryaku.core.LogicThread;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.data.KeyEventListenersData;
import eu.grmdev.senryaku.graphic.GameWindow;
import lombok.Getter;

public class Game {
	@Getter
	private static Game instance;
	@Getter
	private GameWindow graphic;
	@Getter
	private LogicThread logicThread;
	@Getter
	private EventHandler eventHandler;
	
	public Game() {
		instance = this;
		eventHandler = new EventHandler();
		graphic = new GameWindow();
		logicThread = new LogicThread(graphic, eventHandler);
		KeyEventListenersData.init(eventHandler);
	}
	
	/**
	 * Starts the game
	 */
	public void start() {
		graphic.start();
		logicThread.start();
	}
	
	/**
	 * Stops the game
	 */
	public void stop() {
		graphic.setClose();
	}
}
