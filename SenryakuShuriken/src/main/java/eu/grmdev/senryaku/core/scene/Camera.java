package eu.grmdev.senryaku.core.scene;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.camera.ArcBallCamera;
import org.lwjgl.BufferUtils;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.graphic.GameWindow;
import lombok.Getter;

public class Camera {
	public Vector4f point = new Vector4f(0, 0, 10, 20);
	private GameWindow gWindow;
	@Getter
	private ArcBallCamera cam;
	private Matrix4f mat;
	private FloatBuffer fb;
	
	public Camera() {
		mat = new Matrix4f();
		EventHandler h = Game.getInstance().getEventHandler();
		h.addKeyEventListener(event -> {
			KeyEvent e = (KeyEvent) event;
			int key = e.getKey();
			switch (key) {
				case GLFW_KEY_UP :
					point.z -= 1.0f;
					break;
				case GLFW_KEY_DOWN :
					point.z += 1.0f;
					break;
				case GLFW_KEY_LEFT :
					point.x -= 1.0f;
					break;
				case GLFW_KEY_RIGHT :
					point.x += 1.0f;
					break;
				case GLFW_KEY_PAGE_UP :
					point.w -= 1.0f;
					break;
				case GLFW_KEY_PAGE_DOWN :
					point.w += 1.0f;
					break;
				default :
					break;
			}
			moveCameraTo(point.x, point.y, point.z);
		});
	}
	
	public void init(GameWindow gWindow) {
		this.gWindow = gWindow;
		cam = new ArcBallCamera();
		cam.setAlpha((float) Math.toRadians(-20));
		cam.setBeta((float) Math.toRadians(20));
		fb = BufferUtils.createFloatBuffer(16);
		moveCameraTo(point.x, point.y, point.z);
	}
	
	public void update(float diff, int width, int height) {
		cam.zoom(point.w);
		cam.update(diff);
		glViewport(0, 0, width, height);
		mat.setPerspective((float) Math.atan((32.5 * height / 1200) / 60.0), (float) width / height, 0.01f, 100.0f).get(fb);
		glMatrixMode(GL_PROJECTION);
		glLoadMatrixf(fb);
		cam.viewMatrix(mat.identity()).get(fb);
		// float aspect = (float) width / height;
		// glLoadIdentity();
		// glOrtho(-aspect, aspect, -1, 1, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadMatrixf(fb);
		// glLoadIdentity();
	}
	
	public void translateToCamCenter() {
		mat.translate(cam.centerMover.target).get(fb);
	}
	
	public void moveCameraTo(float x, float y, float z) {
		cam.center(x, y, z);
	}
	
	public void resetRenderPos() {
		glLoadMatrixf(fb);
	}
}
