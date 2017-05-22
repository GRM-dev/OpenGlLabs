package eu.grmdev.senryaku.game.data;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.util.*;

import eu.grmdev.senryaku.Main;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.events.listeners.GameEventListener;
import eu.grmdev.senryaku.core.handlers.EventHandler;

public class KeyEventListenersData {
	private static boolean initialized;
	private static List<GameEventListener> keyEvents = new LinkedList<>(Arrays.asList((GameEventListener) event -> {
		{
			KeyEvent ke = (KeyEvent) event;
			if (ke.getKey() == GLFW_KEY_ESCAPE) {
				Main.closeApp();
			}
		}
	}));
	
	public static void init(EventHandler handler) {
		if (!initialized) {
			for (GameEventListener listener : keyEvents) {
				handler.addKeyEventListener(listener);
			}
		}
	}
}
