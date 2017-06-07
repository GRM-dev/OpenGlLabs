package eu.grmdev.senryaku.core.entity.throwable;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import java.util.concurrent.ConcurrentLinkedQueue;

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
	private @Getter Vector3f rot;
	private boolean collided;
	private LevelManager levelManager;
	private EventHandler eh;
	private static ConcurrentLinkedQueue<Projectile> objects = new ConcurrentLinkedQueue<>();
	
	public Projectile(Projectiles projectile, Vector3f startPos, Vector3f dir, IGame game, EventHandler eh) throws Exception {
		super(game);
		objects.offer(this);
		this.projectile = projectile;
		this.eh = eh;
		this.startPos = new Vector3f(startPos);
		this.dir = new Vector3f(dir);
		this.meshes = projectile.getMesh();
		this.scale = projectile.getScale();
		this.setPosition(startPos.x, startPos.y + 0.5f, startPos.z);
		this.setRotation(projectile.getInitialRotation());
		this.levelManager = LevelManager.getInstance();
		this.rot = new Vector3f();
		this.dir.z += 0.42f;
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
		float x = Math.round(tAnimation.getNextPosition().x);
		if (x > -0.7f && x < 0.0f) {
			x = 0;
		}
		float z = Math.round(tAnimation.getNextPosition().z);
		if (z > -0.7f && z < 0.0f) {
			z = 0;
		}
		collided = !map.canMoveTo(x, Config.PLAYER_DEF_Y_POS, z);
	}
	
	@Override
	public boolean canDie() {
		return getCreationTime() + ((double) projectile.getLifetime()) / 1000 < glfwGetTime() || collided;
	}
	
	@Override
	public void die() {
		getGame().removeEntity(this);
		objects.remove(this);
	}
	
}
