package eu.grmdev.senryaku.core.entity;

import eu.grmdev.senryaku.graphic.GameWindow;
import lombok.AccessLevel;
import lombok.Setter;

/**
 * Base class for all game entities
 * 
 * @author Levvy
 */
public abstract class Entity {
	@Setter(value = AccessLevel.PROTECTED)
	private boolean initialized;
	
	public abstract void init();
	
	public void render(GameWindow window) {
		if (!initialized) {
			init();
		}
		draw(window);
	}
	
	protected abstract void draw(GameWindow window);
}
