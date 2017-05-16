package eu.grmdev.senryaku.game;

import org.joml.*;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.graphic.Camera;

public class CameraBoxSelectionDetector {
	private final Vector3f max;
	private final Vector3f min;
	private final Vector2f nearFar;
	private Vector3f dir;
	
	public CameraBoxSelectionDetector() {
		dir = new Vector3f();
		min = new Vector3f();
		max = new Vector3f();
		nearFar = new Vector2f();
	}
	
	public void selectGameItem(Entity[] gameItems, Camera camera) {
		dir = camera.getViewMatrix().positiveZ(dir).negate();
		selectGameItem(gameItems, camera.getPosition(), dir);
	}
	
	protected boolean selectGameItem(Entity[] gameItems, Vector3f center, Vector3f dir) {
		boolean selected = false;
		Entity selectedGameItem = null;
		float closestDistance = Float.POSITIVE_INFINITY;
		
		for (Entity gameItem : gameItems) {
			gameItem.setSelected(false);
			min.set(gameItem.getPosition());
			max.set(gameItem.getPosition());
			min.add(-gameItem.getScale(), -gameItem.getScale(), -gameItem.getScale());
			max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
			if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < closestDistance) {
				closestDistance = nearFar.x;
				selectedGameItem = gameItem;
			}
		}
		
		if (selectedGameItem != null) {
			selectedGameItem.setSelected(true);
			selected = true;
		}
		return selected;
	}
}
