package eu.grmdev.senryaku.core.entity;

import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import lombok.Getter;

public class VertexBufferObject {
	@Getter
	private int id;
	
	public VertexBufferObject() {
		id = glGenBuffers();
	}
	
	public void bind(int target) {
		glBindBuffer(target, id);
	}
	
	public void setData(int target, FloatBuffer data, int usage) {
		glBufferData(target, data, usage);
	}
	
	public void remove() {
		glDeleteBuffers(id);
	}
}
