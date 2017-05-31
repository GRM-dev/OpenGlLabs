package eu.grmdev.senryaku.core.map;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.joml.Vector2i;
import org.joml.Vector3f;

import eu.grmdev.senryaku.core.misc.Utils;
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
	private String backgroundTextureFile;
	private boolean finished;
	private @Getter @Setter int stepCounter = 0;
	private @Getter int score = 0;
	
	public GameMap(int level) throws Exception {
		this.level = level;
		loadMap();
	}
	
	public void loadMap() throws Exception {
		System.out.println("Loading map level " + level);
		List<String> fileLines = readFile();
		parseHeader(fileLines);
		
		HashMap<Integer, HashMap<Integer, Tile>> data = new HashMap<>();
		for (int i = 0; i < fileLines.size(); i++) {
			String[] arg = fileLines.get(i).trim().split(" ");
			HashMap<Integer, Tile> row = new HashMap<>();
			for (int j = 0; j < arg.length; j++) {
				char c = arg[j].trim().charAt(0);
				int ci = Integer.parseInt(c + "");
				Tile t = Tile.value(ci);
				row.put(j, t);
			}
			data.put(i, row);
		}
		rows = data.size();
		columns = data.get(0).size();
		data.forEach((k, v) -> {
			columns = columns > v.size() ? columns : v.size();
		});
		Tile[][] tiles = new Tile[rows][columns];
		for (int i = 0; i < rows; i++) {
			HashMap<Integer, Tile> row = data.get(i);
			for (int j = 0; j < columns; j++) {
				tiles[i][j] = row.containsKey(j) ? row.get(j) : Tile.EMPTY;
			}
		}
		tiles = Utils.transpose(tiles);
		terrain = new Terrain(tiles, backgroundTextureFile);
	}
	
	private List<String> readFile() throws IOException {
		List<String> fileLines = Utils.readAllLines("/maps/map_" + level + ".smap");
		return fileLines;
	}
	
	private void parseHeader(List<String> fileLines) {
		String[] header = fileLines.get(0).split("\\|");
		int[] positions = new int[]{Integer.parseInt(header[0]), Integer.parseInt(header[1]), Integer.parseInt(header[2]), Integer.parseInt(header[3])};
		startPos = new Vector2i(positions[0], positions[1]);
		endPos = new Vector2i(positions[2], positions[3]);
		title = header[4].trim();
		backgroundTextureFile = "/textures/" + header[5].trim().toLowerCase() + ".png";
		fileLines.remove(0);
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
