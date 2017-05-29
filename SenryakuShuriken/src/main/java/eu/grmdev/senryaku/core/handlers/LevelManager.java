package eu.grmdev.senryaku.core.handlers;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2i;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.map.GameMap;
import eu.grmdev.senryaku.game.Player;
import lombok.Getter;
import lombok.Setter;

public class LevelManager {
	private @Getter GameMap currentMap;
	private Map<Integer, GameMap> maps;
	private @Setter Player player;
	private @Setter Entity portal;
	
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
		setStartEndObjectsPositions(currentMap.getStartPos(), currentMap.getEndPos());
	}
	
	private void setStartEndObjectsPositions(Vector2i start, Vector2i end) throws Exception {
		player.setPosition(start.x, Player.PLAYER_DEF_Y_POS, start.y);
		portal.setPosition(end.x, Player.PLAYER_DEF_Y_POS, end.y);
	}
}
