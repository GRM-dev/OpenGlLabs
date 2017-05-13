package eu.grmdev.senryaku.graphic;

import static org.lwjgl.opengl.GL30.*;

import lombok.Getter;

public class VertexArrayObject {
	@Getter
	private int id;
	
	public VertexArrayObject() {
		id = glGenVertexArrays();
	}
	
	public void bind() {
		glBindVertexArray(id);
	}
	
	public void remove() {
		glDeleteVertexArrays(id);
	}
}
