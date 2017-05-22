package eu.grmdev.senryaku.game;

import java.io.File;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.Mesh;

public class Player extends Entity {
	
	public Player() throws Exception {
		super();
		String fileName = Utils.loadResourceURL("models/player/ninja.obj").getFile();
		File file = new File(fileName);
		Mesh[] terrainMesh = StaticMeshesLoader.load(file.getAbsolutePath(), "/models/player");
		this.meshes = terrainMesh;
		setScale(0.5f);
		getPosition().y = 0.5f;
		getPosition().x = -0.16f;
	}
	
	public void init(EventHandler eh) {
		eh.addKeyEventListener(event -> {
			
		});
	}
}
