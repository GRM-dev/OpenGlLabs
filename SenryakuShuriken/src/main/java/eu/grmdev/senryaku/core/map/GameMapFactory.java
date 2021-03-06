package eu.grmdev.senryaku.core.map;

import java.io.IOException;
import java.util.*;

import org.joml.Vector2i;
import org.joml.Vector3f;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.core.misc.VectorUtils;
import eu.grmdev.senryaku.graphic.mesh.*;
import eu.grmdev.senryaku.graphic.particles.*;

public class GameMapFactory {
	private static int rows;
	private static int columns;
	private static String backgroundTextureFile;
	private static String title;
	private static Vector2i startPos;
	private static Vector2i endPos;
	
	public static GameMap create(int level, IGame game) throws Exception {
		System.out.println("Loading map level " + level);
		List<String> fileLines = readFile(level);
		parseHeader(fileLines);
		
		Tile[][] tiles = getTiles(fileLines);
		Terrain terrain = new Terrain(tiles, backgroundTextureFile, game);
		GameMap gm = new GameMap(level, title, rows, columns, terrain, startPos, endPos, game);
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
				if (t == null) {
					System.err.println("ERROR!: Wrong Tile at (" + i + ", " + j + ") with value [" + c + "]");
					row.put(j, Tile.EMPTY);
				} else {
					row.put(j, t);
				}
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
	
	public static List<IParticleEmitter> prepareEmitters(Tile[][] tiles, IGame game) throws Exception {
		List<IParticleEmitter> list = new ArrayList<>();
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (tiles[i][j] == Tile.CONE) {
					ParticleType emitter = Tile.CONE.getEmitter();
					list.add(setupParticlesEmitter(emitter, game, i, j));
				}
			}
		}
		return list;
	}
	
	private static IParticleEmitter setupParticlesEmitter(ParticleType emitter, IGame game, int x, int z) throws Exception {
		Mesh partMesh = StaticMeshesLoader.loadMesh(emitter.getRes(), Config.MAX_PARICLES.<Integer> get());
		Texture particleTexture = new Texture(emitter.getTex(), 4, 4);
		Material partMaterial = new Material(particleTexture, 1f);
		partMesh.setMaterial(partMaterial);
		Vector3f particleSpeed = new Vector3f(0, 0.6f, 0);
		particleSpeed.mul(2.5f);
		Particle particle = new Particle(partMesh, particleSpeed, Config.PARTICLE_LIFE_TIME.<Integer> get(), 100, game);
		particle.setPosition(x, 0.5f, z);
		particle.setScale(Config.PARTICLE_SCALE.get());
		FlowParticleEmitter particleEmitter = new FlowParticleEmitter(particle, Config.MAX_PARICLES.get(), 300);
		particleEmitter.setActive(true);
		particleEmitter.setPositionRndRange(Config.PARTICLE_RANGE.get());
		particleEmitter.setSpeedRndRange(Config.PARTICLE_RANGE.get());
		particleEmitter.setAnimRange(10);
		return particleEmitter;
	}
}
