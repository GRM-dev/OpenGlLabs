package eu.grmdev.senryaku.core.map;

import java.io.FileNotFoundException;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.material.Material;
import eu.grmdev.senryaku.graphic.material.Texture;
import lombok.Getter;

public enum Tile {
	EMPTY("tile_def") ,
	FLOOR("tile_c") ,
	WALL("tile_a");
	
	private @Getter String textureFile;
	private Mesh mesh;
	
	private Tile(String textureFile) {
		this.textureFile = textureFile;
	}
	
	public static Tile value(int i) {
		return values()[i];
	}
	
	public Mesh getMesh() throws Exception {
		if (mesh == null) {
			try {
				mesh = new Mesh(Terrain.VERTICES, Terrain.TEX_COORDS, Utils.calcNormals(Terrain.VERTICES, Terrain.INDICES), Terrain.INDICES);
				Texture texture = new Texture("/textures/" + textureFile + ".png");
				Material material = new Material(texture);
				mesh.setMaterial(material);
			}
			catch (Exception ex) {
				throw new FileNotFoundException("Cannot create texture " + textureFile + "." + Config.IMAGE_FORMAT + ". Probably file does not exits.");
			}
		}
		return mesh;
	}
}
