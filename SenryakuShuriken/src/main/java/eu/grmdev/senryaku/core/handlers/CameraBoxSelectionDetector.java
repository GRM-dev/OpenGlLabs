package eu.grmdev.senryaku.core.handlers;

import java.util.List;

import org.joml.*;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.graphic.Camera;

/**
 * Selects objects in the center of camera view
 */
public class CameraBoxSelectionDetector {
	private final Vector3f max;
	private final Vector3f min;
	private final Vector2f nearFar;
	private Vector3f dir;
	private Entity selectedEntity;
	
	public CameraBoxSelectionDetector() {
		dir = new Vector3f();
		min = new Vector3f();
		max = new Vector3f();
		nearFar = new Vector2f();
	}
	
	public void selectEntity(Entity[] entities, Camera camera) {
		dir = camera.getViewMatrix().positiveZ(dir).negate();
		selectEntity(entities, camera.getPosition(), dir);
	}
	
	protected Entity selectEntity(Entity[] entities, Vector3f center, Vector3f dir) {
		selectedEntity = null;
		float closestDistance = Float.POSITIVE_INFINITY;
		for (int i = 0; i < entities.length; i++) {
			Entity entity = entities[i];
			closestDistance = selectEntity(center, dir, closestDistance, entity);
		}
		markSelected();
		return selectedEntity;
	}
	
	protected Entity selectClosestEntity(List<Entity> entities, Vector3f center, Vector3f dir) {
		selectedEntity = null;
		float closestDistance = Float.POSITIVE_INFINITY;
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			closestDistance = selectEntity(center, dir, closestDistance, entity);
		}
		markSelected();
		return selectedEntity;
	}
	
	private void markSelected() {
		if (selectedEntity != null) {
			selectedEntity.setSelected(true);
		}
	}
	
	private float selectEntity(Vector3f center, Vector3f dir, float closestDistance, Entity entity) {
		if (entity != null) {
			entity.setSelected(false);
			min.set(entity.getPosition());
			max.set(entity.getPosition());
			min.sub(entity.getScale(), entity.getScale(), entity.getScale());
			max.add(entity.getScale() / 2, entity.getScale() / 2, entity.getScale() / 2);
			if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < closestDistance) {
				closestDistance = nearFar.x;
				selectedEntity = entity;
			}
		}
		return closestDistance;
	}
}
