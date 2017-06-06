package eu.grmdev.senryaku.core.map;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.joml.Vector2i;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.core.misc.VectorUtils;

public class GameMapFactory {
	private static int rows;
	private static int columns;
	private static Terrain terrain;
	private static String backgroundTextureFile;
	private static String title;
	private static Vector2i startPos;
	private static Vector2i endPos;
	
	public static GameMap create(int level, IGame game) throws Exception {
		System.out.println("Loading map level " + level);
		List<String> fileLines = readFile(level);
		parseHeader(fileLines);
		
		Tile[][] tiles = getTiles(fileLines);
		terrain = new Terrain(tiles, backgroundTextureFile, game);
		GameMap gm = new GameMap(level, title, rows, columns, terrain, startPos, endPos);
		return gm;
	}
	
	private static List<String> readFile(int level) throws IOException {
		List<String> fileLines = Utils.readAllLines(getFileName(level));
		return fileLines;
	}
	
	private static void parseHeader(List<String> fileLines) {
		String[] header = fileLines.get(0).split("\\|");
		int[] positions = new int[]{Integer.parseInt(header[0]), Integer.parseInt(header[1]), Integer.parseInt(header[2]), Integer.parseInt(header[3])};
		startPos = new Vector2i(positions[0], positions[1]);
		endPos = new Vector2i(positions[2], positions[3]);
		title = header[4].trim();
		backgroundTextureFile = "/textures/" + header[5].trim().toLowerCase() + ".png";
		fileLines.remove(0);
	}
	
	private static Tile[][] getTiles(List<String> fileLines) {
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
		tiles = VectorUtils.transpose(tiles);
		return tiles;
	}
	
	public static boolean exist(int level) {
		return Utils.existsResourceFile(getFileName(level));
	}
	
	private static String getFileName(int level) {
		return "/maps/map_" + level + ".smap";
	}
}
