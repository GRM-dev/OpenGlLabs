package eu.grmdev.senryaku.core.map;

import org.joml.Vector2i;
import org.joml.Vector3f;

import eu.grmdev.senryaku.game.GameSave;
import lombok.Getter;
import lombok.Setter;

public class GameMap {
	private @Getter Terrain terrain;
	private final @Getter int level;
	private @Getter String title;
	private int columns;
	private int rows;
	private @Getter Vector2i startPos;
	private @Getter Vector2i endPos;
	private @Getter boolean initialized;
	private boolean finished;
	private @Getter @Setter int stepCounter = 0;
	private @Getter int score = 0;
	
	public GameMap(int level, String title, int rows, int columns, Terrain terrain, Vector2i startPos, Vector2i endPos) throws Exception {
		this.level = level;
		this.title = title;
		this.rows = rows;
		this.columns = columns;
		this.terrain = terrain;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	public void init() throws Exception {
		terrain.init();
		initialized = true;
	}
	
	public boolean canMoveTo(float x, float playerDefYPos, float z) {
		if (finished) { return false; }
		Tile tile = getTerrain().getTile(x, z);
		if (tile == null) { return false; }
		return tile.isPassable();
	}
	
	public synchronized boolean checkEnd(Vector3f pos) {
		if (endPos.x == pos.x && endPos.y == pos.z && !finished) {
			score = stepCounter;
			finished = true;
			GameSave.save(level, this);
		}
		return finished;
	}
	
	public void incCounter() {
		stepCounter++;
	}
	
	public void reset() {
		finished = false;
	}
	
	public int getBestScore() {
		int bs = GameSave.getScore(level);
		if (bs == 0 || score < bs) { return score; }
		return bs;
	}
}
