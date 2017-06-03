package eu.grmdev.senryaku.core.events;

import eu.grmdev.senryaku.core.IGame;
import lombok.Getter;

public class KeyEvent extends GameEvent {
	private @Getter int key;
	private @Getter int action;
	public static final byte RELEASED = 0;
	public static final byte PRESSED = 1;
	public static final byte REPEAT = 2;
	
	public KeyEvent(int key, byte action, boolean repeated, IGame game) {
		this(key, action, game);
		this.repeatable = repeated;
	}
	
	public KeyEvent(int key, byte action, IGame game) {
		super(false, null, game);
		this.key = key;
		this.action = action;
	}
	
}
