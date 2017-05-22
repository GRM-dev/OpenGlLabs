package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.core.handlers.MouseHandler;

public interface IGame {
	
	void init(Window window) throws Exception;
	
	void input(Window window, MouseHandler mouseInput);
	
	void update(float interval, MouseHandler mouseInput, Window window);
	
	void render(Window window);
	
	void destroy();
}