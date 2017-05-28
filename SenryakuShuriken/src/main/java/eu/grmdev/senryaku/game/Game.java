package eu.grmdev.senryaku.game;

import static org.lwjgl.glfw.GLFW.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import org.joml.*;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.*;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.SkyBox;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.handlers.*;
import eu.grmdev.senryaku.game.hud.Hud;
import eu.grmdev.senryaku.graphic.*;
import eu.grmdev.senryaku.graphic.effects.Fog;
import eu.grmdev.senryaku.graphic.lights.DirectionalLight;
import lombok.Getter;

public class Game implements IGame {
	private @Getter final Vector3f cameraInc;
	private final Renderer renderer;
	private Scene scene;
	private final Hud hud;
	private final Camera camera;
	private @Getter final LevelManager levelManager;
	private float lightAngleInc;
	private float lightAngle;
	private Entity[] entities;
	private Player player;
	
	public Game() throws Exception {
		renderer = new Renderer();
		camera = new Camera();
		levelManager = new LevelManager();
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
		scene = new Scene();
		hud = new Hud();
		lightAngle = 90;
		lightAngleInc = 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initRender(Window window) throws Exception {
		renderer.init(window);
		
		entities = setupStartEntities();
		scene.setEntities(entities);
		
		scene.setRenderShadows(Config.SHADOWS_ENABLED);
		Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
		scene.setFog(new Fog(Config.FOG_ENABLED, fogColour, 0.02f));
		
		setupWorld();
		setupLights();
		setupCameraParams();
		hud.initRender(window);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initLogic(EventHandler eh, MouseHandler mh) throws Exception {
		assignGlobalListeners(eh);
		player.init(eh);
		levelManager.goTo(1);
		hud.initLogic(eh, mh);
	}
	
	private Entity[] setupStartEntities() throws Exception {
		List<Entity> entities = new ArrayList<>();
		player = new Player(camera, levelManager, hud);
		entities.add(player);
		
		// String fileName =
		// Utils.loadResourceURL("models/cube/cube.obj").getFile();
		// File file = new File(fileName);
		// Mesh[] houseMesh = StaticMeshesLoader.load(file.getAbsolutePath(),
		// "/models/cube");
		// Entity cube = new Entity(houseMesh);
		// entities.add(cube);
		
		return entities.toArray(new Entity[0]);
	}
	
	/**
	 * Loads Sky Box from obj file and applies it to scene
	 * 
	 * @throws Exception
	 */
	private void setupWorld() throws Exception {
		float skyBoxScale = 100.0f;
		SkyBox skyBox = new SkyBox("models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
		skyBox.setScale(skyBoxScale);
		scene.setSkyBox(skyBox);
		
	}
	
	/**
	 * Creates Lights and adds it to scene lights
	 */
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
	
	/**
	 * Changes position of camera to startup position
	 */
	private void setupCameraParams() {
		camera.setPosition(0.0f, 7.0f, 3.0f);
		camera.getRotation().x = 65.0f;
		camera.getOffset().z = 3f;
		camera.getOffset().x = 0.3f;
		
	}
	
	private void assignGlobalListeners(EventHandler eHandler) {
		eHandler.addKeyEventListener(event -> {
			if (event.getKey() == GLFW_KEY_ESCAPE) {
				hud.setMenuActive(true);
				event.setConsumed(true);
			}
		}, KeyEvent.RELEASED);
		eHandler.addTickGameEventListener(event -> {
			Window window = event.getWindow();
			if (window.isKeyPressed(GLFW_KEY_PAGE_UP)) {
				cameraInc.y -= 1;
			} else if (window.isKeyPressed(GLFW_KEY_PAGE_DOWN)) {
				cameraInc.y += 1;
			}
			if (window.isKeyPressed(GLFW_KEY_LEFT_BRACKET)) {
				lightAngleInc -= 0.05f;
			} else if (window.isKeyPressed(GLFW_KEY_RIGHT_BRACKET)) {
				lightAngleInc += 0.05f;
			} else {
				lightAngleInc = 0;
			}
		});
	}
	
	@Override
	public void input(Window window, MouseHandler mouseInput) {
		while (isPaused()) {
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void update(float interval, MouseHandler mouse, Window window) {
		if (mouse.isRightButtonPressed()) {
			Vector2f rotVec = mouse.getDisplVec();
			camera.moveRotation(rotVec.x * Config.MOUSE_SENSITIVITY, rotVec.y * Config.MOUSE_SENSITIVITY, 0);
		}
		player.animate(interval);
		camera.movePosition(cameraInc.x * Config.CAMERA_POS_STEP, cameraInc.y * Config.CAMERA_POS_STEP, cameraInc.z * Config.CAMERA_POS_STEP);
		cameraInc.set(0, 0, 0);
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
		renderer.render(window, camera, scene, levelManager);
		hud.render();
	}
	
	/**
	 * Called on game closing. Throws the thrash out of memory
	 */
	@Override
	public void destroy() {
		hud.destroy();
		renderer.cleanup();
		scene.destroy();
	}
	
	@Override
	public Entity getPlayer() {
		return player;
	}
	
	@Override
	public boolean isPaused() {
		return hud.isMenuActive();
	}
}
