package eu.grmdev.senryaku.graphic.lights;

import org.joml.Vector3f;

import lombok.Getter;
import lombok.Setter;

public class SpotLight {
	@Getter
	@Setter
	private PointLight pointLight;
	@Getter
	@Setter
	private Vector3f coneDirection;
	@Getter
	@Setter
	private float cutOff;
	
	public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
		this.pointLight = pointLight;
		this.coneDirection = coneDirection;
		setCutOffAngle(cutOffAngle);
	}
	
	public SpotLight(SpotLight spotLight) {
		this(new PointLight(spotLight.getPointLight()), new Vector3f(spotLight.getConeDirection()), spotLight.getCutOff());
	}
	
	public final void setCutOffAngle(float cutOffAngle) {
		this.setCutOff((float) Math.cos(Math.toRadians(cutOffAngle)));
	}
	
}
