package eu.grmdev.senryaku.game;

import org.joml.*;

import eu.grmdev.senryaku.core.Window;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.graphic.Camera;

public class MouseBoxSelectionDetector extends CameraBoxSelectionDetector {
	private final Matrix4f invProjectionMatrix;
	private final Matrix4f invViewMatrix;
	private final Vector3f mouseDir;
	private final Vector4f tmpVec;
	
	public MouseBoxSelectionDetector() {
		super();
		invProjectionMatrix = new Matrix4f();
		invViewMatrix = new Matrix4f();
		mouseDir = new Vector3f();
		tmpVec = new Vector4f();
	}
	
	public boolean selectGameItem(Entity[] gameItems, Window window, Vector2d mousePos, Camera camera) {
		// Transform mouse coordinates into normalized space [-1, 1]
		int wdwWitdh = window.getWidth();
		int wdwHeight = window.getHeight();
		
		float x = (float) (2 * mousePos.x) / wdwWitdh - 1.0f;
		float y = 1.0f - (float) (2 * mousePos.y) / wdwHeight;
		float z = -1.0f;
		
		invProjectionMatrix.set(window.getProjectionMatrix());
		invProjectionMatrix.invert();
		
		tmpVec.set(x, y, z, 1.0f);
		tmpVec.mul(invProjectionMatrix);
		tmpVec.z = -1.0f;
		tmpVec.w = 0.0f;
		
		Matrix4f viewMatrix = camera.getViewMatrix();
		invViewMatrix.set(viewMatrix);
		invViewMatrix.invert();
		tmpVec.mul(invViewMatrix);
		
		mouseDir.set(tmpVec.x, tmpVec.y, tmpVec.z);
		
		return selectGameItem(gameItems, camera.getPosition(), mouseDir);
	}
}