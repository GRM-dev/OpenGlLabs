package eu.grmdev.senryaku.core.entity;

import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import lombok.Getter;

public class VertexBufferObject {
	@Getter
	private int id;
	@Getter
	private int size;
	
	public VertexBufferObject() {
		id = glGenBuffers();
	}
	
	public void bind(int target) {
		glBindBuffer(target, id);
	}
	
	public void setData(int target, float[] data, int usage) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
		buf.put(data);
		buf.flip();
		glBufferData(target, buf, usage);
	}
	
	public void setData(int target, int[] indices, int usage) {
		IntBuffer buf = BufferUtils.createIntBuffer(indices.length);
		buf.put(indices);
		buf.flip();
		setData(target, buf, usage);
	}
	
	public void setData(int target, IntBuffer data, int usage) {
		glBufferData(target, data, usage);
	}
	
	public void setData(int target, FloatBuffer data, int usage) {
		glBufferData(target, data, usage);
	}
	
	public void unbind(int target) {
		glBindBuffer(target, 0);
	}
	
	public void remove() {
		glDeleteBuffers(id);
	}
}
