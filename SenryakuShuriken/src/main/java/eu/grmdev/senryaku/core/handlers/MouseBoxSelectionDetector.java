package eu.grmdev.senryaku.core.handlers;

import java.util.ArrayList;
import java.util.List;

import org.joml.*;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.map.GameMap;
import eu.grmdev.senryaku.graphic.Camera;
import eu.grmdev.senryaku.graphic.Window;

/**
 * Selects objects at position where mouse click event was fired (LMB clicked)
 */
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
	
	public Entity selectTile(GameMap map, Window window, Vector2d mousePos, Camera camera) {
		Entity[][] entities2 = map.getTerrain().getEntitiesByPos();
		List<Entity> entities = new ArrayList<>();
		if (entities2 != null) {
			for (int i = 0; i < entities2.length; i++) {
				Entity[] entities3 = entities2[i];
				for (int j = 0; j < entities3.length; j++) {
					Entity entity = entities3[j];
					if (entity != null) {
						entities.add(entity);
					} else {
						System.out.println("NULL entity at [" + i + " " + j + "]");
					}
				}
			}
			setMouseDir(window, mousePos, camera);
			return selectClosestEntity(entities, camera.getOffsetPosition(), mouseDir);
		}
		return null;
	}
	
	public Entity selectClosestEntity(List<Entity> entities, Window window, Vector2d mousePos, Camera camera) {
		setMouseDir(window, mousePos, camera);
		return selectClosestEntity(entities, camera.getOffsetPosition(), mouseDir);
	}
	
	public Entity selectClosestEntity(Entity[] entities, Window window, Vector2d mousePos, Camera camera) {
		setMouseDir(window, mousePos, camera);
		return selectEntity(entities, camera.getPosition(), mouseDir);
	}
	
	private void setMouseDir(Window window, Vector2d mousePos, Camera camera) {
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
	}
	
	public Vector3f getClickDestination(Window window, Vector2d mousePos, Camera camera) {
		setMouseDir(window, mousePos, camera);
		return mouseDir;
	}
}
