package eu.grmdev.senryaku.core.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.grmdev.senryaku.core.misc.Utils;
import lombok.Getter;

public class GameMap {
	private @Getter Terrain terrain;
	private int level;
	private String title;
	private int columns;
	private int rows;
	
	public GameMap(int level) throws Exception {
		this.level = level;
		loadMap();
	}
	
	private void loadMap() throws Exception {
		URL filename = getClass().getResource("/maps/map_" + level + ".smap");
		URI uri = filename.toURI();
		if (filename == null || !(new File(uri)).exists()) { throw new FileNotFoundException("Can't load map for lavel: " + level); }
		
		List<String> fileLine;
		try (Stream<String> stream = Files.lines(Paths.get(uri))) {
			fileLine = stream.map(String::toUpperCase).collect(Collectors.toList());
		}
		
		String[] header = fileLine.get(0).split("\\|");
		int[] positions = new int[]{Integer.parseInt(header[0]), Integer.parseInt(header[1]), Integer.parseInt(header[2]), Integer.parseInt(header[3])};
		title = header[4].trim();
		String bg = "/textures/" + header[5].trim().toLowerCase() + ".png";
		fileLine.remove(0);
		
		HashMap<Integer, HashMap<Integer, Tile>> data = new HashMap<>();
		for (int i = 0; i < fileLine.size(); i++) {
			String[] arg = fileLine.get(i).trim().split(" ");
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
		terrain = new Terrain(positions, tiles, 1, bg);
	}
	
	public void init() throws Exception {
		terrain.init();
	}
}
