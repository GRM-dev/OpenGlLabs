package eu.grmdev.senryaku.core.handlers;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MouseHandler extends GLFWCursorPosCallback {
	
	@Override
	public void invoke(long window, double xpos, double ypos) {
		// System.out.println("X: " + xpos + " Y: " + ypos);
	}
}
