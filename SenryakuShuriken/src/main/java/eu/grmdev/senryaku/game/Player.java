package eu.grmdev.senryaku.game;

import static org.lwjgl.glfw.GLFW.*;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.events.listeners.KeyEventListener;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.handlers.LevelManager;
import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.core.map.GameMap;
import eu.grmdev.senryaku.core.map.Tile;
import eu.grmdev.senryaku.graphic.Camera;
import eu.grmdev.senryaku.graphic.Mesh;

public class Player extends Entity {
	private Camera camera;
	private LevelManager levelManager;
	
	public Player(Camera camera, LevelManager levelManager) throws Exception {
		super();
		this.camera = camera;
		this.levelManager = levelManager;
		Mesh[] mesh = StaticMeshesLoader.load("models/player/ninja.obj", "/models/player");
		this.meshes = mesh;
		setScale(0.5f);
		setPosition(-0.2f, 0.5f, -0.2f);
		tAnimation.reset();
		this.levelManager.setPlayer(this);
	}
	
	public void init(EventHandler eh) {
		eh.addKeyEventListener(new KeyEventListener() {
			private double cooldown = 0.5f;
			private double lastFired;
			
			@Override
			public void actionPerformed(KeyEvent event) {
				if (!event.isRepeatable() || event.getCreationTime() - (lastFired + cooldown) > 0) {
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
		int x = (int) Math.floor(tAnimation.getDestPosition().x + rx);
		int z = (int) Math.floor(tAnimation.getDestPosition().z + rz);
		Tile tile = map.getTerrain().getTile(x, z);
		System.out.println("x: " + x + " z: " + z + " | " + (tile != null) + " | " + (tile == null ? "-" : tile.isPassable()));
		if (tile == null) { return false; }
		return tile.isPassable();
	}
	
	private void move(int rx, int rz) {
		tAnimation.move(rx, rz);
	}
	
	public void animate(float interval) {
		tAnimation.animate(interval);
		camera.setPosition(tAnimation.getPosition().x, camera.getPosition().y, tAnimation.getPosition().z);
	}
}
