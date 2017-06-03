package eu.grmdev.senryaku.core.events;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import java.util.function.Function;

import eu.grmdev.senryaku.core.IGame;
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
	private @Getter IGame game;
	
	protected GameEvent(Window window, IGame game) {
		this(true, null, game);
		this.window = window;
	}
	
	protected GameEvent(boolean repeatable, Function<GameEvent, Boolean> repeatingCondition, IGame game) {
		this.repeatable = repeatable;
		this.repeatingCondition = repeatingCondition;
		this.game = game;
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
	}
	
	public void reset() {
		consumed = false;
		remove = false;
	}
}
