package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.handlers.MouseHandler;
import eu.grmdev.senryaku.game.Game;
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
	
	/**
	 * When {@link Game} is paused return true.
	 * Pause is when menu or other screen is active.
	 * 
	 * @return true wheng ame is paused
	 */
	boolean isPaused();
	
	Camera getCamera();
	
	/**
	 * Adds entity to scene
	 * 
	 * @param entity
	 */
	void addEntity(Entity entity);
	
	/**
	 * Remove entity from scene
	 * 
	 * @param entity
	 */
	void removeEntity(Entity entity);
}