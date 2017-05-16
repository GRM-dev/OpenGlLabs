package eu.grmdev.senryaku.core;

import org.joml.Vector3f;

import eu.grmdev.senryaku.graphic.lights.*;
import lombok.Getter;
import lombok.Setter;

public class SceneLight {
	@Getter
	@Setter
	private Vector3f ambientLight;
	@Getter
	@Setter
	private Vector3f skyBoxLight;
	@Getter
	@Setter
	private PointLight[] pointLightList;
	@Getter
	@Setter
	private SpotLight[] spotLightList;
	@Getter
	@Setter
	private DirectionalLight directionalLight;
}