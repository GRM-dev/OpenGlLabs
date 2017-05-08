package eu.grmdev.senryaku.timers;

class Sync {
	private static final long NANOS_IN_SECOND = 1000L * 1000L * 1000L;
	private static long nextFrame = 0;
	private static boolean initialized = false;
	private static RunningAvg sleepDurations = new RunningAvg(10);
	private static RunningAvg yieldDurations = new RunningAvg(10);
	
	/**
	 * An accurate sync method that will attempt to run at a constant frame
	 * rate.
	 * It should be called once every frame.
	 * 
	 * @param fps
	 *           - the desired frame rate, in frames per second
	 */
	public static void sync(int fps) {
		if (fps <= 0) return;
		if (!initialized) {
			initialize();
		}
		long currentTime = getTime();
		try {
			for (long t0 = currentTime, t1; (nextFrame - t0) > sleepDurations.avg(); t0 = t1) {
				Thread.sleep(1);
				sleepDurations.add((t1 = currentTime) - t0);
			}
			sleepDurations.dampenForLowResTicker();
			for (long t0 = currentTime, t1; (nextFrame - t0) > yieldDurations.avg(); t0 = t1) {
				Thread.yield();
				yieldDurations.add((t1 = currentTime) - t0); // update average
				// yield time
			}
		}
		catch (InterruptedException e) {
			
		}
		nextFrame = Math.max(nextFrame + NANOS_IN_SECOND / fps, currentTime);
	}
	
	/**
	 * This method will initialize the sync method by setting initial
	 * values for sleepDurations/yieldDurations and nextFrame.
	 * If running on windows it will start the sleep timer fix.
	 */
	private static void initialize() {
		initialized = true;
		sleepDurations.init(1000 * 1000);
		long currentTime = getTime();
		yieldDurations.init((int) (-(currentTime - currentTime) * 1.333));
		nextFrame = currentTime;
		String osName = System.getProperty("os.name");
		
		if (osName.startsWith("Win")) {
			Thread timerAccuracyThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(Long.MAX_VALUE);
					}
					catch (Exception e) {}
				}
			});
			
			timerAccuracyThread.setName("Tick Timer");
			timerAccuracyThread.setDaemon(true);
			timerAccuracyThread.start();
		}
	}
	
	/**
	 * Get the system time in nanoseconds
	 * 
	 * @return will return the current time in nano's
	 */
	private static long getTime() {
		return System.nanoTime();
	}
	
	private static class RunningAvg {
		private final long[] slots;
		private int offset;
		private static final long DAMPEN_THRESHOLD = 10 * 1000L * 1000L;
		private static final float DAMPEN_FACTOR = 0.9f;
		
		public RunningAvg(int slotCount) {
			this.slots = new long[slotCount];
			this.offset = 0;
		}
		
		public void init(long value) {
			while (this.offset < this.slots.length) {
				this.slots[this.offset++] = value;
			}
		}
		
		public void add(long value) {
			this.slots[this.offset++ % this.slots.length] = value;
			this.offset %= this.slots.length;
		}
		
		public long avg() {
			long sum = 0;
			for (int i = 0; i < this.slots.length; i++) {
				sum += this.slots[i];
			}
			return sum / this.slots.length;
		}
		
		public void dampenForLowResTicker() {
			if (this.avg() > DAMPEN_THRESHOLD) {
				for (int i = 0; i < this.slots.length; i++) {
					this.slots[i] *= DAMPEN_FACTOR;
				}
			}
		}
	}
}