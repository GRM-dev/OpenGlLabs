package eu.grmdev.senryaku.core.entity;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import eu.grmdev.senryaku.graphic.Texture;
import eu.grmdev.senryaku.graphic.VertexArrayObject;
import lombok.Setter;

public class Mesh {
	private VertexArrayObject vao;
	@Setter
	private VertexBufferObject vert_vbo;
	@Setter
	private VertexBufferObject ind_vbo;
	@Setter
	private VertexBufferObject tex_vbo;
	@Setter
	private Texture texture;
	
	public Mesh() {}
	
	public void init() {
		vao = new VertexArrayObject();
	}
	
	public void bind() {
		vao.bind();
	}
	
	public void unbind() {
		vao.unbind();
	}
	
	public void delete() {
		if (vert_vbo != null) {
			vert_vbo.remove();
		}
		if (ind_vbo != null) {
			ind_vbo.remove();
		}
		if (tex_vbo != null) {
			tex_vbo.remove();
		}
		if (vao != null) {
			vao.remove();
		}
	}
	
	public void render(Entity entity) {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		vert_vbo.bind(GL_ARRAY_BUFFER);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L);
		
		tex_vbo.bind(GL_ELEMENT_ARRAY_BUFFER);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0L);
		
		ind_vbo.bind(GL_ARRAY_BUFFER);
		glDrawElements(GL_TRIANGLES, ind_vbo.getSize(), GL_UNSIGNED_INT, 0);
		
		tex_vbo.unbind(GL_ELEMENT_ARRAY_BUFFER);
		vert_vbo.unbind(GL_ARRAY_BUFFER);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
	}
}