package eu.grmdev.senryaku.graphic.mesh;

import org.joml.Vector4f;

import lombok.Getter;
import lombok.Setter;

public class Material {
	public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	@Getter
	@Setter
	private Vector4f ambientColor;
	@Getter
	@Setter
	private Vector4f diffuseColor;
	@Getter
	@Setter
	private Vector4f specularColor;
	@Getter
	@Setter
	private float shininess;
	@Getter
	@Setter
	private float reflectance;
	@Getter
	@Setter
	private Texture texture;
	@Getter
	@Setter
	private Texture normalMap;
	
	public Material() {
		this.ambientColor = DEFAULT_COLOR;
		this.diffuseColor = DEFAULT_COLOR;
		this.specularColor = DEFAULT_COLOR;
		this.texture = null;
		this.reflectance = 0;
		this.shininess = 0;
	}
	
	public Material(Vector4f colour, float reflectance) {
		this(colour, colour, colour, null, reflectance);
	}
	
	public Material(Texture texture) {
		this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, texture, 0);
	}
	
	public Material(Texture texture, float reflectance) {
		this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, texture, reflectance);
	}
	
	public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, float reflectance) {
		this(ambientColour, diffuseColour, specularColour, null, reflectance);
	}
	
	public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, Texture texture, float reflectance) {
		this.ambientColor = ambientColour;
		this.diffuseColor = diffuseColour;
		this.specularColor = specularColour;
		this.texture = texture;
		this.reflectance = reflectance;
	}
	
	public boolean isTextured() {
		return this.texture != null;
	}
	
	public boolean hasNormalMap() {
		return this.normalMap != null;
	}
}