package eu.grmdev.senryaku.graphic;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;

import java.util.*;

import org.joml.*;

import eu.grmdev.senryaku.Main.Config;
import eu.grmdev.senryaku.core.*;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.SkyBox;
import eu.grmdev.senryaku.graphic.effects.shadow.ShadowCascade;
import eu.grmdev.senryaku.graphic.effects.shadow.ShadowRenderer;
import eu.grmdev.senryaku.graphic.lights.*;
import eu.grmdev.senryaku.graphic.material.Texture;

public class Renderer {
	private final Transformation transformation;
	private final ShadowRenderer shadowRenderer;
	private ShaderProgram sceneShaderProgram;
	private ShaderProgram skyBoxShaderProgram;
	private final float specularPower;
	private final FrustumCullingFilter frustumFilter;
	private final List<Entity> filteredItems;
	
	public Renderer() {
		transformation = new Transformation();
		specularPower = 10f;
		shadowRenderer = new ShadowRenderer();
		frustumFilter = new FrustumCullingFilter();
		filteredItems = new ArrayList<>();
	}
	
	public void init(Window window) throws Exception {
		shadowRenderer.init(window);
		setupSkyBoxShader();
		setupSceneShader();
	}
	
	private void setupSkyBoxShader() throws Exception {
		skyBoxShaderProgram = new ShaderProgram();
		skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/sb_vertex.vs"));
		skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/sb_fragment.fs"));
		skyBoxShaderProgram.link();
		
		skyBoxShaderProgram.createUniform("projectionMatrix");
		skyBoxShaderProgram.createUniform("modelViewMatrix");
		skyBoxShaderProgram.createUniform("texture_sampler");
		skyBoxShaderProgram.createUniform("ambientLight");
		skyBoxShaderProgram.createUniform("colour");
		skyBoxShaderProgram.createUniform("hasTexture");
	}
	
	private void setupSceneShader() throws Exception {
		sceneShaderProgram = new ShaderProgram();
		sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
		sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
		sceneShaderProgram.link();
		
		sceneShaderProgram.createUniform("viewMatrix");
		sceneShaderProgram.createUniform("projectionMatrix");
		sceneShaderProgram.createUniform("texture_sampler");
		sceneShaderProgram.createUniform("normalMap");
		sceneShaderProgram.createMaterialUniform("material");
		sceneShaderProgram.createUniform("specularPower");
		sceneShaderProgram.createUniform("ambientLight");
		sceneShaderProgram.createPointLightListUniform("pointLights", Config.MAX_POINT_LIGHTS);
		sceneShaderProgram.createSpotLightListUniform("spotLights", Config.MAX_SPOT_LIGHTS);
		sceneShaderProgram.createDirectionalLightUniform("directionalLight");
		sceneShaderProgram.createFogUniform("fog");
		
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES; i++) {
			sceneShaderProgram.createUniform("shadowMap_" + i);
		}
		sceneShaderProgram.createUniform("orthoProjectionMatrix", Config.NUM_SHADOW_CASCADES);
		sceneShaderProgram.createUniform("modelNonInstancedMatrix");
		sceneShaderProgram.createUniform("lightViewMatrix", Config.NUM_SHADOW_CASCADES);
		sceneShaderProgram.createUniform("cascadeFarPlanes", Config.NUM_SHADOW_CASCADES);
		sceneShaderProgram.createUniform("renderShadow");
		
		sceneShaderProgram.createUniform("jointsMatrix");
		
		sceneShaderProgram.createUniform("isInstanced");
		sceneShaderProgram.createUniform("numCols");
		sceneShaderProgram.createUniform("numRows");
		
