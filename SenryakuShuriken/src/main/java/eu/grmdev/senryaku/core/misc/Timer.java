package eu.grmdev.senryaku.core.misc;

public class Timer {
	private double lastLoopTime;
	
	public void init() {
		lastLoopTime = getTime();
	}
	
	/**
	 * @return Current time in seconds
	 */
	public double getTime() {
		return System.nanoTime() / 1E9;
	}
	
	public float getElapsedTime() {
		double time = getTime();
		float elapsedTime = (float) (time - lastLoopTime);
		if (elapsedTime > 1f) {
			lastLoopTime = time;
		}
		return elapsedTime;
	}
	
	public double getLastLoopTime() {
		return lastLoopTime;
	}
}