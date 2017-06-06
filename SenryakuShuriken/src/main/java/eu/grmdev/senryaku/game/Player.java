package eu.grmdev.senryaku.game;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.entity.*;
import eu.grmdev.senryaku.core.entity.throwable.Projectile;
import eu.grmdev.senryaku.core.entity.throwable.Projectiles;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.events.MouseEvent;
import eu.grmdev.senryaku.core.events.listeners.KeyEventListener;
import eu.grmdev.senryaku.core.events.listeners.MouseEventListener;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.core.map.GameMap;
import eu.grmdev.senryaku.core.map.LevelManager;
import eu.grmdev.senryaku.game.hud.Hud;
import eu.grmdev.senryaku.graphic.Camera;
import eu.grmdev.senryaku.graphic.Mesh;

public class Player extends Entity implements Movable {
	private Camera camera;
	private LevelManager levelManager;
	private Hud hud;
	private EventHandler eh;
	private Vector3f cRot;
	private static float offset = -0.15f;
	
	public Player(LevelManager levelManager, Hud hud, IGame game) throws Exception {
		super(game);
		this.camera = game.getCamera();
		this.levelManager = levelManager;
		this.hud = hud;
		Mesh[] mesh = StaticMeshesLoader.load("models/player/ninja.obj", "/models/player");
		this.meshes = mesh;
		setScale(0.5f);
		applyRenderOffset();
		this.levelManager.setPlayer(this);
		cRot = new Vector3f(0, 0, 1);
	}
	
	@Override
	public void init(EventHandler eh) {
		this.eh = eh;
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
		}, KeyEvent.PRESSED);
		eh.addMouseEventListener(new MouseEventListener() {
			private double cooldown = 0.45f;
			private double lastFired;
			
			@Override
			public void actionPerformed(MouseEvent event) {
				if (!getGame().isPaused() && event.isLeftButtonClicked() && (event.getCreationTime() - (lastFired + cooldown) > 0)) {
					lastFired = event.getCreationTime();
					try {
						Vector3f dir = event.getClickDestination();
						Projectile s = new Projectile(Projectiles.SHURIKEN, getPosition(), dir, getGame());
						// System.out.println("_" + getPosition());
						// System.out.println(">" + dir);
						s.getRot().z = 0.5f;
						s.init(eh);
						getGame().addEntity(s);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		});
	}
	
	@Override
	public synchronized boolean canMove(float rx, float rz) {
		GameMap map = levelManager.getCurrentMap();
		float x = (float) Math.floor(tAnimation.getDestPosition().x + rx);
		float z = (float) Math.floor(tAnimation.getDestPosition().z + rz);
		return map.canMoveTo(x, Config.PLAYER_DEF_Y_POS, z);
	}
	
	@Override
	public synchronized void move(float rx, float rz) {
		tAnimation.move(rx, rz);
		if (cRot.x != rx || cRot.z != rz) {
			Direction d = Direction.getDirInvY(rx, rz);
			float angle = direction.angle(d);
			getRotation().rotateLocalY(angle);
			cRot.x = rx;
			cRot.z = rz;
			direction = d;
			applyRenderOffset();
		}
		checkEnd(position);
		levelManager.getCurrentMap().incCounter();
	}
	
	private void applyRenderOffset() {
		if (direction == Direction.DOWN) {
			getRenderOffset().x = offset;
		} else if (direction == Direction.RIGHT) {
			getRenderOffset().z = -offset;
		} else if (direction == Direction.LEFT) {
			getRenderOffset().x = -offset;
			getRenderOffset().z = offset;
		} else {
			getRenderOffset().x = -offset;
			getRenderOffset().z = -offset;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see eu.grmdev.senryaku.game.Movable#animate(float)
	 */
	@Override
	public void animate(float interval) {
		tAnimation.animate(interval);
		camera.setPosition(tAnimation.getPosition().x, camera.getPosition().y, tAnimation.getPosition().z);
		checkCollisions(tAnimation.getDestPosition());
		checkEnd(position);
	}
	
	@Override
	public void checkCollisions(Vector3f pos) {
		// TODO Auto-generated method stub
		
	}
	
	public void checkEnd(Vector3f pos) {
		GameMap map = levelManager.getCurrentMap();
		if (map.checkEnd(pos)) {
			hud.setShowEndLevelScreen(true);
		}
	}
	
	@Override
	public boolean canDie() {
		return false;
	}
	
	@Override
	public void die() {}
}
