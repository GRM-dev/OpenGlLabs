package eu.grmdev.senryaku.graphic.particles;

import org.joml.Vector3f;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.material.Texture;
import lombok.Getter;
import lombok.Setter;

public class Particle extends Entity {
	private @Getter @Setter long updateTextureMillis;
	private @Getter long currentAnimTimeMillis;
	private @Getter @Setter Vector3f speed;
	/**
	 * Time to live for particle in milliseconds.
	 */
	private @Getter @Setter long ttl;
	private @Getter int animFrames;
	
	public Particle(Mesh mesh, Vector3f speed, long ttl, long updateTextureMillis, IGame game) {
		super(mesh, game);
		this.speed = new Vector3f(speed);
		this.ttl = ttl;
		this.updateTextureMillis = updateTextureMillis;
		this.currentAnimTimeMillis = 0;
		Texture texture = this.getMesh().getMaterial().getTexture();
		this.animFrames = texture.getNumCols() * texture.getNumRows();
	}
	
	public Particle(Particle baseParticle, IGame game) {
		super(baseParticle.getMesh(), game);
		Vector3f aux = baseParticle.getPosition();
		setPosition(aux.x, aux.y, aux.z);
		setRotation(baseParticle.getRotation());
		setScale(baseParticle.getScale());
		this.speed = new Vector3f(baseParticle.speed);
		this.ttl = baseParticle.getTtl();
		this.updateTextureMillis = baseParticle.getUpdateTextureMillis();
		this.currentAnimTimeMillis = 0;
		this.animFrames = baseParticle.getAnimFrames();
	}
	
	/**
	 * Updates the Particle's TTL
	 * 
	 * @param elapsedTime
	 *           Elapsed Time in milliseconds
	 * @return The Particle's TTL
	 */
	public long updateTtl(long elapsedTime) {
		this.ttl -= elapsedTime;
		this.currentAnimTimeMillis += elapsedTime;
		if (this.currentAnimTimeMillis >= this.getUpdateTextureMillis() && this.animFrames > 0) {
			this.currentAnimTimeMillis = 0;
			int pos = this.getTextPos();
			pos++;
			if (pos < this.animFrames) {
				this.setTextPos(pos);
			} else {
				this.setTextPos(0);
			}
		}
		return this.ttl;
	}
	
}