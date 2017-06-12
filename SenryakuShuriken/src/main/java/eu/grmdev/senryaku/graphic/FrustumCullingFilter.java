package eu.grmdev.senryaku.graphic;

import java.util.List;
import java.util.Map;

import org.joml.*;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.graphic.mesh.Mesh;

public class FrustumCullingFilter {
	private final Matrix4f prjViewMatrix;
	private final FrustumIntersection frustumInt;
	
	public FrustumCullingFilter() {
		prjViewMatrix = new Matrix4f();
		frustumInt = new FrustumIntersection();
	}
	
	public void updateFrustum(Matrix4f projMatrix, Matrix4f viewMatrix) {
		prjViewMatrix.set(projMatrix);
		prjViewMatrix.mul(viewMatrix);
		frustumInt.set(prjViewMatrix);
	}
	
	public void filter(Map<? extends Mesh, List<Entity>> mapMesh) {
		for (Map.Entry<? extends Mesh, List<Entity>> entry : mapMesh.entrySet()) {
			List<Entity> gameItems = entry.getValue();
			filter(gameItems, entry.getKey().getBoundingRadius());
		}
	}
	
	public void filter(List<Entity> gameItems, float meshBoundingRadius) {
		float boundingRadius;
		Vector3f pos;
		for (Entity gameItem : gameItems) {
			if (!gameItem.isDisableFrustumCulling()) {
				boundingRadius = gameItem.getScale() * meshBoundingRadius;
				pos = gameItem.getPosition();
				gameItem.setInsideFrustum(insideFrustum(pos.x, pos.y, pos.z, boundingRadius));
			}
		}
	}
	
	public boolean insideFrustum(float x0, float y0, float z0, float boundingRadius) {
		return frustumInt.testSphere(x0, y0, z0, boundingRadius);
	}
}
