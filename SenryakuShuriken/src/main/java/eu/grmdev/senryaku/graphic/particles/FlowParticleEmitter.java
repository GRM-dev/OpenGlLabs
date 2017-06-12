package eu.grmdev.senryaku.graphic.particles;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector3f;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.entity.Entity;
import lombok.Getter;
import lombok.Setter;

public class FlowParticleEmitter implements IParticleEmitter {
	private @Getter @Setter int maxParticles;
	private @Getter @Setter boolean active;
	private @Getter final ConcurrentHashMap<Integer, Entity> particles;
	private static int nextId = 0;
	private @Getter final Particle baseParticle;
	private @Getter @Setter long creationPeriodMillis;
	private long lastCreationTime;
	private @Getter @Setter float speedRndRange;
	private @Getter @Setter float positionRndRange;
	private @Getter @Setter float scaleRndRange;
	private @Setter long animRange;
	private IGame game;
	
	public FlowParticleEmitter(Particle baseParticle, int maxParticles, long creationPeriodMillis) {
		this.particles = new ConcurrentHashMap<>();
		this.baseParticle = baseParticle;
		this.maxParticles = maxParticles;
		this.active = false;
		this.lastCreationTime = 0;
		this.creationPeriodMillis = creationPeriodMillis;
	}
	
	/**
	 * Particle life update
	 * 
	 * @param elapsedTime
	 */
	public void update(long elapsedTime) {
		long now = System.currentTimeMillis();
		if (lastCreationTime == 0) {
			lastCreationTime = now;
		}
		Iterator<Integer> it = particles.keySet().iterator();
		while (it.hasNext()) {
			Particle particle = (Particle) particles.get(it.next());
			if (particle.updateTtl(elapsedTime) < 0) {
				it.remove();
			} else {
				updatePosition(particle, elapsedTime);
			}
		}
		
		int length = this.getParticles().size();
		if (now - lastCreationTime >= this.creationPeriodMillis && length < maxParticles) {
			createParticle();
			this.lastCreationTime = now;
		}
	}
	
	private void createParticle() {
		Particle particle = new Particle(this.getBaseParticle(), game);
		float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
		float speedInc = sign * (float) Math.random() * this.speedRndRange;
		float posInc = sign * (float) Math.random() * this.positionRndRange;
		float scaleInc = sign * (float) Math.random() * this.scaleRndRange;
		long updateAnimInc = (long) sign * (long) (Math.random() * this.animRange);
		particle.getPosition().add(posInc, posInc, posInc);
		particle.getSpeed().add(speedInc, speedInc, speedInc);
		particle.setScale(particle.getScale() + scaleInc);
		particle.setUpdateTextureMillis(particle.getUpdateTextureMillis() + updateAnimInc);
		particles.put(nextId++, particle);
	}
	
	/**
	 * Updates a particle position
	 * 
	 * @param particle
	 *           The particle to update
	 * @param elapsedTime
	 *           Elapsed time in milliseconds
	 */
	public void updatePosition(Particle particle, long elapsedTime) {
		Vector3f speed = particle.getSpeed();
		float delta = elapsedTime / 1000.0f;
		float dx = speed.x * delta;
		float dy = speed.y * delta;
		float dz = speed.z * delta;
		Vector3f pos = particle.getPosition();
		particle.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
	}
	
	/**
	 * Correctly clean after particle dispose
	 */
	@Override
	public void cleanup() {
		for (Integer id : getParticles().keySet()) {
			getParticles().get(id).cleanup();
		}
	}
}
