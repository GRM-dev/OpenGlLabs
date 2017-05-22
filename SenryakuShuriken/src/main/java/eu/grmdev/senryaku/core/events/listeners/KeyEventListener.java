package eu.grmdev.senryaku.core.events.listeners;

import java.util.EventListener;

import eu.grmdev.senryaku.core.events.KeyEvent;

public interface KeyEventListener extends EventListener {
	public void actionPerformed(KeyEvent event);
}
