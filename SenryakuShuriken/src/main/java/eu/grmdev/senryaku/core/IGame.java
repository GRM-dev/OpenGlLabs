package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.handlers.MouseHandler;

public interface IGame {
	/**
	 * Called from glfw owned thread. MAkes all initialization for rendering.
	 * 
	 * @param window
	 * @throws Exception
	 */
	void initRender(Window window) throws Exception;
	
	/**
	 * Do not own glfw. Use to initialize logic data. Called after
	 * {@link #initRender(Window)}
	 * 
	 * @param eh
	 * @throws Exception
	 */
	void initLogic(EventHandler eh) throws Exception;
	
	void input(Window window, MouseHandler mouseInput);
	
	void update(float interval, MouseHandler mouseInput, Window window);
	
	void render(Window window);
	
	void destroy();
}