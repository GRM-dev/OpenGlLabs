package eu.grmdev.senryaku.core.handlers;

import java.util.LinkedList;
import java.util.List;

import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.events.listeners.GameEventListener;

public class EventHandler {
	private List<GameEventListener> keyEventsListeners = new LinkedList<>();
	
	public synchronized void dispatchKeyEvent(KeyEvent event) {
		if (!keyEventsListeners.isEmpty()) {
			for (GameEventListener gEl : keyEventsListeners) {
				gEl.actionPerformed(event);
			}
		}
	}
	
	public void addKeyEventListener(GameEventListener listener) {
		if (!keyEventsListeners.contains(listener)) {
			keyEventsListeners.add(listener);
		}
	}
}
