package eu.grmdev.senryaku.core.map;

import java.io.FileNotFoundException;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.misc.VectorUtils;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.material.Material;
import eu.grmdev.senryaku.graphic.material.Texture;
import lombok.Getter;

public enum Tile {
	EMPTY(0,"def",true,0f) ,
	FLOOR(1,"floor",true,0f) ,
	GRASS(3,"grass",true,0f) ,
	WALL(2,"wall",false,1f);
	
	private @Getter int id;
	private @Getter String textureFile;
	private @Getter boolean passable;
	private @Getter float height;
	private Mesh mesh;
	
	private Tile(int id, String textureFile, boolean passable, float height) {
		this.id = id;
		this.textureFile = textureFile;
		this.passable = passable;
		this.height = height;
	}
	
	public static Tile value(int i) {
		return values()[i];
	}
	
	public Mesh getMesh() throws Exception {
		if (mesh == null) {
			try {
				mesh = new Mesh(Terrain.VERTICES, Terrain.TEX_COORDS, VectorUtils.calcNormals(Terrain.VERTICES, Terrain.INDICES), Terrain.INDICES);
				Texture texture = new Texture("/textures/tile_" + textureFile + ".png");
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
