package eu.grmdev.senryaku.core.events.listeners;

import java.util.EventListener;

import eu.grmdev.senryaku.core.events.GameEvent;

public interface GameEventListener extends EventListener {
	
	public void actionPerformed(GameEvent event);
}
