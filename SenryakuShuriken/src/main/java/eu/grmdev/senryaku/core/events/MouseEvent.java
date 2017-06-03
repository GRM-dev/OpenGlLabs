package eu.grmdev.senryaku.core.events;

import org.joml.Vector2d;

import lombok.Getter;

public class MouseEvent extends GameEvent {
	private @Getter byte key;
	private @Getter int action;
	private @Getter Vector2d pos;
	public static final byte RELEASED = 0;
	public static final byte PRESSED = 1;
	public static final byte REPEAT = 2;
	public static final byte LEFT_KEY = 0;
	public static final byte RIGHT_KEY = 1;
	public static final byte MIDDLE_KEY = 2;
	
	public MouseEvent(byte key, byte action, Vector2d pos, boolean repeated) {
		this(key, action, pos);
		this.repeatable = repeated;
	}
	
	public MouseEvent(byte key, byte action, Vector2d pos) {
		super(false, null);
		this.key = key;
		this.action = action;
		this.pos = pos;
	}
}
