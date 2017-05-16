package eu.grmdev.senryaku.graphic.effects;

import org.joml.Vector3f;

import lombok.Getter;
import lombok.Setter;

public class Fog {
	@Getter
	@Setter
	private boolean active;
	@Getter
	@Setter
	private Vector3f color;
	@Getter
	@Setter
	private float density;
	public static final Fog NOFOG = new Fog();
	
	public Fog() {
		active = false;
		this.color = new Vector3f(0, 0, 0);
		this.density = 0;
	}
	
	public Fog(boolean active, Vector3f colour, float density) {
		this.color = colour;
		this.density = density;
		this.active = active;
	}
}
