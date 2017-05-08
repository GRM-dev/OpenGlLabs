package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.graphic.Window;

public class LogicThread extends Thread {
	private Window graphic;
	
	public LogicThread(Window graphic) {
		this.graphic = graphic;
		setName("Logic Thread");
	}
	
	@Override
	public void run() {
		init();
		System.out.println("Start Logic Thread");
		loop();
		System.out.println("Stop Logic Thread");
	}
	
	private void init() {
		while (!graphic.isReady()) {
			try {
				Thread.sleep(50);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loop() {
		while (!graphic.shouldClose()) {
			try {
				Thread.sleep(50);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