		sceneShaderProgram.createUniform("selectedNonInstanced");
	}
	
	public void render(Window window, Camera camera, Scene scene, boolean sceneChanged) {
		clear();
		
		if (window.getWindowOptions().frustumCulling) {
			frustumFilter.updateFrustum(window.getProjectionMatrix(), camera.getViewMatrix());
			frustumFilter.filter(scene.getGameMeshes());
			frustumFilter.filter(scene.getGameInstancedMeshes());
		}
		
		if (scene.isRenderShadows() && sceneChanged) {
			shadowRenderer.render(window, scene, camera, transformation, this);
		}
		
		glViewport(0, 0, window.getWidth(), window.getHeight());
		window.updateProjectionMatrix();
		
		renderScene(window, camera, scene);
		renderSkyBox(window, camera, scene);
		
		renderAxes(window, camera);
		renderCrossHair(window);
	}
	
	private void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	}
	
	private void renderSkyBox(Window window, Camera camera, Scene scene) {
		SkyBox skyBox = scene.getSkyBox();
		if (skyBox != null) {
			skyBoxShaderProgram.bind();
			
			skyBoxShaderProgram.setUniformi("texture_sampler", 0);
			
			Matrix4f projectionMatrix = window.getProjectionMatrix();
			skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
			Matrix4f viewMatrix = camera.getViewMatrix();
			float m30 = viewMatrix.m30();
			viewMatrix.m30(0);
			float m31 = viewMatrix.m31();
			viewMatrix.m31(0);
			float m32 = viewMatrix.m32();
			viewMatrix.m32(0);
			
			Mesh mesh = skyBox.getMesh();
			Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
			skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
			skyBoxShaderProgram.setUniformv3f("ambientLight", scene.getSceneLight().getSkyBoxLight());
			skyBoxShaderProgram.setUniformv4f("colour", mesh.getMaterial().getAmbientColor());
			skyBoxShaderProgram.setUniformi("hasTexture", mesh.getMaterial().isTextured() ? 1 : 0);
			
			mesh.render();
			
			viewMatrix.m30(m30);
			viewMatrix.m31(m31);
			viewMatrix.m32(m32);
			skyBoxShaderProgram.unbind();
		}
	}
	
	public void renderScene(Window window, Camera camera, Scene scene) {
		sceneShaderProgram.bind();
		
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f projectionMatrix = window.getProjectionMatrix();
		sceneShaderProgram.setUniform("viewMatrix", viewMatrix);
		sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
		
		List<ShadowCascade> shadowCascades = shadowRenderer.getShadowCascades();
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES; i++) {
			ShadowCascade shadowCascade = shadowCascades.get(i);
			sceneShaderProgram.setUniformm4fi("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix(), i);
			sceneShaderProgram.setUniformfi("cascadeFarPlanes", Config.SHADOW_CASCADE_SPLITS[i], i);
			sceneShaderProgram.setUniformm4fi("lightViewMatrix", shadowCascade.getLightViewMatrix(), i);
		}
		
		SceneLight sceneLight = scene.getSceneLight();
		renderLights(viewMatrix, sceneLight);
		
		sceneShaderProgram.setUniformFog("fog", scene.getFog());
		sceneShaderProgram.setUniformi("texture_sampler", 0);
		sceneShaderProgram.setUniformi("normalMap", 1);
		int start = 2;
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES; i++) {
			sceneShaderProgram.setUniformi("shadowMap_" + i, start + i);
		}
		sceneShaderProgram.setUniformi("renderShadow", scene.isRenderShadows() ? 1 : 0);
		
		renderNonInstancedMeshes(scene);
		renderInstancedMeshes(scene, viewMatrix);
		
		sceneShaderProgram.unbind();
	}
	
	private void renderNonInstancedMeshes(Scene scene) {
		sceneShaderProgram.setUniformi("isInstanced", 0);
		
		// Render each mesh with the associated game Items
		Map<Mesh, List<Entity>> mapMeshes = scene.getGameMeshes();
		for (Mesh mesh : mapMeshes.keySet()) {
			sceneShaderProgram.setUniformMat("material", mesh.getMaterial());
			
			Texture text = mesh.getMaterial().getTexture();
			if (text != null) {
				sceneShaderProgram.setUniformi("numCols", text.getNumCols());
				sceneShaderProgram.setUniformi("numRows", text.getNumRows());
			}
			
			shadowRenderer.bindTextures(GL_TEXTURE2);
			
			mesh.renderList(mapMeshes.get(mesh), (Entity gameItem) -> {
				sceneShaderProgram.setUniformf("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
				Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
				sceneShaderProgram.setUniform("modelNonInstancedMatrix", modelMatrix);
			});
		}
	}
	
	private void renderInstancedMeshes(Scene scene, Matrix4f viewMatrix) {
		sceneShaderProgram.setUniformi("isInstanced", 1);
		
		Map<InstancedMesh, List<Entity>> mapMeshes = scene.getGameInstancedMeshes();
		for (InstancedMesh mesh : mapMeshes.keySet()) {
			Texture text = mesh.getMaterial().getTexture();
			if (text != null) {
				sceneShaderProgram.setUniformi("numCols", text.getNumCols());
				sceneShaderProgram.setUniformi("numRows", text.getNumRows());
			}
			
			sceneShaderProgram.setUniformMat("material", mesh.getMaterial());
			
			filteredItems.clear();
			for (Entity gameItem : mapMeshes.get(mesh)) {
				if (gameItem.isInsideFrustum()) {
					filteredItems.add(gameItem);
				}
			}
			shadowRenderer.bindTextures(GL_TEXTURE2);
			
			mesh.renderListInstanced(filteredItems, transformation, viewMatrix);
		}
	}
	
	private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {
		
		sceneShaderProgram.setUniformv3f("ambientLight", sceneLight.getAmbientLight());
		sceneShaderProgram.setUniformf("specularPower", specularPower);
		
		// Process Point Lights
		PointLight[] pointLightList = sceneLight.getPointLightList();
		int numLights = pointLightList != null ? pointLightList.length : 0;
		for (int i = 0; i < numLights; i++) {
			// Get a copy of the point light object and transform its position to
			// view coordinates
			PointLight currPointLight = new PointLight(pointLightList[i]);
			Vector3f lightPos = currPointLight.getPosition();
			Vector4f aux = new Vector4f(lightPos, 1);
			aux.mul(viewMatrix);
			lightPos.x = aux.x;
			lightPos.y = aux.y;
			lightPos.z = aux.z;
			sceneShaderProgram.setUniformPl("pointLights", currPointLight, i);
		}
		
		// Process Spot Ligths
		SpotLight[] spotLightList = sceneLight.getSpotLightList();
		numLights = spotLightList != null ? spotLightList.length : 0;
		for (int i = 0; i < numLights; i++) {
			// Get a copy of the spot light object and transform its position and
			// cone direction to view coordinates
			SpotLight currSpotLight = new SpotLight(spotLightList[i]);
			Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
			dir.mul(viewMatrix);
			currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
			
			Vector3f lightPos = currSpotLight.getPointLight().getPosition();
			Vector4f aux = new Vector4f(lightPos, 1);
			aux.mul(viewMatrix);
			lightPos.x = aux.x;
			lightPos.y = aux.y;
			lightPos.z = aux.z;
			
			sceneShaderProgram.setUniformSli("spotLights", currSpotLight, i);
		}
		
		// Get a copy of the directional light object and transform its position
		// to view coordinates
		DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
		Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
		dir.mul(viewMatrix);
		currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
		sceneShaderProgram.setUniformDl("directionalLight", currDirLight);
	}
	
	private void renderCrossHair(Window window) {
		if (window.getWindowOptions().compatibleProfile) {
			glPushMatrix();
			glLoadIdentity();
			
			float inc = 0.05f;
			glLineWidth(2.0f);
			
			glBegin(GL_LINES);
			
			glColor3f(1.0f, 1.0f, 1.0f);
			
			// Horizontal line
			glVertex3f(-inc, 0.0f, 0.0f);
			glVertex3f(+inc, 0.0f, 0.0f);
			glEnd();
			
			// Vertical line
			glBegin(GL_LINES);
			glVertex3f(0.0f, -inc, 0.0f);
			glVertex3f(0.0f, +inc, 0.0f);
			glEnd();
			
			glPopMatrix();
		}
	}
	
	/**
	 * Renders the three axis in space (For debugging purposes only
	 *
	 * @param camera
	 */
	private void renderAxes(Window window, Camera camera) {
		Window.WindowOptions opts = window.getWindowOptions();
		if (opts.compatibleProfile) {
			glPushMatrix();
			glLoadIdentity();
			float rotX = camera.getRotation().x;
			float rotY = camera.getRotation().y;
			float rotZ = 0;
			glRotatef(rotX, 1.0f, 0.0f, 0.0f);
			glRotatef(rotY, 0.0f, 1.0f, 0.0f);
			glRotatef(rotZ, 0.0f, 0.0f, 1.0f);
			glLineWidth(2.0f);
			
			glBegin(GL_LINES);
			// X Axis
			glColor3f(1.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(1.0f, 0.0f, 0.0f);
			// Y Axis
			glColor3f(0.0f, 1.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 1.0f, 0.0f);
			// Z Axis
			glColor3f(1.0f, 1.0f, 1.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 1.0f);
			glEnd();
			
			glPopMatrix();
		}
	}
	
	public void cleanup() {
		if (shadowRenderer != null) {
			shadowRenderer.cleanup();
		}
		if (skyBoxShaderProgram != null) {
			skyBoxShaderProgram.cleanup();
		}
		if (sceneShaderProgram != null) {
			sceneShaderProgram.cleanup();
		}
	}
}
