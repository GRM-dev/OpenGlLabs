package eu.grmdev.senryaku.core.handlers;

import java.util.HashMap;
import java.util.Map;

import eu.grmdev.senryaku.core.map.GameMap;
import lombok.Getter;

public class LevelManager {
	private @Getter GameMap currentMap;
	private Map<Integer, GameMap> maps;
	
	public LevelManager() {
		maps = new HashMap<>();
	}
	
	public void goTo(int i) throws Exception {
		if (maps.containsKey(i)) {
			currentMap = maps.get(i);
		} else {
			currentMap = new GameMap(i);
			maps.put(i, currentMap);
		}
		
	}
}
