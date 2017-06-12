package eu.grmdev.senryaku.core.entity.throwable;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.graphic.mesh.Mesh;
import lombok.Getter;

public enum Projectiles {
	SHURIKEN("models/entities/shuriken.obj","/models/entities",0.05f,3000l,new Vector3f(60.0f, 0.0f, 0.0f));
	
	private String obj;
	private String tex;
	private Mesh[] mesh;
	private @Getter float scale;
	private @Getter long lifetime;
	private @Getter Quaternionf initialRotation;
	
	/**
	 * @param obj
	 *           path to obj file
	 * @param tex
	 *           path to textures
	 * @param scale
	 *           scle of object
	 * @param lifetime
	 *           time of projectile life in miliseconds
	 */
	private Projectiles(String obj, String tex, float scale, long lifetime) {
		this.obj = obj;
		this.tex = tex;
		this.scale = scale;
		this.lifetime = lifetime;
	}
	
	private Projectiles(String obj, String tex, float scale, long lifetime, Vector3f rot) {
		this(obj, tex, scale, lifetime);
		this.initialRotation = new Quaternionf((float) Math.toRadians(rot.x), (float) Math.toRadians(rot.y), (float) Math.toRadians(rot.z)).normalize();
		System.out.println(rot + " | " + initialRotation);
	}
	
	public Mesh[] getMesh() throws Exception {
		if (mesh == null) {
			mesh = StaticMeshesLoader.load(obj, tex);
		}
		return mesh;
	}
}
