package eu.grmdev.senryaku.core.map;

public enum Tile {
	EMPTY ,
	FLOOR ,
	WALL;
	
	public static Tile value(int i) {
		return values()[i];
	}
}
