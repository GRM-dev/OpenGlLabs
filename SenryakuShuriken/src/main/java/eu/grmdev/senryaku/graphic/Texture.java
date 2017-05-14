package eu.grmdev.senryaku.graphic;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

public class Texture {
	private int id;
	private int width;
	private int height;
	
	private Texture() {
		id = glGenTextures();
	}
	
	private Texture(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void setData(ByteBuffer data) {
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	
	public void setData(int width, int height, ByteBuffer data) {
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
		this.width = width;
		this.height = height;
	}
	
	public void setParam(int name, int value) {
		glTexParameteri(GL_TEXTURE_2D, name, value);
	}
	
	public void delete() {
		glDeleteTextures(id);
	}
	
	public static Texture loadTexture(String file) {
		ByteBuffer img;
		int width, height;
		File f;
		try {
			f = new File(Texture.class.getResource("/textures/" + file + ".png").toURI());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException("Failed creating texture " + file);
		}
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);
			
			stbi_set_flip_vertically_on_load(true);
			img = stbi_load(f.toString(), w, h, comp, 4);
			if (img == null) { throw new RuntimeException("Failed creating texture " + file + " Tex exists:" + f.exists()); }
			
			width = w.get();
			height = h.get();
		}
		
		Texture tex = new Texture(width, height);
		tex.bind();
		tex.setParam(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		tex.setParam(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		tex.setParam(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		tex.setParam(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		tex.setData(img);
		
		return tex;
	}
}
