package eu.grmdev.senryaku.core.entity;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.core.events.listeners.GameEventListener;
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
	protected Transformation transformation;
	protected boolean attachedToCamera;
	protected Mesh mesh;
	
	public Entity() {
		transformation = new Transformation();
		mesh = new Mesh();
	}
	
	public abstract void init();
	
	public void render(GameWindow window) {
		if (!initialized) {
			init();
		}
		transformation.transform();
		mesh.bind();
		draw(window);
		mesh.unbind();
	}
	
	protected abstract void draw(GameWindow window);
	
	protected void addKeyListener(GameEventListener listener) {
		Game.getInstance().getEventHandler().addKeyEventListener(listener);
	}
	
	public void delete() {
		mesh.delete();
	}
}
