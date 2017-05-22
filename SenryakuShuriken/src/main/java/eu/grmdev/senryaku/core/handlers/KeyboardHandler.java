package eu.grmdev.senryaku.core.handlers;

import static org.lwjgl.glfw.GLFW.*;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFWKeyCallback;

import eu.grmdev.senryaku.core.events.KeyEvent;

public class KeyboardHandler extends GLFWKeyCallback {
	private static boolean[] keys = new boolean[65536];
	private static List<Integer> pressedKeys = new LinkedList<>();
	private EventHandler eHandler;
	
	public KeyboardHandler(EventHandler eHandler) {
		this.eHandler = eHandler;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		boolean isDown = action != GLFW_RELEASE;
		boolean wasDown = isKeyDown(key);
		keys[key] = isDown;
		if (isDown) {
			if (!wasDown) {
				KeyEvent event = new KeyEvent(key, action);
				eHandler.dispatchKeyEvent(event);
				synchronized (pressedKeys) {
					pressedKeys.add(key);
				}
			}
		} else {
			synchronized (pressedKeys) {
				pressedKeys.remove(new Integer(key));
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
		if (!pressedKeys.isEmpty()) {
			synchronized (pressedKeys) {
				for (int key : pressedKeys) {
					KeyEvent event = new KeyEvent(key, GLFW_PRESS, true);
					eHandler.dispatchKeyEvent(event);
				}
			}
		}
	}
}
