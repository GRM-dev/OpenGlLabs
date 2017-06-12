package eu.grmdev.senryaku.core.map;

import java.io.FileNotFoundException;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.misc.VectorUtils;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.material.Material;
import eu.grmdev.senryaku.graphic.material.Texture;
import eu.grmdev.senryaku.graphic.particles.ParticleType;
import lombok.Getter;

public enum Tile {
	EMPTY(0,"def",true,0f,false) ,
	FLOOR(1,"floor2",true,0f,true) ,
	GRASS(3,"grass",true,0f,true) ,
	WALL(2,"wall",false,1f,false) ,
	CONE(4,"cone",false,0f,false,ParticleType.FIRE);
	
	private @Getter int id;
	private @Getter String textureFile;
	private @Getter boolean passable;
	private @Getter boolean throwable;
	private @Getter float height;
	private @Getter ParticleType emitter;
	private Mesh mesh;
	
	private Tile(int id, String textureFile, boolean throwable, float height, boolean passable, ParticleType emitter) {
		this(id, textureFile, throwable, height, passable);
		this.emitter = emitter;
	}
	
	private Tile(int id, String textureFile, boolean throwable, float height, boolean passable) {
		this.id = id;
		this.textureFile = textureFile;
		this.throwable = throwable;
		this.passable = passable;
		this.height = height;
	}
	
	public static Tile value(int id) {
		for (Tile t : values()) {
			if (t.id == id) { return t; }
		}
		return null;
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
	
	public static void destroy() {
		for (Tile t : values()) {
			t.mesh = null;
		}
	}
}
