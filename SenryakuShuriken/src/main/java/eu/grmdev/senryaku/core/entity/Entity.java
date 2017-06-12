package eu.grmdev.senryaku.core.entity;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.graphic.anim.TranslateAnimation;
import eu.grmdev.senryaku.graphic.mesh.Mesh;
import lombok.*;

public class Entity {
	private @Getter @Setter boolean selected;
	protected @Getter Mesh[] meshes;
	protected @Getter final Vector3f position;
	protected @Getter final Vector3f renderOffset;
	protected @Getter @Setter float scale;
	private @Getter final Quaternionf rotation;
	protected @Getter Direction direction;
	private @Getter @Setter int textPos;
	private @Getter @Setter boolean disableFrustumCulling;
	private @Getter @Setter boolean insideFrustum;
	protected TranslateAnimation tAnimation;
	private @Getter double creationTime;
	private @Getter(value = AccessLevel.PROTECTED) IGame game;
	
	public Entity(IGame game) {
		this.game = game;
		selected = false;
		position = new Vector3f(0, 0, 0);
		renderOffset = new Vector3f(0, 0, 0);
		rotation = new Quaternionf();
		direction = Direction.DOWN;
		tAnimation = new TranslateAnimation(position);
		creationTime = glfwGetTime();
		scale = 1;
		textPos = 0;
		insideFrustum = true;
		disableFrustumCulling = false;
	}
	
	public Entity(Mesh mesh, IGame game) {
		this(game);
		this.meshes = new Mesh[]{mesh};
	}
	
	public Entity(Mesh[] meshes, IGame game) {
		this(game);
		this.meshes = meshes;
	}
	
	public Entity(Mesh[] meshes, float scale, IGame game) {
		this(meshes, game);
		this.scale = scale;
	}
	
	public final void setPosition(float x, float y, float z) {
		synchronized (position) {
			this.position.set(x, y, z);
			tAnimation.reset();
		}
	}
	
	public final void setPosition(Vector3f v) {
		setPosition(v.x, v.y, v.z);
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
