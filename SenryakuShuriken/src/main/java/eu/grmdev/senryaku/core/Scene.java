package eu.grmdev.senryaku.core;

import java.util.*;

import eu.grmdev.senryaku.core.items.GameItem;
import eu.grmdev.senryaku.core.items.SkyBox;
import eu.grmdev.senryaku.graphic.InstancedMesh;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.effects.Fog;
import lombok.Getter;
import lombok.Setter;

public class Scene {
	@Getter
	private final Map<Mesh, List<GameItem>> gameMeshes;
	@Getter
	private final Map<InstancedMesh, List<GameItem>> gameInstancedMeshes;
	@Getter
	@Setter
	private SkyBox skyBox;
	@Getter
	@Setter
	private SceneLight sceneLight;
	@Getter
	@Setter
	private Fog fog;
	@Getter
	@Setter
	private boolean renderShadows;
	
	public Scene() {
		gameMeshes = new HashMap<>();
		gameInstancedMeshes = new HashMap<>();
		fog = Fog.NOFOG;
		renderShadows = true;
	}
	
	public void setGameItems(GameItem[] gameItems) {
		int numGameItems = gameItems != null ? gameItems.length : 0;
		for (int i = 0; i < numGameItems; i++) {
			GameItem gameItem = gameItems[i];
			Mesh[] meshes = gameItem.getMeshes();
			for (Mesh mesh : meshes) {
				boolean instancedMesh = mesh instanceof InstancedMesh;
				List<GameItem> list = instancedMesh ? gameInstancedMeshes.get(mesh) : gameMeshes.get(mesh);
				if (list == null) {
					list = new ArrayList<>();
					if (instancedMesh) {
						gameInstancedMeshes.put((InstancedMesh) mesh, list);
					} else {
						gameMeshes.put(mesh, list);
					}
				}
				list.add(gameItem);
			}
		}
	}
	
	public void cleanup() {
		for (Mesh mesh : gameMeshes.keySet()) {
			mesh.cleanUp();
		}
		for (Mesh mesh : gameInstancedMeshes.keySet()) {
			mesh.cleanUp();
		}
	}
}
