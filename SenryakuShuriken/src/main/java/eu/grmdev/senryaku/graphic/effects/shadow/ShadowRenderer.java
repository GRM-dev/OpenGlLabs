package eu.grmdev.senryaku.graphic.effects.shadow;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.joml.Matrix4f;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.*;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.*;
import eu.grmdev.senryaku.graphic.lights.DirectionalLight;
import lombok.Getter;

public class ShadowRenderer {
	private ShaderProgram depthShaderProgram;
	@Getter
	private List<ShadowCascade> shadowCascades;
	private ShadowBuffer shadowBuffer;
	private final List<Entity> filteredItems;
	
	public ShadowRenderer() {
		filteredItems = new ArrayList<>();
	}
	
	public void init(Window window) throws Exception {
		shadowBuffer = new ShadowBuffer();
		shadowCascades = new ArrayList<>();
		
		setupDepthShader();
		
		float zNear = Config.Z_NEAR;
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES; i++) {
			ShadowCascade shadowCascade = new ShadowCascade(zNear, Config.SHADOW_CASCADE_SPLITS[i]);
			shadowCascades.add(shadowCascade);
			zNear = Config.SHADOW_CASCADE_SPLITS[i];
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
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES; i++) {
			ShadowCascade shadowCascade = shadowCascades.get(i);
			shadowCascade.update(window, viewMatrix, directionalLight);
		}
	}
	
	public void render(Window window, Scene scene, Camera camera, Transformation transformation, Renderer renderer) {
		update(window, camera.getViewMatrix(), scene);
		
		glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
		glViewport(0, 0, Config.SHADOW_MAP_WIDTH, Config.SHADOW_MAP_HEIGHT);
		glClear(GL_DEPTH_BUFFER_BIT);
		
		depthShaderProgram.bind();
		
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES; i++) {
			ShadowCascade shadowCascade = shadowCascades.get(i);
			
			depthShaderProgram.setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix());
			depthShaderProgram.setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix());
			
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
				depthShaderProgram.setUniform("modelNonInstancedMatrix", modelMatrix);
			});
		}
	}
	
	private void renderInstancedMeshes(Scene scene, Transformation transformation) {
		depthShaderProgram.setUniformi("isInstanced", 1);
		
		Map<InstancedMesh, List<Entity>> mapMeshes = scene.getGameInstancedMeshes();
		for (InstancedMesh mesh : mapMeshes.keySet()) {
			filteredItems.clear();
			for (Entity gameItem : mapMeshes.get(mesh)) {
				if (gameItem.isInsideFrustum()) {
					filteredItems.add(gameItem);
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
