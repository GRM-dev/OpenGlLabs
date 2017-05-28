package eu.grmdev.senryaku.core.events;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import java.util.function.Function;

import eu.grmdev.senryaku.graphic.Window;
import lombok.Getter;
import lombok.Setter;

public abstract class GameEvent {
	private @Setter @Getter boolean consumed;
	private @Getter boolean remove;
	protected @Getter boolean repeatable;
	private Function<GameEvent, Boolean> repeatingCondition;
	private @Getter double creationTime;
	private @Getter Window window;
	
	protected GameEvent(Window window) {
		this(true, null);
		this.window = window;
	}
	
	protected GameEvent(boolean repeatable, Function<GameEvent, Boolean> repeatingCondition) {
		this.repeatable = repeatable;
		this.repeatingCondition = repeatingCondition;
		this.creationTime = glfwGetTime();
	}
	
	public void dispatch() {
		if (consumed) {
			remove = true;
			return;
		}
		if (repeatable && repeatingCondition != null && !repeatingCondition.apply(this)) {
			repeatable = false;
		}
		if (!repeatable) {
			consumed = true;
		}
	}
	
	public void reset() {
		consumed = false;
		remove = false;
	}
}
