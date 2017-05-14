package eu.grmdev.senryaku.core.events;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import java.util.function.Function;

import eu.grmdev.senryaku.core.events.listeners.GameEventListener;
import lombok.Getter;
import lombok.Setter;

public abstract class GameEvent {
	@Getter
	@Setter
	private boolean consumed;
	@Getter
	private boolean remove;
	@Getter
	protected boolean repeatable;
	private Function<GameEvent, Boolean> repeatingCondition;
	@Getter
	private double creationTime;
	
	protected GameEvent(boolean repeatable, Function<GameEvent, Boolean> repeatingCondition) {
		this.repeatable = repeatable;
		this.repeatingCondition = repeatingCondition;
		this.creationTime = glfwGetTime();
	}
	
	public void dispatch(GameEventListener listener) {
		if (listener == null) { throw new NullPointerException("Null listener tried to invoke event!"); }
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
}
