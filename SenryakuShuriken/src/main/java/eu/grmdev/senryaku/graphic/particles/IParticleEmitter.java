package eu.grmdev.senryaku.graphic.particles;

import java.util.concurrent.ConcurrentHashMap;

import eu.grmdev.senryaku.core.entity.Entity;

public interface IParticleEmitter {
	ConcurrentHashMap<Integer, Entity> getParticles();
	
	Particle getBaseParticle();
	
	boolean isActive();

	void cleanup();
}
