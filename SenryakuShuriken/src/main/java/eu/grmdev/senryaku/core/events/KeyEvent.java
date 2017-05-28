package eu.grmdev.senryaku.core.events;

import lombok.Getter;

public class KeyEvent extends GameEvent {
	private @Getter int key;
	private @Getter int action;
	public static final byte RELEASED = 1;
	public static final byte PRESSED = 2;
	private @Getter byte keyType;
	
	public KeyEvent(int key, int action, boolean repeated, byte keyType) {
		this(key, action, keyType);
		this.repeatable = repeated;
	}
	
	public KeyEvent(int key, int action, byte keyType) {
		super(false, null);
		this.key = key;
		this.action = action;
		this.keyType = keyType;
	}
	
}
