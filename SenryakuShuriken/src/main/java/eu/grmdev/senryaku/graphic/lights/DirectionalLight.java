package eu.grmdev.senryaku.graphic.lights;

import org.joml.Vector3f;

import lombok.Getter;
import lombok.Setter;

public class DirectionalLight {
	@Getter
	@Setter
	private Vector3f color;
	@Getter
	@Setter
	private Vector3f direction;
	@Getter
	@Setter
	private float intensity;
	
	public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
		this.color = color;
		this.direction = direction;
		this.intensity = intensity;
	}
	
	public DirectionalLight(DirectionalLight light) {
		this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
	}
	
	public static class OrthoCoords {
		public float left;
		public float right;
		public float bottom;
		public float top;
		public float near;
		public float far;
	}
}