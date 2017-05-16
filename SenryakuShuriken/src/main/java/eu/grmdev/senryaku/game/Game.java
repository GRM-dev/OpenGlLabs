package eu.grmdev.senryaku.game;

import static org.lwjgl.glfw.GLFW.*;

import java.io.File;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import org.joml.*;

import eu.grmdev.senryaku.Main.Config;
import eu.grmdev.senryaku.core.*;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.SkyBox;
import eu.grmdev.senryaku.core.handlers.MouseInput;
import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.graphic.*;
import eu.grmdev.senryaku.graphic.effects.Fog;
import eu.grmdev.senryaku.graphic.lights.DirectionalLight;

public class Game implements IGame {
	private final Vector3f cameraInc;
	private final Renderer renderer;
	private final Camera camera;
	private float lightAngleInc;
	private float lightAngle;
	private boolean firstTime;
	private boolean sceneChanged;
	private Scene scene;
	private Entity[] entities;
	
	public Game() {
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
		lightAngleInc = 0;
		lightAngle = 90;
		firstTime = true;
	}
	
	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);
		scene = new Scene();
		
		entities = setupStartEntities();
		scene.setGameItems(entities);
		
		scene.setRenderShadows(Config.SHADOWS_ENABLED);
		Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
		scene.setFog(new Fog(true, fogColour, 0.02f));
		
		setupSkyBox();
		setupLights();
		setupCameraStartPos();
	}
	
	private Entity[] setupStartEntities() throws Exception {
		List<Entity> entities = new ArrayList<>();
		String fileName = Utils.loadResourceURL("models/house/house.obj").getFile();
		File file = new File(fileName);
		Mesh[] houseMesh = StaticMeshesLoader.load(file.getAbsolutePath(), "/models/house");
		Entity house = new Entity(houseMesh);
		entities.add(house);
		
		fileName = Utils.loadResourceURL("models/terrain/terrain.obj").getFile();
		file = new File(fileName);
		Mesh[] terrainMesh = StaticMeshesLoader.load(file.getAbsolutePath(), "/models/terrain");
		Entity terrain = new Entity(terrainMesh);
		terrain.setScale(100.0f);
		entities.add(terrain);
		
		return entities.toArray(new Entity[0]);
	}
	
	private void setupSkyBox() throws Exception {
		float skyBoxScale = 100.0f;
		String fileName = Utils.loadResourceURL("models/skybox.obj").getFile();
		File file = new File(fileName);
		SkyBox skyBox = new SkyBox(file.getAbsolutePath(), new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
		skyBox.setScale(skyBoxScale);
		scene.setSkyBox(skyBox);
	}
	
	private void setupLights() {
		SceneLight sceneLight = new SceneLight();
		scene.setSceneLight(sceneLight);
		
		sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
		sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));
		
		float lightIntensity = 1.0f;
		Vector3f lightDirection = new Vector3f(0, 1, 1);
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
		sceneLight.setDirectionalLight(directionalLight);
	}
	
	private void setupCameraStartPos() {
		camera.getPosition().x = -17.0f;
		camera.getPosition().y = 17.0f;
		camera.getPosition().z = -30.0f;
		camera.getRotation().x = 20.0f;
		camera.getRotation().y = 140.f;
	}
	
	@Override
	public void input(Window window, MouseInput mouseInput) {
		sceneChanged = false;
		cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW_KEY_W)) {
			sceneChanged = true;
			cameraInc.z = -1;
		} else if (window.isKeyPressed(GLFW_KEY_S)) {
			sceneChanged = true;
			cameraInc.z = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_A)) {
			sceneChanged = true;
			cameraInc.x = -1;
		} else if (window.isKeyPressed(GLFW_KEY_D)) {
			sceneChanged = true;
			cameraInc.x = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_Z)) {
			sceneChanged = true;
			cameraInc.y = -1;
		} else if (window.isKeyPressed(GLFW_KEY_X)) {
			sceneChanged = true;
			cameraInc.y = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_LEFT)) {
			sceneChanged = true;
			lightAngleInc -= 0.05f;
		} else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
			sceneChanged = true;
			lightAngleInc += 0.05f;
		} else {
			lightAngleInc = 0;
		}
	}
	
	@Override
	public void update(float interval, MouseInput mouseInput, Window window) {
		if (mouseInput.isRightButtonPressed()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * Config.MOUSE_SENSITIVITY, rotVec.y * Config.MOUSE_SENSITIVITY, 0);
			sceneChanged = true;
		}
		camera.movePosition(cameraInc.x * Config.CAMERA_POS_STEP, cameraInc.y * Config.CAMERA_POS_STEP, cameraInc.z * Config.CAMERA_POS_STEP);
		
		lightAngle += lightAngleInc;
		if (lightAngle < 0) {
			lightAngle = 0;
		} else if (lightAngle > 180) {
			lightAngle = 180;
		}
		float zValue = (float) Math.cos(Math.toRadians(lightAngle));
		float yValue = (float) Math.sin(Math.toRadians(lightAngle));
		Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
		lightDirection.x = 0;
		lightDirection.y = yValue;
		lightDirection.z = zValue;
		lightDirection.normalize();
		
		camera.updateViewMatrix();
	}
	
	@Override
	public void render(Window window) {
		if (firstTime) {
			sceneChanged = true;
			firstTime = false;
		}
		renderer.render(window, camera, scene, sceneChanged);
	}
	
	@Override
	public void destroy() {
		renderer.cleanup();
		scene.destroy();
	}
}
