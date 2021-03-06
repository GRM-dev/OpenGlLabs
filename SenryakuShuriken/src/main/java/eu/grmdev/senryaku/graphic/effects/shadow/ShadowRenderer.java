package eu.grmdev.senryaku.graphic.effects.shadow;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Matrix4f;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.Scene;
import eu.grmdev.senryaku.core.SceneLight;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.*;
import eu.grmdev.senryaku.graphic.lights.DirectionalLight;
import eu.grmdev.senryaku.graphic.mesh.InstancedMesh;
import eu.grmdev.senryaku.graphic.mesh.Mesh;
import lombok.Getter;

public class ShadowRenderer {
	private ShaderProgram depthShaderProgram;
	@Getter
	private List<ShadowCascade> shadowCascades;
	private ShadowBuffer shadowBuffer;
	private final ConcurrentHashMap<Integer, Entity> filteredItems;
	private int nextId;
	
	public ShadowRenderer() {
		filteredItems = new ConcurrentHashMap<>();
	}
	
	public void init(Window window) throws Exception {
		shadowBuffer = new ShadowBuffer();
		shadowCascades = new ArrayList<>();
		
		setupDepthShader();
		
		float zNear = Config.Z_NEAR.<Float> get();
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES.<Integer> get(); i++) {
			ShadowCascade shadowCascade = new ShadowCascade(zNear, Config.SHADOW_CASCADE_SPLITS.getArray()[i]);
			shadowCascades.add(shadowCascade);
			zNear = Config.SHADOW_CASCADE_SPLITS.getArray()[i];
		}
	}
	
	private void setupDepthShader() throws Exception {
		depthShaderProgram = new ShaderProgram();
		depthShaderProgram.createVertexShader(Utils.loadResourceContent("/shaders/depth_vertex.vs"));
		depthShaderProgram.createFragmentShader(Utils.loadResourceContent("/shaders/depth_fragment.fs"));
		depthShaderProgram.link();
		
		depthShaderProgram.createUniform("isInstanced");
		depthShaderProgram.createUniform("modelNonInstancedMatrix");
		depthShaderProgram.createUniform("lightViewMatrix");
		depthShaderProgram.createUniform("jointsMatrix");
		depthShaderProgram.createUniform("orthoProjectionMatrix");
	}
	
	public void bindTextures(int start) {
		this.shadowBuffer.bindTextures(start);
	}
	
	private void update(Window window, Matrix4f viewMatrix, Scene scene) {
		SceneLight sceneLight = scene.getSceneLight();
		DirectionalLight directionalLight = sceneLight != null ? sceneLight.getDirectionalLight() : null;
		if (directionalLight == null) { return; }
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES.<Integer> get(); i++) {
			ShadowCascade shadowCascade = shadowCascades.get(i);
			shadowCascade.update(window, viewMatrix, directionalLight);
		}
	}
	
	public void render(Window window, Scene scene, Camera camera, Transformation transformation, Renderer renderer) {
		update(window, camera.getViewMatrix(), scene);
		
		glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
		glViewport(0, 0, Config.SHADOW_MAP_WIDTH.<Integer> get(), Config.SHADOW_MAP_HEIGHT.<Integer> get());
		glClear(GL_DEPTH_BUFFER_BIT);
		
		depthShaderProgram.bind();
		
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES.<Integer> get(); i++) {
			ShadowCascade shadowCascade = shadowCascades.get(i);
			
			depthShaderProgram.setUniformm4f("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix());
			depthShaderProgram.setUniformm4f("lightViewMatrix", shadowCascade.getLightViewMatrix());
			
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i], 0);
			glClear(GL_DEPTH_BUFFER_BIT);
			
			renderNonInstancedMeshes(scene, transformation);
			renderInstancedMeshes(scene, transformation);
		}
		unbind();
	}
	
	private void renderNonInstancedMeshes(Scene scene, Transformation transformation) {
		depthShaderProgram.setUniformi("isInstanced", 0);
		
		Map<Mesh, List<Entity>> mapMeshes = scene.getEntityMeshes();
		for (Mesh mesh : mapMeshes.keySet()) {
			mesh.renderList(mapMeshes.get(mesh), (Entity gameItem) -> {
				Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
				depthShaderProgram.setUniformm4f("modelNonInstancedMatrix", modelMatrix);
			});
		}
	}
	
	private void renderInstancedMeshes(Scene scene, Transformation transformation) {
		depthShaderProgram.setUniformi("isInstanced", 1);
		
		Map<InstancedMesh, List<Entity>> mapMeshes = scene.getGameInstancedMeshes();
		for (InstancedMesh mesh : mapMeshes.keySet()) {
			filteredItems.clear();
			nextId = 0;
			for (Entity gameItem : mapMeshes.get(mesh)) {
				if (gameItem.isInsideFrustum()) {
					filteredItems.put(nextId++, gameItem);
				}
			}
			bindTextures(GL_TEXTURE2);
			
			mesh.renderListInstanced(filteredItems, transformation, null);
		}
	}
	
	private void unbind() {
		depthShaderProgram.unbind();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void cleanup() {
		if (shadowBuffer != null) {
			shadowBuffer.cleanup();
		}
		if (depthShaderProgram != null) {
			depthShaderProgram.cleanup();
		}
	}
}
