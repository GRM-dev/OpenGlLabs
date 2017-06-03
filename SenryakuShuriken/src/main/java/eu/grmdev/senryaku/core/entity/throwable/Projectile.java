package eu.grmdev.senryaku.core.entity.throwable;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import org.joml.Vector3f;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.Movable;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.map.GameMap;
import eu.grmdev.senryaku.core.map.LevelManager;
import lombok.Getter;

public class Projectile extends Entity implements Movable {
	private Projectiles projectile;
	private Vector3f startPos;
	private Vector3f dir;
	private LevelManager levelManager;
	private boolean collided;
	private @Getter Vector3f rot;
	
	public Projectile(Projectiles projectile, Vector3f startPos, Vector3f dir, IGame game) throws Exception {
		super(game);
		this.projectile = projectile;
		this.startPos = startPos;
		this.dir = dir;
		this.meshes = projectile.getMesh();
		this.scale = projectile.getScale();
		this.setPosition(startPos);
		this.getPosition().add(0f, 0.5f, 0f);
		this.setRotation(projectile.getInitialRotation());
		this.levelManager = LevelManager.getInstance();
		this.rot = new Vector3f();
	}
	
	@Override
	public void init(EventHandler eh) {
		eh.addTickGameEventListener(event -> {
			if (!event.getGame().isPaused()) {
				if (canMove(dir.x, dir.z)) {
					move(dir.x, dir.z);
				}
			}
		});
	}
	
	@Override
	public boolean canMove(float rx, float rz) {
		return true;
	}
	
	@Override
	public void move(float rx, float rz) {
		tAnimation.move(rx, rz);
	}
	
	@Override
	public void animate(float interval) {
		tAnimation.animate(interval);
		checkCollisions(tAnimation.getDestPosition());
		getRotation().rotate(rot.x, rot.y, rot.z);
	}
	
	@Override
	public void checkCollisions(Vector3f pos) {
		GameMap map = levelManager.getCurrentMap();
		float x = (float) Math.floor(tAnimation.getDestPosition().x);
		float z = (float) Math.floor(tAnimation.getDestPosition().z);
		collided = !map.canMoveTo(x, Config.PLAYER_DEF_Y_POS, z);
		// System.out.println(tAnimation.getDestPosition().toString(NumberFormat.getIntegerInstance()));
	}
	
	@Override
	public boolean canDie() {
		return getCreationTime() + ((double) projectile.getLifetime()) / 1000 < glfwGetTime();// ||
																															// collided;
	}
	
	@Override
	public void die() {
		getGame().removeEntity(this);
	}
	
}
