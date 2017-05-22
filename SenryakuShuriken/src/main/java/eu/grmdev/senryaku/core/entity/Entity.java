package eu.grmdev.senryaku.core.entity;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import eu.grmdev.senryaku.graphic.Mesh;
import lombok.Getter;
import lombok.Setter;

public class Entity {
	private @Getter @Setter boolean selected;
	protected @Getter @Setter Mesh[] meshes;
	private @Getter final Vector3f position;
	private @Getter @Setter float scale;
	private @Getter final Quaternionf rotation;
	private @Getter @Setter int textPos;
	private @Getter @Setter boolean disableFrustumCulling;
	private @Getter @Setter boolean insideFrustum;
	
	public Entity() {
		selected = false;
		position = new Vector3f(0, 0, 0);
		scale = 1;
		rotation = new Quaternionf();
		textPos = 0;
		insideFrustum = true;
		disableFrustumCulling = false;
	}
	
	public Entity(Mesh mesh) {
		this();
		this.meshes = new Mesh[]{mesh};
	}
	
	public Entity(Mesh[] meshes) {
		this();
		this.meshes = meshes;
	}
	
	public final void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}
	
	public final void setRotation(Quaternionf q) {
		this.rotation.set(q);
	}
	
	public Mesh getMesh() {
		return meshes[0];
	}
	
	public void setMesh(Mesh mesh) {
		this.meshes = new Mesh[]{mesh};
	}
	
	public void cleanup() {
		int numMeshes = this.meshes != null ? this.meshes.length : 0;
		for (int i = 0; i < numMeshes; i++) {
			this.meshes[i].remove();
		}
	}
}
