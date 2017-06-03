package eu.grmdev.senryaku.core.entity;

import org.joml.Vector3f;

import eu.grmdev.senryaku.core.LogicThread;
import eu.grmdev.senryaku.core.handlers.EventHandler;

public interface Movable {
	/**
	 * Initializes Movable. Can add event l;isteners
	 * 
	 * @param eh
	 *           {@link EventHandler}
	 */
	void init(EventHandler eh);
	
	/**
	 * When there is no obstacle to move object then it returns true.
	 * 
	 * @param rx
	 *           vector x param to which point we want object to move.
	 * @param rzvector
	 *           z param to which point we want object to move.
	 * @return
	 */
	boolean canMove(float rx, float rz);
	
	/**
	 * Moves our object to positions x+rx, y, z+rz
	 * 
	 * @param rx
	 * @param rz
	 */
	void move(float rx, float rz);
	
	/**
	 * Method called each tick in game by {@link LogicThread}
	 * 
	 * @param interval
	 *           miliseconds passed from last tick
	 */
	void animate(float interval);
	
	void checkCollisions(Vector3f pos);
	
	/**
	 * If movable is declared to be executed or its lifetime has beed used then
	 * return true
	 * 
	 * @return true if movable has to die
	 */
	boolean canDie();
	
	void die();
}