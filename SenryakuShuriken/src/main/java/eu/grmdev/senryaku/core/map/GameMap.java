package eu.grmdev.senryaku.core.map;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joml.Vector2i;

import eu.grmdev.senryaku.core.misc.Utils;
import lombok.Getter;

public class GameMap {
	private @Getter Terrain terrain;
	private final int level;
	private String title;
	private int columns;
	private int rows;
	private @Getter Vector2i startPos;
	private @Getter Vector2i endPos;
	private @Getter boolean initialized;
	private String backgroundTextureFile;
	
	public GameMap(int level) throws Exception {
		this.level = level;
		loadMap();
	}
	
	public void loadMap() throws Exception {
		URL filename = getClass().getResource("/maps/map_" + level + ".smap");
		URI uri = filename.toURI();
		if (filename == null || !(new File(uri)).exists()) { throw new FileNotFoundException("Can't load map for lavel: " + level); }
		
		List<String> fileLines = readFile(uri);
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
	
	private void parseHeader(List<String> fileLines) {
		String[] header = fileLines.get(0).split("\\|");
		int[] positions = new int[]{Integer.parseInt(header[0]), Integer.parseInt(header[1]), Integer.parseInt(header[2]), Integer.parseInt(header[3])};
		startPos = new Vector2i(positions[0], positions[1]);
		endPos = new Vector2i(positions[2], positions[3]);
		title = header[4].trim();
		backgroundTextureFile = "/textures/" + header[5].trim().toLowerCase() + ".png";
		fileLines.remove(0);
	}
	
	private List<String> readFile(URI uri) throws IOException {
		List<String> fileLines;
		try (Stream<String> stream = Files.lines(Paths.get(uri))) {
			fileLines = stream.map(String::toUpperCase).collect(Collectors.toList());
		}
		return fileLines;
	}
	
	public void init() throws Exception {
		terrain.init();
		initialized = true;
	}
}
