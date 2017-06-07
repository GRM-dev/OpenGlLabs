package eu.grmdev.senryaku.core.handlers;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFWKeyCallback;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.events.KeyEvent;

public class KeyboardHandler extends GLFWKeyCallback {
	private static boolean[] keys = new boolean[65536];
	private static List<Integer> pressedKeys = new LinkedList<>();
	private EventHandler eHandler;
	private IGame game;
	
	public KeyboardHandler(EventHandler eHandler, IGame game) {
		this.eHandler = eHandler;
		this.game = game;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key == -1) { return; }
		boolean isDown = action != GLFW_RELEASE;
		boolean wasDown = isKeyDown(key);
		keys[key] = isDown;
		if (isDown) {
			if (!wasDown) {
				KeyEvent event = new KeyEvent(key, KeyEvent.PRESSED, game);
				eHandler.dispatchKeyEvent(event);
				synchronized (pressedKeys) {
					pressedKeys.add(key);
				}
			}
		} else {
			if (wasDown) {
				synchronized (pressedKeys) {
					pressedKeys.remove(new Integer(key));
					KeyEvent event = new KeyEvent(key, KeyEvent.RELEASED, game);
					eHandler.dispatchKeyEvent(event);
					if (!event.isRemove()) {
						eHandler.dispatchHudKeyEvent(event);
					}
				}
			}
		}
	}
	
	/**
	 * method that returns true if a given key
	 * is pressed from {@link #keys} array
	 * 
	 * @return true if key is pressed
	 */
	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}
	
	public synchronized void dispatchAllActiveKeyEvents() {
		synchronized (pressedKeys) {
			if (!pressedKeys.isEmpty()) {
				for (int key : pressedKeys) {
					KeyEvent event = new KeyEvent(key, KeyEvent.REPEAT, true, game);
					eHandler.dispatchKeyEvent(event);
					if (event.isRemove()) {
						continue;
					}
					eHandler.dispatchHudKeyEvent(event);
				}
			}
		}
	}
}
