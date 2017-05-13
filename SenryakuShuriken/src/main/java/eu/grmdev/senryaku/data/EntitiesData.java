package eu.grmdev.senryaku.data;

import java.util.ArrayList;
import java.util.List;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.Player;

public class EntitiesData {
	private static List<Entity> ents = new ArrayList<>();
	
	static {
		ents.add(new Player());
	}
	
	public static void load(Game game) {
		for (Entity entity : ents) {
			game.addEntity(entity);
		}
	}
}
