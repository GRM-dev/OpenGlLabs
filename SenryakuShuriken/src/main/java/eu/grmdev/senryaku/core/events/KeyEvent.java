package eu.grmdev.senryaku.core.events;

import lombok.Getter;

public class KeyEvent extends GameEvent {
	@Getter
	private int key;
	@Getter
	private int action;
	
	public KeyEvent(int key, int action) {
		super(false, null);
		this.key = key;
		this.action = action;
		
	}
	
}
