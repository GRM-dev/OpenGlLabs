package eu.grmdev.senryaku.core.entity;

import eu.grmdev.senryaku.graphic.GameWindow;
import eu.grmdev.senryaku.graphic.VertexArrayObject;
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
	protected VertexArrayObject vao;
	protected VertexBufferObject vbo;
	
	public abstract void init();
	
	public void render(GameWindow window) {
		if (!initialized) {
			init();
		}
		vao.bind();
		draw(window);
	}
	
	protected abstract void draw(GameWindow window);
	
	public void delete() {
		if (vbo != null) {
			vbo.remove();
		}
		if (vao != null) {
			vao.remove();
		}
	}
}
