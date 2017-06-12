package eu.grmdev.senryaku.graphic;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import eu.grmdev.senryaku.graphic.effects.Fog;
import eu.grmdev.senryaku.graphic.lights.*;
import eu.grmdev.senryaku.graphic.material.Material;

public class ShaderProgram {
	private final int id;
	private int vertexShaderId;
	private int fragmentShaderId;
	private int geometryShaderId;
	private final Map<String, Integer> uniforms;
	
	public ShaderProgram() throws Exception {
		id = glCreateProgram();
		if (id == 0) { throw new Exception("Could not create Shader"); }
		uniforms = new HashMap<>();
	}
	
	public void createVertexShader(String shaderCode) throws Exception {
		vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
	}
	
	public void createFragmentShader(String shaderCode) throws Exception {
		fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
	}
	
	private int createShader(String shaderCode, int shaderType) throws Exception {
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0) { throw new Exception("Error creating shader. Type: " + shaderType); }
		
		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);
		
		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) { throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024)); }
		
		glAttachShader(id, shaderId);
		
		return shaderId;
	}
	
	public void link() throws Exception {
		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) == 0) { throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(id, 1024)); }
		
		if (vertexShaderId != 0) {
			glDetachShader(id, vertexShaderId);
		}
		if (geometryShaderId != 0) {
			glDetachShader(id, geometryShaderId);
		}
		if (fragmentShaderId != 0) {
			glDetachShader(id, fragmentShaderId);
		}
		
		glValidateProgram(id);
		if (glGetProgrami(id, GL_VALIDATE_STATUS) == 0) {
			System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(id, 1024));
		}
	}
	
	public void bind() {
		glUseProgram(id);
	}
	
	public void unbind() {
		glUseProgram(0);
	}
	
	public void cleanup() {
		unbind();
		if (id != 0) {
			glDeleteProgram(id);
		}
	}
	
	public void createUniform(String uniformName) throws Exception {
		int uniformLocation = glGetUniformLocation(id, uniformName);
		if (uniformLocation < 0) { throw new Exception("Could not find uniform:" + uniformName); }
		uniforms.put(uniformName, uniformLocation);
	}
	
	public void createUniform(String uniformName, int size) throws Exception {
		for (int i = 0; i < size; i++) {
			createUniform(uniformName + "[" + i + "]");
		}
	}
	
	public void createPointLightListUniform(String uniformName, int size) throws Exception {
		for (int i = 0; i < size; i++) {
			createPointLightUniform(uniformName + "[" + i + "]");
		}
	}
	
	public void createPointLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".att.constant");
		createUniform(uniformName + ".att.linear");
		createUniform(uniformName + ".att.exponent");
	}
	
	public void createSpotLightListUniform(String uniformName, int size) throws Exception {
		for (int i = 0; i < size; i++) {
			createSpotLightUniform(uniformName + "[" + i + "]");
		}
	}
	
	public void createSpotLightUniform(String uniformName) throws Exception {
		createPointLightUniform(uniformName + ".pl");
		createUniform(uniformName + ".conedir");
		createUniform(uniformName + ".cutoff");
	}
	
	public void createDirectionalLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".direction");
		createUniform(uniformName + ".intensity");
	}
	
	public void createMaterialUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".ambient");
		createUniform(uniformName + ".diffuse");
		createUniform(uniformName + ".specular");
		createUniform(uniformName + ".hasTexture");
		createUniform(uniformName + ".hasNormalMap");
		createUniform(uniformName + ".reflectance");
	}
	
	public void createFogUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".activeFog");
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".density");
	}
	
	public void setUniformm4f(String uniformName, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
		}
	}
	
	public void setUniformm4fi(String uniformName, Matrix4f value, int index) {
		setUniformm4f(uniformName + "[" + index + "]", value);
	}
	
	public void setUniformm4fa(String uniformName, Matrix4f[] matrices) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			int length = matrices != null ? matrices.length : 0;
			FloatBuffer fb = stack.mallocFloat(16 * length);
			for (int i = 0; i < length; i++) {
				matrices[i].get(16 * i, fb);
			}
			glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
		}
	}
	
	public void setUniformi(String uniformName, int value) {
		glUniform1i(uniforms.get(uniformName), value);
	}
	
	public void setUniformf(String uniformName, float value) {
		glUniform1f(uniforms.get(uniformName), value);
	}
	
	public void setUniformfi(String uniformName, float value, int index) {
		setUniformf(uniformName + "[" + index + "]", value);
	}
	
	public void setUniformv3f(String uniformName, Vector3f value) {
		glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
	}
	
	public void setUniformv4f(String uniformName, Vector4f value) {
		glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
	}
	
	public void setUniformPla(String uniformName, PointLight[] pointLights) {
		int numLights = pointLights != null ? pointLights.length : 0;
		for (int i = 0; i < numLights; i++) {
			setUniformPl(uniformName, pointLights[i], i);
		}
	}
	
	public void setUniformPl(String uniformName, PointLight pointLight, int pos) {
		setUniformPl(uniformName + "[" + pos + "]", pointLight);
	}
	
	public void setUniformPl(String uniformName, PointLight pointLight) {
		setUniformv3f(uniformName + ".colour", pointLight.getColor());
		setUniformv3f(uniformName + ".position", pointLight.getPosition());
		setUniformf(uniformName + ".intensity", pointLight.getIntensity());
		PointLight.Attenuation att = pointLight.getAttenuation();
		setUniformf(uniformName + ".att.constant", att.getConstant());
		setUniformf(uniformName + ".att.linear", att.getLinear());
		setUniformf(uniformName + ".att.exponent", att.getExponent());
	}
	
	public void setUniformSla(String uniformName, SpotLight[] spotLights) {
		int numLights = spotLights != null ? spotLights.length : 0;
		for (int i = 0; i < numLights; i++) {
			setUniformSli(uniformName, spotLights[i], i);
		}
	}
	
	public void setUniformSli(String uniformName, SpotLight spotLight, int pos) {
		setUniformSl(uniformName + "[" + pos + "]", spotLight);
	}
	
	public void setUniformSl(String uniformName, SpotLight spotLight) {
		setUniformPl(uniformName + ".pl", spotLight.getPointLight());
		setUniformv3f(uniformName + ".conedir", spotLight.getConeDirection());
		setUniformf(uniformName + ".cutoff", spotLight.getCutOff());
	}
	
	public void setUniformDl(String uniformName, DirectionalLight dirLight) {
		setUniformv3f(uniformName + ".colour", dirLight.getColor());
		setUniformv3f(uniformName + ".direction", dirLight.getDirection());
		setUniformf(uniformName + ".intensity", dirLight.getIntensity());
	}
	
	public void setUniformMat(String uniformName, Material material) {
		setUniformv4f(uniformName + ".ambient", material.getAmbientColor());
		setUniformv4f(uniformName + ".diffuse", material.getDiffuseColor());
		setUniformv4f(uniformName + ".specular", material.getSpecularColor());
		setUniformi(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
		setUniformi(uniformName + ".hasNormalMap", material.hasNormalMap() ? 1 : 0);
		setUniformf(uniformName + ".reflectance", material.getReflectance());
	}
	
	public void setUniformFog(String uniformName, Fog fog) {
		setUniformi(uniformName + ".activeFog", fog.isActive() ? 1 : 0);
		setUniformv3f(uniformName + ".colour", fog.getColor());
		setUniformf(uniformName + ".density", fog.getDensity());
	}
}
