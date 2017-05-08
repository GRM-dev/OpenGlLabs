package eu.grmdev.senryaku;

import eu.grmdev.senryaku.core.LogicThread;
import eu.grmdev.senryaku.graphic.Window;

public class Game {
	private static Game instance;
	private Window graphic;
	private LogicThread logicThread;
	
	/**
	 * Starts the game
	 */
	public void start() {
		instance = this;
		graphic = new Window();
		logicThread = new LogicThread(graphic);
		graphic.start();
		logicThread.start();
	}
	
	/**
	 * Stops the game
	 */
	public void stop() {
		
	}
	
	public static Game getInstance() {
		return instance;
	}
}
