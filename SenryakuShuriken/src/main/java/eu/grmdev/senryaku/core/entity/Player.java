package eu.grmdev.senryaku.core.entity;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

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
	
	@Override
	public void init() {
		try (MemoryStack stack = stackPush()) {
			FloatBuffer buffer = memAllocFloat(3 * 2);
			buffer.put(-0.5f).put(-0.5f);
			buffer.put(+0.5f).put(-0.5f);
			buffer.put(+0.0f).put(+0.5f);
			buffer.flip();
			int vbo = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
			glVertexPointer(2, GL_FLOAT, 0, 0L);
		}
		setInitialized(true);
	}
	
	@Override
	protected void draw(GameWindow window) {
		window.getShaderHandler().update(colors, 2);
		glDrawArrays(GL_TRIANGLES, 0, 3);
	}
	
}
