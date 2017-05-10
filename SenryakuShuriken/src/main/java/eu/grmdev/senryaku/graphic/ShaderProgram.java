package eu.grmdev.senryaku.graphic;

import static eu.grmdev.senryaku.graphic.util.DemoUtils.createShader;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

import java.io.IOException;

import lombok.Getter;

public class ShaderProgram {
	@Getter
	private int programId;
	private int vshader;
	private int fshader;
	private int vec3ArrayUniform;
	private int chosenUniform;
	
	public ShaderProgram(String vertexShader, String fragmentShader) throws IOException {
		programId = glCreateProgram();
		vshader = createShader(vertexShader, GL_VERTEX_SHADER);
		fshader = createShader(fragmentShader, GL_FRAGMENT_SHADER);
		glAttachShader(programId, vshader);
		glAttachShader(programId, fshader);
		
		glBindAttribLocation(programId, 0, "position");
		glBindFragDataLocation(programId, 0, "color");
		glLinkProgram(programId);
		int linked = glGetProgrami(programId, GL_LINK_STATUS);
		String programLog = glGetProgramInfoLog(programId);
		if (programLog != null && programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) { throw new AssertionError("Could not link program of shaders: {" + vertexShader + ", " + fragmentShader + "}"); }
		vec3ArrayUniform = glGetUniformLocation(programId, "cols");
		chosenUniform = glGetUniformLocation(programId, "chosen");
	}
	
	public void use() {
		glUseProgram(programId);
	}
	
	public void update(Object... params) {
		// TODO Auto-generated method stub
		
	}
	
}
