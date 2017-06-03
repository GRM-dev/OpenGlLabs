package eu.grmdev.senryaku.core.handlers;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.events.MouseEvent;
import eu.grmdev.senryaku.graphic.Window;
import lombok.Getter;

public class MouseHandler implements GLFWMouseButtonCallbackI {
	private final Vector2d previousPos;
	private @Getter final Vector2d currentPos;
	private @Getter final Vector2f displVec;
	private boolean inWindow = false;
	private @Getter boolean leftButtonPressed = false;
	private @Getter boolean rightButtonPressed = false;
	private MouseBoxSelectionDetector detector;
	private Window window;
	private IGame game;
	private EventHandler eHandler;
	
	public MouseHandler(IGame game, EventHandler eHandler) {
		this.game = game;
		this.eHandler = eHandler;
		previousPos = new Vector2d(-1, -1);
		currentPos = new Vector2d(0, 0);
		displVec = new Vector2f();
		detector = new MouseBoxSelectionDetector();
	}
	
	public void init(Window window) {
		this.window = window;
		glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
			currentPos.x = xpos;
			currentPos.y = ypos;
		});
		glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
			inWindow = entered;
		});
		// eHandler.addMouseEventListener(event -> {
		// if (event.getKey() == MouseEvent.LEFT_KEY && event.getAction() ==
		// MouseEvent.PRESSED) {
		// Entity entity =
		// detector.selectTile(LevelManager.getInstance().getCurrentMap(), window,
		// currentPos, this.window.getCamera());
		// if (entity != null) {
		// System.out.println(entity.getPosition().toString(NumberFormat.getIntegerInstance()));
		// }
		// }
		// });
	}
	
	@Override
	public void invoke(long window, int button, int action, int mods) {
		leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
		rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
		switch (button) {
			case GLFW_MOUSE_BUTTON_1 :
				switch (action) {
					case GLFW_PRESS :
						createEvent(MouseEvent.LEFT_KEY, MouseEvent.PRESSED);
						break;
					case GLFW_REPEAT :
						createEvent(MouseEvent.LEFT_KEY, MouseEvent.REPEAT);
						break;
					case GLFW_RELEASE :
						createEvent(MouseEvent.LEFT_KEY, MouseEvent.RELEASED);
						break;
				}
				break;
			case GLFW_MOUSE_BUTTON_2 :
				switch (action) {
					case GLFW_PRESS :
						createEvent(MouseEvent.RIGHT_KEY, MouseEvent.PRESSED);
						break;
					case GLFW_REPEAT :
						createEvent(MouseEvent.RIGHT_KEY, MouseEvent.REPEAT);
						break;
					case GLFW_RELEASE :
						createEvent(MouseEvent.RIGHT_KEY, MouseEvent.RELEASED);
						break;
				}
				break;
		}
	}
	
	private void createEvent(byte key, byte action) {
		MouseEvent event = new MouseEvent(key, action, currentPos, game, detector);
		eHandler.dispatchMouseEvent(event);
	}
	
	public void input(Window window) {
		displVec.x = 0;
		displVec.y = 0;
		if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
			double deltax = currentPos.x - previousPos.x;
			double deltay = currentPos.y - previousPos.y;
			boolean rotateX = deltax != 0;
			boolean rotateY = deltay != 0;
			if (rotateX) {
				displVec.y = (float) deltax;
			}
			if (rotateY) {
				displVec.x = (float) deltay;
			}
		}
		previousPos.x = currentPos.x;
		previousPos.y = currentPos.y;
	}
}
