package eu.grmdev.senryaku.core;

import java.util.List;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.handlers.MouseHandler;
import eu.grmdev.senryaku.graphic.Camera;
import eu.grmdev.senryaku.graphic.Window;

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
	 * @param mouseHandler
	 * @throws Exception
	 */
	void initLogic(EventHandler eh, MouseHandler mHandler) throws Exception;
	
	void input(Window window, MouseHandler mouseInput);
	
	void update(float interval, MouseHandler mouseInput, Window window);
	
	void render(Window window);
	
	Entity getPlayer();
	
	void destroy();
	
	boolean isPaused();
	
	Camera getCamera();
	
	List<Entity> getEntities();
}