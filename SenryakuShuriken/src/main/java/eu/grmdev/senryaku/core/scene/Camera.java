package eu.grmdev.senryaku.core.scene;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.joml.*;
import org.joml.Math;
import org.joml.camera.ArcBallCamera;
import org.lwjgl.BufferUtils;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.graphic.GameWindow;

public class Camera {
	public Vector4f point = new Vector4f(0, 0, 10, 20);
	private float fovy;
	private float beta;
	private float alpha;
	private GameWindow gWindow;
	private ArcBallCamera cam;
	private Matrix4f mat;
	private FloatBuffer fb;
	private int width;
	private int height;
	private int currentRenderPos;
	
	public Camera() {
		mat = new Matrix4f();
		alpha = (float) Math.toRadians(-20);
		beta = (float) Math.toRadians(20);
		EventHandler h = Game.getInstance().getEventHandler();
		h.addKeyEventListener(event -> {
			KeyEvent e = (KeyEvent) event;
			int key = e.getKey();
			float dv = 0.5f;
			float dq = 0.05f;
			switch (key) {// TODO:Limits
				case GLFW_KEY_UP :
					point.z -= dv;
					break;
				case GLFW_KEY_DOWN :
					point.z += dv;
					break;
				case GLFW_KEY_LEFT :
					point.x -= dv;
					break;
				case GLFW_KEY_RIGHT :
					point.x += dv;
					break;
				case GLFW_KEY_PAGE_UP :
					point.w -= dv;
					break;
				case GLFW_KEY_PAGE_DOWN :
					point.w += dv;
					break;
				case GLFW_KEY_Z :
					alpha -= dq;
					break;
				case GLFW_KEY_X :
					alpha += dq;
					break;
				case GLFW_KEY_C :
					beta -= dq;
					break;
				case GLFW_KEY_V :
					beta += dq;
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
		cam.setAlpha(alpha);
		cam.setBeta(beta);
		fb = BufferUtils.createFloatBuffer(16);
		moveCameraTo(point.x, point.y, point.z);
	}
	
	public void update(float diff, int width, int height) {
		this.width = width;
		this.height = height;
		fovy = (float) java.lang.Math.atan((32.5 * height / 1200) / 60.0);
		glViewport(0, 0, width, height);
		update(diff);
	}
	
	public void update(float diff) {
		cam.zoom(point.w);
		cam.setAlpha(alpha);
		cam.setBeta(beta);
		cam.update(diff);
		mat.setPerspective(fovy, (float) width / height, 0.01f, 100.0f).get(fb);
		glMatrixMode(GL_PROJECTION);
		glLoadMatrixf(fb);
		cam.viewMatrix(mat.identity()).get(fb);
		glMatrixMode(GL_MODELVIEW);
		glLoadMatrixf(fb);
	}
	
	public void translateTo(Vector3f v) {
		mat.translate(v).get(fb);
		glLoadMatrixf(fb);
	}
	
	public void translateToCamCenter() {
		mat.translate(cam.centerMover.target).get(fb);
		glLoadMatrixf(fb);
		System.out.println("Cam");
	}
	
	public void translateToWorldCenter() {
		mat.translate(cam.centerMover.target.negate()).get(fb);
		glLoadMatrixf(fb);
		System.out.println("World");
	}
	
	public void moveCameraTo(float x, float y, float z) {
		cam.center(x, y, z);
	}
}
