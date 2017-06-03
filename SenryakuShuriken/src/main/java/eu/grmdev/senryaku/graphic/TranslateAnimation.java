package eu.grmdev.senryaku.graphic;

import org.joml.Vector3f;

import eu.grmdev.senryaku.Config;
import lombok.Getter;
import lombok.Setter;

public class TranslateAnimation {
	private @Getter Vector3f position;
	private @Getter Vector3f destPosition;
	private Vector3f v;
	private int moveCounter = 0;
	private @Getter @Setter float speed = 0.5f;
	private int tps = Config.TARGET_UPS / 2;
	
	public TranslateAnimation(Vector3f position) {
		this.position = position;
		this.destPosition = new Vector3f(position);
		this.v = new Vector3f();
	}
	
	public void move(float rx, float rz) {
		destPosition.x += rx;
		destPosition.z += rz;
		recalc();
	}
	
	public void reset() {
		destPosition.set(position);
	}
	
	private void recalc() {
		moveCounter = 0;
		v.x = (destPosition.x - position.x) / tps;
		v.z = (destPosition.z - position.z) / tps;
	}
	
	public void animate(float interval) {
		synchronized (position) {
			if (moveCounter < tps) {
				position.x += v.x * interval * 0.02f;
				position.z += v.z * interval * 0.02f;
				moveCounter++;
			} else {
				position.set(destPosition);
			}
		}
	}
}
