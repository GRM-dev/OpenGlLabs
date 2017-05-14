package eu.grmdev.senryaku.core.tile;

import eu.grmdev.senryaku.graphic.Assets;
import eu.grmdev.senryaku.graphic.GameWindow;

public class World {
	public static final int[] SIZE = new int[]{30, 40};
	public static final float[] VERT = new float[]{-0.5f, 0.5f, 0, 0.5f, 0.5f, 0, 0.5f, -0.5f, 0, -0.5f, -0.5f, 1};
	public static final int[] IND = new int[]{0, 1, 2, 2, 3, 0};
	public static final float[] TEX = new float[]{0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0};
	private Tile[][] tiles;
	
	public World() {
		tiles = new Tile[SIZE[0]][];
		for (int i = 0; i < SIZE[0]; i++) {
			tiles[i] = new Tile[SIZE[1]];
			for (int j = 0; j < SIZE[1]; j++) {
				tiles[i][j] = new Tile(i, j, 0, VERT, IND, TEX, Assets.Asset.WOOD_1);
			}
		}
	}
	
	public void init() {
		for (int i = 0; i < SIZE[0]; i++) {
			for (int j = 0; j < SIZE[1]; j++) {
				tiles[i][j].init();
			}
		}
	}
	
	public void render(GameWindow window) {
		for (int i = 0; i < SIZE[0]; i++) {
			for (int j = 0; j < SIZE[1]; j++) {
				tiles[i][j].render(window);
			}
		}
	}
}
