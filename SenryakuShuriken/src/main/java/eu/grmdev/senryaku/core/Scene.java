package eu.grmdev.senryaku.core;

import java.util.*;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.SkyBox;
import eu.grmdev.senryaku.core.map.GameMap;
import eu.grmdev.senryaku.graphic.InstancedMesh;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.effects.Fog;
import lombok.Getter;
import lombok.Setter;

public class Scene {
	private @Getter final HashMap<Mesh, List<Entity>> entityMeshes;
	private @Getter final HashMap<InstancedMesh, List<Entity>> gameInstancedMeshes;
	private @Getter @Setter SkyBox skyBox;
	private @Getter @Setter SceneLight sceneLight;
	private @Getter @Setter Fog fog;
	private @Getter @Setter GameMap map;
	private @Getter @Setter boolean renderShadows;
	
	public Scene() {
		entityMeshes = new HashMap<>();
		gameInstancedMeshes = new HashMap<>();
		fog = Fog.NOFOG;
		renderShadows = true;
	}
	
	public void setEntities(Entity[] entities) {
		if (entities == null || entities.length == 0) { return; }
		for (int i = 0; i < entities.length; i++) {
			Entity entity = entities[i];
			addEntity(entity);
		}
	}
	
	public void addEntity(Entity entity) {
		Mesh[] meshes = entity.getMeshes();
		for (Mesh mesh : meshes) {
			boolean instancedMesh = mesh instanceof InstancedMesh;
			List<Entity> list = instancedMesh ? gameInstancedMeshes.get(mesh) : entityMeshes.get(mesh);
			if (list == null) {
				list = new ArrayList<>();
				if (instancedMesh) {
					gameInstancedMeshes.put((InstancedMesh) mesh, list);
				} else {
					entityMeshes.put(mesh, list);
				}
			}
			list.add(entity);
		}
	}
	
	public void removeEntity(Entity entity) {
		Mesh[] meshes = entity.getMeshes();
		for (Mesh mesh : meshes) {
			boolean instancedMesh = mesh instanceof InstancedMesh;
			List<Entity> list = instancedMesh ? gameInstancedMeshes.get(mesh) : entityMeshes.get(mesh);
			if (list != null) {
				for (Iterator<Entity> it = list.iterator(); it.hasNext();) {
					Entity e = it.next();
					if (entity.equals(e)) {
						it.remove();
					}
				}
			}
		}
	}
	
	public void destroy() {
		for (Mesh mesh : entityMeshes.keySet()) {
			mesh.remove();
		}
		for (Mesh mesh : gameInstancedMeshes.keySet()) {
			mesh.remove();
		}
	}
}
