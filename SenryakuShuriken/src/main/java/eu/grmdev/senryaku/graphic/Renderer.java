package eu.grmdev.senryaku.graphic;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.*;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.Main;
import eu.grmdev.senryaku.core.Scene;
import eu.grmdev.senryaku.core.SceneLight;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.SkyBox;
import eu.grmdev.senryaku.core.map.*;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.effects.shadow.ShadowCascade;
import eu.grmdev.senryaku.graphic.effects.shadow.ShadowRenderer;
import eu.grmdev.senryaku.graphic.lights.*;
import eu.grmdev.senryaku.graphic.material.Texture;
import eu.grmdev.senryaku.graphic.particles.IParticleEmitter;

public class Renderer {
	private final Transformation transformation;
	private final ShadowRenderer shadowRenderer;
	private ShaderProgram sceneShaderProgram;
	private ShaderProgram skyBoxShaderProgram;
	private ShaderProgram particlesShaderProgram;
	private final float specularPower;
	private final FrustumCullingFilter frustumFilter;
	private final ConcurrentHashMap<Integer, Entity> filteredItems;
	private int nextId = 0;
	
	public Renderer() {
		transformation = new Transformation();
		specularPower = 10f;
		shadowRenderer = new ShadowRenderer();
		frustumFilter = new FrustumCullingFilter();
		filteredItems = new ConcurrentHashMap<>();
	}
	
	public void init(Window window) throws Exception {
		shadowRenderer.init(window);
		setupSkyBoxShader();
		setupSceneShader();
		setupParticlesShader();
	}
	
	private void setupSkyBoxShader() throws Exception {
		skyBoxShaderProgram = new ShaderProgram();
		skyBoxShaderProgram.createVertexShader(Utils.loadResourceContent("/shaders/sb_vertex.vs"));
		skyBoxShaderProgram.createFragmentShader(Utils.loadResourceContent("/shaders/sb_fragment.fs"));
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
		sceneShaderProgram.createVertexShader(Utils.loadResourceContent("/shaders/scene_vertex.vs"));
		sceneShaderProgram.createFragmentShader(Utils.loadResourceContent("/shaders/scene_fragment.fs"));
		sceneShaderProgram.link();
		
		sceneShaderProgram.createUniform("viewMatrix");
		sceneShaderProgram.createUniform("projectionMatrix");
		sceneShaderProgram.createUniform("texture_sampler");
		sceneShaderProgram.createUniform("normalMap");
		sceneShaderProgram.createMaterialUniform("material");
		sceneShaderProgram.createUniform("specularPower");
		sceneShaderProgram.createUniform("ambientLight");
		sceneShaderProgram.createPointLightListUniform("pointLights", Config.MAX_POINT_LIGHTS.<Integer> get());
		sceneShaderProgram.createSpotLightListUniform("spotLights", Config.MAX_SPOT_LIGHTS.<Integer> get());
		sceneShaderProgram.createDirectionalLightUniform("directionalLight");
		sceneShaderProgram.createFogUniform("fog");
		
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES.<Integer> get(); i++) {
			sceneShaderProgram.createUniform("shadowMap_" + i);
		}
		sceneShaderProgram.createUniform("orthoProjectionMatrix", Config.NUM_SHADOW_CASCADES.<Integer> get());
		sceneShaderProgram.createUniform("modelNonInstancedMatrix");
		sceneShaderProgram.createUniform("lightViewMatrix", Config.NUM_SHADOW_CASCADES.<Integer> get());
		sceneShaderProgram.createUniform("cascadeFarPlanes", Config.NUM_SHADOW_CASCADES.<Integer> get());
		sceneShaderProgram.createUniform("renderShadow");
		
		sceneShaderProgram.createUniform("jointsMatrix");
		
		sceneShaderProgram.createUniform("isInstanced");
		sceneShaderProgram.createUniform("numCols");
		sceneShaderProgram.createUniform("numRows");
		
