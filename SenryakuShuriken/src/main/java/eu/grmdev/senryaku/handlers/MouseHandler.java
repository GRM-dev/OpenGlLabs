package eu.grmdev.senryaku.handlers;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MouseHandler extends GLFWCursorPosCallback {

	@Override
	public void invoke(long window, double xpos, double ypos) {
		// TODO Auto-generated method stub
		// this basically just prints out the X and Y coordinates
		// of our mouse whenever it is in our window
		System.out.println("X: " + xpos + " Y: " + ypos);
	}
}
