package eu.grmdev.senryaku.graphic.particles;

import lombok.Getter;

public enum ParticleType {
	FIRE("/models/particle.obj","/textures/particle_anim.png") ,
	STATIC("","");
	
	private @Getter String res;
	private @Getter String tex;
	
	private ParticleType(String res, String tex) {
		this.res = res;
		this.tex = tex;
	}
}
