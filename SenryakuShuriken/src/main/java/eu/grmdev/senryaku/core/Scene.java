package eu.grmdev.senryaku.core;

import java.util.*;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.SkyBox;
import eu.grmdev.senryaku.graphic.InstancedMesh;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.effects.Fog;
import lombok.Getter;
import lombok.Setter;

public class Scene {
	@Getter
	private final Map<Mesh, List<Entity>> gameMeshes;
	@Getter
	private final Map<InstancedMesh, List<Entity>> gameInstancedMeshes;
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
	
	public void setGameItems(Entity[] gameItems) {
		if (gameItems == null || gameItems.length == 0) { return; }
		for (int i = 0; i < gameItems.length; i++) {
			Entity gameItem = gameItems[i];
			Mesh[] meshes = gameItem.getMeshes();
			for (Mesh mesh : meshes) {
				boolean instancedMesh = mesh instanceof InstancedMesh;
				List<Entity> list = instancedMesh ? gameInstancedMeshes.get(mesh) : gameMeshes.get(mesh);
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
	
	public void destroy() {
		for (Mesh mesh : gameMeshes.keySet()) {
			mesh.remove();
		}
		for (Mesh mesh : gameInstancedMeshes.keySet()) {
			mesh.remove();
		}
	}
}
