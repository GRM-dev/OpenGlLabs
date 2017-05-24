package eu.grmdev.senryaku.game;

import static org.lwjgl.glfw.GLFW.*;

import java.io.File;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.core.events.listeners.KeyEventListener;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.Camera;
import eu.grmdev.senryaku.graphic.Mesh;

public class Player extends Entity {
	private Camera camera;
	
	public Player(Camera camera) throws Exception {
		super();
		this.camera = camera;
		String fileName = Utils.loadResourceURL("models/player/ninja.obj").getFile();
		File file = new File(fileName);
		Mesh[] terrainMesh = StaticMeshesLoader.load(file.getAbsolutePath(), "/models/player");
		this.meshes = terrainMesh;
		setScale(0.5f);
		getPosition().y = 0.5f;
		getPosition().x = -0.2f;
		tAnimation.reset();
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
					if (key == GLFW_KEY_W) {
						move(0f, -1.f);
					} else if (key == GLFW_KEY_S) {
						move(0f, 1.0f);
					}
					if (key == GLFW_KEY_A) {
						move(-1.0f, 0f);
					} else if (key == GLFW_KEY_D) {
						move(1.0f, 0f);
					}
				}
			}
		});
	}
	
	private void move(float rx, float rz) {
		tAnimation.move(rx, rz);
		// camera.x += rx;
		// camera.z += rz;
	}
	
	public void animate(float interval) {
		tAnimation.animate(interval);
	}
}
