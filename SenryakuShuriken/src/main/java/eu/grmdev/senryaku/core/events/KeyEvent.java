package eu.grmdev.senryaku.core.events;

import lombok.Getter;

public class KeyEvent extends GameEvent {
	private @Getter int key;
	private @Getter int action;
	public static final byte RELEASED = 0;
	public static final byte PRESSED = 1;
	public static final byte REPEAT = 2;
	
	public KeyEvent(int key, byte action, boolean repeated) {
		this(key, action);
		this.repeatable = repeated;
	}
	
	public KeyEvent(int key, byte action) {
		super(false, null);
		this.key = key;
		this.action = action;
	}
	
}
