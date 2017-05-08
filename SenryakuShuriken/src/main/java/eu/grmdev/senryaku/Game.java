package eu.grmdev.senryaku;

import eu.grmdev.senryaku.core.LogicThread;
import eu.grmdev.senryaku.graphic.GameWindow;
import lombok.Getter;

public class Game {
	@Getter
	private static Game instance;
	private GameWindow graphic;
	private LogicThread logicThread;
	
	/**
	 * Starts the game
	 */
	public void start() {
		instance = this;
		graphic = new GameWindow();
		logicThread = new LogicThread(graphic);
		graphic.start();
		logicThread.start();
	}
	
	/**
	 * Stops the game
	 */
	public void stop() {
		
	}
}
