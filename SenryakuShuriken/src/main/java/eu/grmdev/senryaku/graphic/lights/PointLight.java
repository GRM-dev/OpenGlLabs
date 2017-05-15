package eu.grmdev.senryaku.graphic.lights;

import org.joml.Vector3f;

import lombok.Getter;
import lombok.Setter;

public class PointLight {
	@Getter
	@Setter
	private Vector3f color;
	@Getter
	@Setter
	private Vector3f position;
	@Getter
	@Setter
	private float intensity;
	@Getter
	@Setter
	private Attenuation attenuation;
	
	public PointLight(Vector3f color, Vector3f position, float intensity) {
		attenuation = new Attenuation(1, 0, 0);
		this.color = color;
		this.position = position;
		this.intensity = intensity;
	}
	
	public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
		this(color, position, intensity);
		this.attenuation = attenuation;
	}
	
	public PointLight(PointLight pointLight) {
		this(new Vector3f(pointLight.getColor()), new Vector3f(pointLight.getPosition()), pointLight.getIntensity(), pointLight.getAttenuation());
	}
	
	public static class Attenuation {
		@Getter
		@Setter
		private float constant;
		@Getter
		@Setter
		private float linear;
		@Getter
		@Setter
		private float exponent;
		
		public Attenuation(float constant, float linear, float exponent) {
			this.constant = constant;
			this.linear = linear;
			this.exponent = exponent;
		}
	}
}