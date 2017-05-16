package eu.grmdev.senryaku.graphic.material;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import eu.grmdev.senryaku.core.Utils;
import lombok.Getter;

public class Texture {
	@Getter
	private final int id;
	@Getter
	private final int width;
	@Getter
	private final int height;
	@Getter
	private int numRows = 1;
	@Getter
	private int numCols = 1;
	
	/**
	 * Creates an empty texture.
	 *
	 * @param width
	 *           Width of the texture
	 * @param height
	 *           Height of the texture
	 * @param pixelFormat
	 *           Specifies the format of the pixel data (GL_RGBA, etc.)
	 * @throws Exception
	 */
	public Texture(int width, int height, int pixelFormat) throws Exception {
		this.id = glGenTextures();
		this.width = width;
		this.height = height;
		glBindTexture(GL_TEXTURE_2D, this.id);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, this.width, this.height, 0, pixelFormat, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}
	
	public Texture(String fileName, int numCols, int numRows) throws Exception {
		this(fileName);
		this.numCols = numCols;
		this.numRows = numRows;
	}
	
	public Texture(String fileName) throws Exception {
		this(Utils.ioResourceToByteBuffer(fileName, 1024));
	}
	
	public Texture(ByteBuffer imageData) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer avChannels = stack.mallocInt(1);
			
			ByteBuffer decodedImage = stbi_load_from_memory(imageData, w, h, avChannels, 4);
			
			this.width = w.get();
			this.height = h.get();
			
			this.id = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, this.id);
			
			// Tell OpenGL how to unpack the RGBA bytes
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage);
			glGenerateMipmap(GL_TEXTURE_2D);
		}
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void cleanup() {
		glDeleteTextures(id);
	}
}
