package eu.grmdev.senryaku;

import java.util.*;

import eu.grmdev.senryaku.core.LogicThread;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.tile.World;
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
	private Set<Entity> entities;
	private Queue<Entity> toAddEntities = new PriorityQueue<>();
	private Queue<Entity> toRemoveEntities = new PriorityQueue<>();
	@Getter
	private World world;
	
	public Game() {
		instance = this;
		entities = new LinkedHashSet<>();
		eventHandler = new EventHandler();
		graphic = new GameWindow(this);
		logicThread = new LogicThread(this, graphic, eventHandler);
		KeyEventListenersData.init(eventHandler);
		world = new World();
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
	
	public synchronized Iterator<Entity> getEntityIterator() {
		parseEntities();
		return entities.iterator();
	}
	
	private void parseEntities() {
		if (toAddEntities.size() > 0) {
			Entity entity = toAddEntities.poll();
			while (entity != null) {
				entities.add(entity);
				entity = toAddEntities.poll();
			}
		}
		if (toRemoveEntities.size() > 0) {
			Entity entity = toRemoveEntities.poll();
			while (entity != null) {
				entities.remove(entity);
				entity = toRemoveEntities.poll();
			}
		}
	}
	
	public synchronized void addEntity(Entity entity) {
		toAddEntities.add(entity);
	}
	
	public synchronized void removeEntity(Entity entity) {
		toRemoveEntities.remove(entity);
	}
}
