package eu.grmdev.senryaku.core.entity;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import eu.grmdev.senryaku.core.events.KeyEvent;
import eu.grmdev.senryaku.graphic.GameWindow;

public class Player extends Entity {
	private FloatBuffer colors = BufferUtils.createFloatBuffer(3 * 4);
	{
		colors.put(1).put(0).put(0); // red
		colors.put(0).put(1).put(0); // green
		colors.put(0).put(0).put(1); // blue
		colors.put(1).put(1).put(0); // yellow
		colors.flip();
	}
	
	public Player() {
		attachedToCamera = true;
		addKeyListener(event -> {
			KeyEvent ke = (KeyEvent) event;
			if (ke.getKey() == GLFW_KEY_A) {
				transformation.rotate(0.0f, -5.0f, 0.0f);
			}
		});
	}
	
	@Override
	public void init() {
		mesh.init();
		mesh.bind();
		try (MemoryStack stack = stackPush()) {
			FloatBuffer vertices = memAllocFloat(3 * 3);
			vertices.put(-0.5f).put(-0.5f).put(-0.5f);
			vertices.put(+0.5f).put(-0.5f).put(-0.5f);
			vertices.put(+0.0f).put(+0.5f).put(-0.5f);
			vertices.flip();
			
			VertexBufferObject vert_vbo = new VertexBufferObject();
			vert_vbo.bind(GL_ARRAY_BUFFER);
			vert_vbo.setData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
			mesh.setVert_vbo(vert_vbo);
		}
		setInitialized(true);
	}
	
	@Override
	protected void draw(GameWindow window) {
		window.getShaderHandler().update(colors, 1);
		glVertexPointer(3, GL_FLOAT, 0, 0L);
		glEnableClientState(GL_VERTEX_ARRAY);
		glDrawArrays(GL_TRIANGLES, 0, 3);
		glDisableClientState(GL_VERTEX_ARRAY);
	}
	
}
