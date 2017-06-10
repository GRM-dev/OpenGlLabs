package eu.grmdev.senryaku.graphic.effects.shadow;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.graphic.material.ArrTexture;
import lombok.Getter;

public class ShadowBuffer {
	/** depth map FBO */
	@Getter
	private final int depthMapFBO;
	/** depth map texture */
	@Getter
	private final ArrTexture depthMapTexture;
	
	public ShadowBuffer() throws Exception {
		depthMapFBO = glGenFramebuffers();
		depthMapTexture = new ArrTexture(Config.NUM_SHADOW_CASCADES.<Integer> get(), Config.SHADOW_MAP_WIDTH.<Integer> get(), Config.SHADOW_MAP_HEIGHT.<Integer> get(), GL_DEPTH_COMPONENT);
		
		glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMapTexture.getIds()[0], 0);
		
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) { throw new Exception("Could not create FrameBuffer"); }
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void bindTextures(int start) {
		for (int i = 0; i < Config.NUM_SHADOW_CASCADES.<Integer> get(); i++) {
			glActiveTexture(start + i);
			glBindTexture(GL_TEXTURE_2D, depthMapTexture.getIds()[i]);
		}
	}
	
	public void cleanup() {
		glDeleteFramebuffers(depthMapFBO);
		depthMapTexture.cleanup();
	}
}