		sceneShaderProgram.createUniform("selectedNonInstanced");
	}
	
	private void setupParticlesShader() throws Exception {
		particlesShaderProgram = new ShaderProgram();
		particlesShaderProgram.createVertexShader(Utils.loadResourceContent("/shaders/particles_vertex.vs"));
		particlesShaderProgram.createFragmentShader(Utils.loadResourceContent("/shaders/particles_fragment.fs"));
		particlesShaderProgram.link();
		
		particlesShaderProgram.createUniform("viewMatrix");
		particlesShaderProgram.createUniform("projectionMatrix");
		particlesShaderProgram.createUniform("texture_sampler");
		
		particlesShaderProgram.createUniform("numCols");
		particlesShaderProgram.createUniform("numRows");
	}
	
	public void render(Window window, Camera camera, Scene scene, LevelManager levelManager) {
		clear();
		
		if (window.getWindowOptions().frustumCulling) {
			frustumFilter.updateFrustum(window.getProjectionMatrix(), camera.getViewMatrix());
			frustumFilter.filter(scene.getEntityMeshes());
			frustumFilter.filter(scene.getGameInstancedMeshes());
		}
		
		if (scene.isRenderShadows()) {
			shadowRenderer.render(window, scene, camera, transformation, this);
		}
		
		glViewport(0, 0, window.getWidth(), window.getHeight());
		window.updateProjectionMatrix();
		
		renderScene(window, camera, scene, levelManager);
		renderSkyBox(window, camera, scene);
		renderParticles(window, camera, scene);
		
		if (Main.DEBUG) {
			renderAxes(window, camera);
			renderCrossHair(window);
		}
	}
	
	private void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	}
	
	private void renderScene(Window window, Camera camera, Scene scene, LevelManager levelManager) {
		sceneShaderProgram.bind();
		
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f projectionMatrix = window.getProjectionMatrix();
		sceneShaderProgram.setUniformm4f("viewMatrix", viewMatrix);
		sceneShaderProgram.setUniformm4f("projectionMatrix", projectionMatrix);
		
		List<ShadowCascade> shadowCascades = shadowRenderer.getShadowCascades();
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES.<Integer> get(); i++) {
			ShadowCascade shadowCascade = shadowCascades.get(i);
			sceneShaderProgram.setUniformm4fi("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix(), i);
			sceneShaderProgram.setUniformfi("cascadeFarPlanes", Config.SHADOW_CASCADE_SPLITS.getArray()[i], i);
			sceneShaderProgram.setUniformm4fi("lightViewMatrix", shadowCascade.getLightViewMatrix(), i);
		}
		
		SceneLight sceneLight = scene.getSceneLight();
		renderLights(viewMatrix, sceneLight);
		
		renderEffects(scene);
		
		try {
			renderGameMap(window, camera, levelManager.getCurrentMap());
			renderNonInstancedMeshes(scene);
			renderInstancedMeshes(scene, viewMatrix);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		sceneShaderProgram.unbind();
	}
	
	private void renderEffects(Scene scene) {
		sceneShaderProgram.setUniformFog("fog", scene.getFog());
		sceneShaderProgram.setUniformi("texture_sampler", 0);
		sceneShaderProgram.setUniformi("normalMap", 1);
		int start = 2;
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES.<Integer> get(); i++) {
			sceneShaderProgram.setUniformi("shadowMap_" + i, start + i);
		}
		sceneShaderProgram.setUniformi("renderShadow", scene.isRenderShadows() ? 1 : 0);
	}
	
	private void renderSkyBox(Window window, Camera camera, Scene scene) {
		SkyBox skyBox = scene.getSkyBox();
		if (skyBox != null) {
			skyBoxShaderProgram.bind();
			
			skyBoxShaderProgram.setUniformi("texture_sampler", 0);
			
			Matrix4f projectionMatrix = window.getProjectionMatrix();
			skyBoxShaderProgram.setUniformm4f("projectionMatrix", projectionMatrix);
			Matrix4f viewMatrix = camera.getViewMatrix();
			float m30 = viewMatrix.m30();
			viewMatrix.m30(0);
			float m31 = viewMatrix.m31();
			viewMatrix.m31(0);
			float m32 = viewMatrix.m32();
			viewMatrix.m32(0);
			
			Mesh mesh = skyBox.getMesh();
			Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
			skyBoxShaderProgram.setUniformm4f("modelViewMatrix", modelViewMatrix);
			skyBoxShaderProgram.setUniformv3f("ambientLight", scene.getSceneLight().getSkyBoxLight());
			skyBoxShaderProgram.setUniformv4f("colour", mesh.getMaterial().getAmbientColor());
			skyBoxShaderProgram.setUniformi("hasTexture", mesh.getMaterial().isTextured() ? 1 : 0);
			glDisable(GL_LIGHTING);
			mesh.render();
			glDisable(GL_LIGHTING);
			
			viewMatrix.m30(m30);
			viewMatrix.m31(m31);
			viewMatrix.m32(m32);
			skyBoxShaderProgram.unbind();
		}
	}
	
	private void renderGameMap(Window window, Camera camera, GameMap map) throws Exception {
		if (map != null) {
			if (!map.isInitialized()) {
				map.init();
			}
			Terrain terrain = map.getTerrain();
			sceneShaderProgram.setUniformi("isInstanced", 0);
			Mesh terrainMesh = terrain.getMesh();
			sceneShaderProgram.setUniformMat("material", terrainMesh.getMaterial());
			Texture texture = terrainMesh.getMaterial().getTexture();
			if (texture != null) {
				sceneShaderProgram.setUniformi("numCols", texture.getNumCols());
				sceneShaderProgram.setUniformi("numRows", texture.getNumRows());
			}
			shadowRenderer.bindTextures(GL_TEXTURE2);
			Matrix4f modelMatrix = transformation.buildModelMatrix(terrain);
			sceneShaderProgram.setUniformm4f("modelNonInstancedMatrix", modelMatrix);
			terrainMesh.render();
			
			for (Mesh mesh : terrain.getEntitiesByMesh().keySet())
				renderTiles(mesh, terrain.getEntitiesByMesh().get(mesh));
		}
	}
	
	private void renderTiles(Mesh mesh, List<Entity> entities) {
		sceneShaderProgram.setUniformMat("material", mesh.getMaterial());
		Texture texture = mesh.getMaterial().getTexture();
		if (texture != null) {
			sceneShaderProgram.setUniformi("numCols", texture.getNumCols());
			sceneShaderProgram.setUniformi("numRows", texture.getNumRows());
		}
		shadowRenderer.bindTextures(GL_TEXTURE2);
		mesh.renderList(entities, (Entity entity) -> {
			sceneShaderProgram.setUniformf("selectedNonInstanced", entity.isSelected() ? 1.0f : 0.0f);
			Matrix4f modelMatrix = transformation.buildModelMatrix(entity);
			sceneShaderProgram.setUniformm4f("modelNonInstancedMatrix", modelMatrix);
		});
	}
	
	private void renderNonInstancedMeshes(Scene scene) {
		sceneShaderProgram.setUniformi("isInstanced", 0);
		
		ConcurrentHashMap<Mesh, List<Entity>> mapMeshes = scene.getEntityMeshes();
		for (Mesh mesh : mapMeshes.keySet()) {
			sceneShaderProgram.setUniformMat("material", mesh.getMaterial());
			Texture text = mesh.getMaterial().getTexture();
			if (text != null) {
				sceneShaderProgram.setUniformi("numCols", text.getNumCols());
				sceneShaderProgram.setUniformi("numRows", text.getNumRows());
			}
			
			shadowRenderer.bindTextures(GL_TEXTURE2);
			
			mesh.renderList(mapMeshes.get(mesh), (Entity entity) -> {
				sceneShaderProgram.setUniformf("selectedNonInstanced", entity.isSelected() ? 1.0f : 0.0f);
				Matrix4f modelMatrix = transformation.buildModelMatrix(entity);
				sceneShaderProgram.setUniformm4f("modelNonInstancedMatrix", modelMatrix);
			});
		}
	}
	
	private void renderInstancedMeshes(Scene scene, Matrix4f viewMatrix) {
		sceneShaderProgram.setUniformi("isInstanced", 1);
		
		ConcurrentHashMap<InstancedMesh, List<Entity>> mapMeshes = scene.getGameInstancedMeshes();
		for (InstancedMesh mesh : mapMeshes.keySet()) {
			Texture text = mesh.getMaterial().getTexture();
			if (text != null) {
				sceneShaderProgram.setUniformi("numCols", text.getNumCols());
				sceneShaderProgram.setUniformi("numRows", text.getNumRows());
			}
			
			sceneShaderProgram.setUniformMat("material", mesh.getMaterial());
			
			filteredItems.clear();
			nextId = 0;
			for (Entity gameItem : mapMeshes.get(mesh)) {
				if (gameItem.isInsideFrustum()) {
					filteredItems.put(nextId++, gameItem);
				}
			}
			shadowRenderer.bindTextures(GL_TEXTURE2);
			
			mesh.renderListInstanced(filteredItems, transformation, viewMatrix);
		}
	}
	
	private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {
		sceneShaderProgram.setUniformv3f("ambientLight", sceneLight.getAmbientLight());
		sceneShaderProgram.setUniformf("specularPower", specularPower);
		
		// Point Lights
		PointLight[] pointLightList = sceneLight.getPointLightList();
		int numLights = pointLightList != null ? pointLightList.length : 0;
		for (int i = 0; i < numLights; i++) {
			PointLight currPointLight = new PointLight(pointLightList[i]);
			Vector3f lightPos = currPointLight.getPosition();
			Vector4f aux = new Vector4f(lightPos, 1);
			aux.mul(viewMatrix);
			lightPos.x = aux.x;
			lightPos.y = aux.y;
			lightPos.z = aux.z;
			sceneShaderProgram.setUniformPl("pointLights", currPointLight, i);
		}
		
		// Spot Ligths
		SpotLight[] spotLightList = sceneLight.getSpotLightList();
		numLights = spotLightList != null ? spotLightList.length : 0;
		for (int i = 0; i < numLights; i++) {
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
		
		DirectionalLight directionalLight = sceneLight.getDirectionalLight();
		DirectionalLight currDirLight = new DirectionalLight(directionalLight);
		Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
		dir.mul(viewMatrix);
		currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
		sceneShaderProgram.setUniformDl("directionalLight", currDirLight);
	}
	
	private void renderParticles(Window window, Camera camera, Scene scene) {
		particlesShaderProgram.bind();
		
		Matrix4f viewMatrix = camera.getViewMatrix();
		particlesShaderProgram.setUniformm4f("viewMatrix", viewMatrix);
		particlesShaderProgram.setUniformi("texture_sampler", 0);
		Matrix4f projectionMatrix = window.getProjectionMatrix();
		particlesShaderProgram.setUniformm4f("projectionMatrix", projectionMatrix);
		
		glDepthMask(false);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		
		IParticleEmitter[] emitters = scene.getParticleEmitters();
		int numEmitters = emitters != null ? emitters.length : 0;
		for (int i = 0; i < numEmitters; i++) {
			IParticleEmitter emitter = emitters[i];
			if (emitter.isActive()) {
				InstancedMesh mesh = (InstancedMesh) emitter.getBaseParticle().getMesh();
				
				Texture text = mesh.getMaterial().getTexture();
				particlesShaderProgram.setUniformi("numCols", text.getNumCols());
				particlesShaderProgram.setUniformi("numRows", text.getNumRows());
				
				mesh.renderListInstanced(emitter.getParticles(), true, transformation, viewMatrix);
			}
		}
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(true);
		
		particlesShaderProgram.unbind();
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
		if (particlesShaderProgram != null) {
			particlesShaderProgram.cleanup();
		}
	}
}
