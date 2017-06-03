package eu.grmdev.senryaku.core.events.listeners;

import java.util.EventListener;

import eu.grmdev.senryaku.core.events.MouseEvent;

public interface MouseEventListener extends EventListener {
	public void actionPerformed(MouseEvent event);
}
