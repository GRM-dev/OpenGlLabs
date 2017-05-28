package eu.grmdev.senryaku.game;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.events.listeners.KeyEventListener;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.handlers.LevelManager;
import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.core.map.GameMap;
import eu.grmdev.senryaku.game.hud.Hud;
import eu.grmdev.senryaku.graphic.Camera;
import eu.grmdev.senryaku.graphic.Mesh;

public class Player extends Entity {
	public static final float PLAYER_DEF_Y_POS = 0.5f;
	private Camera camera;
	private LevelManager levelManager;
	private Hud hud;
	
	public Player(Camera camera, LevelManager levelManager, Hud hud) throws Exception {
		super();
		this.camera = camera;
		this.levelManager = levelManager;
		this.hud = hud;
		Mesh[] mesh = StaticMeshesLoader.load("models/player/ninja.obj", "/models/player");
		this.meshes = mesh;
		setScale(0.5f);
		getRenderOffset().x = -0.15f;
		getRenderOffset().z = -0.15f;
		this.levelManager.setPlayer(this);
	}
	
	public void init(EventHandler eh) {
		eh.addKeyEventListener(new KeyEventListener() {
			private double cooldown = 0.45f;
			private double lastFired;
			
			@Override
			public void actionPerformed(KeyEvent event) {
				if (event.getCreationTime() - (lastFired + cooldown) > 0) {
					lastFired = event.getCreationTime();
					int key = event.getKey();
					if (key == GLFW_KEY_W && canMove(0, -1)) {
						move(0, -1);
					} else if (key == GLFW_KEY_S && canMove(0, 1)) {
						move(0, 1);
					}
					if (key == GLFW_KEY_A && canMove(-1, 0)) {
						move(-1, 0);
					} else if (key == GLFW_KEY_D && canMove(1, 0)) {
						move(1, 0);
					}
				}
			}
		});
	}
	
	protected boolean canMove(int rx, int rz) {
		GameMap map = levelManager.getCurrentMap();
		float x = (float) Math.floor(tAnimation.getDestPosition().x + rx);
		float z = (float) Math.floor(tAnimation.getDestPosition().z + rz);
		return map.canMoveTo(x, PLAYER_DEF_Y_POS, z);
	}
	
	private void move(int rx, int rz) {
		tAnimation.move(rx, rz);
		checkCollsions(tAnimation.getDestPosition());
		checkEnd(tAnimation.getDestPosition());
		hud.incCounter();
	}
	
	private void checkCollsions(Vector3f pos) {
		// TODO Auto-generated method stub
		
	}
	
	private void checkEnd(Vector3f pos) {
		GameMap map = levelManager.getCurrentMap();
		if (map.checkEnd(pos)) {
			hud.setShowEndLevelScreen(true);
		}
	}
	
	public void animate(float interval) {
		tAnimation.animate(interval);
		camera.setPosition(tAnimation.getPosition().x, camera.getPosition().y, tAnimation.getPosition().z);
		checkCollsions(tAnimation.getDestPosition());
		checkEnd(tAnimation.getDestPosition());
	}
}
