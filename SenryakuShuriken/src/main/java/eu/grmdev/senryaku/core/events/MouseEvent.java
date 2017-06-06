package eu.grmdev.senryaku.core.events;

import org.joml.Vector2d;
import org.joml.Vector3f;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.handlers.MouseBoxSelectionDetector;
import lombok.Getter;

public class MouseEvent extends GameEvent {
	private @Getter byte key;
	private @Getter int action;
	private @Getter Vector2d pos;
	private MouseBoxSelectionDetector detector;
	public static final byte RELEASED = 0;
	public static final byte PRESSED = 1;
	public static final byte REPEAT = 2;
	public static final byte LEFT_KEY = 0;
	public static final byte RIGHT_KEY = 1;
	public static final byte MIDDLE_KEY = 2;
	
	public MouseEvent(byte key, byte action, Vector2d pos, boolean repeated, IGame game, MouseBoxSelectionDetector detector) {
		this(key, action, pos, game, detector);
		this.repeatable = repeated;
	}
	
	public MouseEvent(byte key, byte action, Vector2d pos, IGame game, MouseBoxSelectionDetector detector) {
		super(false, null, game);
		this.key = key;
		this.action = action;
		this.pos = pos;
		this.detector = detector;
	}
	
	public boolean isLeftButtonClicked() {
		return key == LEFT_KEY && action == PRESSED;
	}
	
	public boolean isRightButtonClicked() {
		return key == RIGHT_KEY && action == PRESSED;
	}
	
	public Vector3f getClickDestination() {
		Vector3f clickDestination = detector.getClickDestination(getGame().getCamera().getWindow(), pos, getGame().getCamera());
		return clickDestination;
	}
}
