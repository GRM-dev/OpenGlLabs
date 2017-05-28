package eu.grmdev.senryaku.core.handlers;

import java.util.LinkedList;
import java.util.List;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.events.GameEvent;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.events.listeners.GameEventListener;
import eu.grmdev.senryaku.core.events.listeners.KeyEventListener;

public class EventHandler {
	private List<KeyEventListener> keyEventsListeners = new LinkedList<>();
	private List<KeyEventListener> hudKeyEventsListeners = new LinkedList<>();
	private List<GameEventListener> tickGameEventsListeners = new LinkedList<>();
	private List<GameEventListener> cycleGameEventsListeners = new LinkedList<>();
	private IGame game;
	
	public EventHandler(IGame game) {
		this.game = game;
	}
	
	public synchronized void dispatchKeyEvent(KeyEvent event) {
		if (!game.isPaused() && !keyEventsListeners.isEmpty()) {
			for (KeyEventListener gEl : keyEventsListeners) {
				gEl.actionPerformed(event);
			}
		}
	}
	
	public synchronized void dispatchHudKeyEvent(KeyEvent event) {
		if (game.isPaused() && !hudKeyEventsListeners.isEmpty()) {
			for (KeyEventListener gEl : hudKeyEventsListeners) {
				gEl.actionPerformed(event);
			}
		}
	}
	
	public synchronized void dispatchTickGameEvent(GameEvent event) {
		if (!tickGameEventsListeners.isEmpty()) {
			for (GameEventListener gEl : tickGameEventsListeners) {
				gEl.actionPerformed(event);
				event.dispatch(gEl);
			}
		}
	}
	
	public synchronized void dispatchCycleGameEvent(GameEvent event) {
		if (!cycleGameEventsListeners.isEmpty()) {
			for (GameEventListener gEl : cycleGameEventsListeners) {
				gEl.actionPerformed(event);
				event.dispatch(gEl);
			}
		}
	}
	
	public void addKeyEventListener(KeyEventListener listener) {
		if (!keyEventsListeners.contains(listener)) {
			keyEventsListeners.add(listener);
		}
	}
	
	public void addHudKeyEventListener(KeyEventListener listener) {
		if (!hudKeyEventsListeners.contains(listener)) {
			hudKeyEventsListeners.add(listener);
		}
	}
	
	public void addTickGameEventListener(GameEventListener listener) {
		if (!tickGameEventsListeners.contains(listener)) {
			tickGameEventsListeners.add(listener);
		}
	}
	
	public void addCycleGameEventListener(GameEventListener listener) {
		if (!cycleGameEventsListeners.contains(listener)) {
			cycleGameEventsListeners.add(listener);
		}
	}
}
