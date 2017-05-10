package eu.grmdev.senryaku.core.handlers;

import static org.lwjgl.opengl.GL20.glUseProgram;

import eu.grmdev.senryaku.graphic.ShaderProgram;

public class ShaderHandler {
	// @formatter:off
	private String[][] shaders = new String[][]{
		{"/shaders/uniformarray-vs.glsl", "/shaders/uniformarray-fs.glsl"}
	};
	private int vec3ArrayUniform;
	// @formatter:on
	private ShaderProgram[] shaderPrograms = new ShaderProgram[1];
	private int chosenUniform;
	private ShaderProgram currentProgram;
	
	public void init() {
		for (int i = 0; i < shaderPrograms.length; i++) {
			try {
				ShaderProgram program = new ShaderProgram(shaders[i][0], shaders[i][1]);
				shaderPrograms[i] = program;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void use(int i) {
		if (i < 0 || i > shaderPrograms.length) { throw new ArrayIndexOutOfBoundsException("There is no program for id: " + 0); }
		if (i == 0) {
			glUseProgram(0);
			currentProgram = null;
		}
		else {
			currentProgram = shaderPrograms[i - 1];
			currentProgram.use();
		}
	}
	
	public void update(Object... params) {
		if (currentProgram != null) {
			currentProgram.update(params);
		}
	}
	
}
