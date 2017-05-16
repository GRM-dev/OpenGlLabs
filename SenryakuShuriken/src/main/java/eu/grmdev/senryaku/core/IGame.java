package eu.grmdev.senryaku.core;

import eu.grmdev.senryaku.core.handlers.MouseInput;

public interface IGame {

    void init(Window window) throws Exception;
    
    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput, Window window);
    
    void render(Window window);
    
    void destroy();
}