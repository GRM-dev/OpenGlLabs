package eu.grmdev.senryaku.core.handlers;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2i;

import eu.grmdev.senryaku.core.map.GameMap;
import eu.grmdev.senryaku.game.Player;
import lombok.Getter;
import lombok.Setter;

public class LevelManager {
	private @Getter GameMap currentMap;
	private Map<Integer, GameMap> maps;
	private @Setter Player player;
	
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
		if (player != null) {
			Vector2i startPos = currentMap.getStartPos();
			player.setPosition(startPos.x, player.getPosition().y, startPos.y);
		}
	}
}
