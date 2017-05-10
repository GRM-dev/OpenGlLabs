package eu.grmdev.senryaku.core.handlers;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import org.lwjgl.glfw.GLFWKeyCallback;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.core.events.KeyEvent;

public class KeyboardHandler extends GLFWKeyCallback {
	
	public static boolean[] keys = new boolean[65536];
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		boolean isDown = action != GLFW_RELEASE;
		keys[key] = isDown;
		if (isDown) {
			KeyEvent event = new KeyEvent(key, action);
			Game.getInstance().getEventHandler().dispatchKeyEvent(event);
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
}
