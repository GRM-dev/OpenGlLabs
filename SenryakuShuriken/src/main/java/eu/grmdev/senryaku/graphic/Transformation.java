package eu.grmdev.senryaku.graphic;

import java.lang.Math;

import org.joml.*;

import eu.grmdev.senryaku.core.entity.Entity;
import lombok.Getter;

public class Transformation {
	private final Matrix4f modelMatrix;
	private final Matrix4f modelViewMatrix;
	private final Matrix4f modelLightViewMatrix;
	private @Getter final Matrix4f lightViewMatrix;
	private final Matrix4f ortho2DMatrix;
	private final Matrix4f orthoModelMatrix;
	
	public Transformation() {
		modelMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
		modelLightViewMatrix = new Matrix4f();
		ortho2DMatrix = new Matrix4f();
		orthoModelMatrix = new Matrix4f();
		lightViewMatrix = new Matrix4f();
	}
	
	public void setLightViewMatrix(Matrix4f lightViewMatrix) {
		this.lightViewMatrix.set(lightViewMatrix);
	}
	
	public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
		return updateGenericViewMatrix(position, rotation, lightViewMatrix);
	}
	
	public static Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
		return matrix.rotationX((float) Math.toRadians(rotation.x)).rotateY((float) Math.toRadians(rotation.y)).translate(-position.x, -position.y, -position.z);
	}
	
	public static Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f posOffset, Vector3f rotation, Matrix4f matrix) {
		return matrix.rotationX((float) Math.toRadians(rotation.x)).rotateY((float) Math.toRadians(rotation.y)).translate(-position.x - posOffset.x, -position.y - posOffset.y, -position.z - posOffset.z);
	}
	
	public final Matrix4f getOrtho2DProjectionMatrix(float left, float right, float bottom, float top) {
		return ortho2DMatrix.setOrtho2D(left, right, bottom, top);
	}
	
	public Matrix4f buildModelMatrix(Entity entity) {
		Quaternionf rotation = entity.getRotation();
		float tx = entity.getPosition().x + entity.getRenderOffset().x;
		float ty = entity.getPosition().y + entity.getRenderOffset().y;
		float tz = entity.getPosition().z + entity.getRenderOffset().z;
		return modelMatrix.translationRotateScale(tx, ty, tz, rotation.x, rotation.y, rotation.z, rotation.w, entity.getScale(), entity.getScale(), entity.getScale());
	}
	
	public Matrix4f buildModelViewMatrix(Entity entity, Matrix4f viewMatrix) {
		return buildModelViewMatrix(buildModelMatrix(entity), viewMatrix);
	}
	
	public Matrix4f buildModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
		return viewMatrix.mulAffine(modelMatrix, modelViewMatrix);
	}
	
	public Matrix4f buildModelLightViewMatrix(Entity entity, Matrix4f lightViewMatrix) {
		return buildModelViewMatrix(buildModelMatrix(entity), lightViewMatrix);
	}
	
	public Matrix4f buildModelLightViewMatrix(Matrix4f modelMatrix, Matrix4f lightViewMatrix) {
		return lightViewMatrix.mulAffine(modelMatrix, modelLightViewMatrix);
	}
	
	public Matrix4f buildOrthoProjModelMatrix(Entity entity, Matrix4f orthoMatrix) {
		return orthoMatrix.mulOrthoAffine(buildModelMatrix(entity), orthoModelMatrix);
	}
}
