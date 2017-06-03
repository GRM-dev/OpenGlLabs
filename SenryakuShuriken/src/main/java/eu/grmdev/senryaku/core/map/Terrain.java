package eu.grmdev.senryaku.core.map;

import java.util.*;

import org.joml.Vector3f;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.material.Material;
import eu.grmdev.senryaku.graphic.material.Texture;
import lombok.Getter;

public class Terrain extends Entity {
	private @Getter final Map<Mesh, List<Entity>> entitiesByMesh;
	private @Getter final Entity[][] entitiesByPos;
	private final int[] terrainSize;
	// @formatter:off
	public static final float[] VERTICES=new float[]{
		// V0
      -0.5f, 0.5f, 0.5f,
      // V1
      -0.5f, -0.5f, 0.5f,
      // V2
      0.5f, -0.5f, 0.5f,
      // V3
      0.5f, 0.5f, 0.5f,
      // V4
      -0.5f, 0.5f, -0.5f,
      // V5
      0.5f, 0.5f, -0.5f,
      // V6
      -0.5f, -0.5f, -0.5f,
      // V7
      0.5f, -0.5f, -0.5f,
      
      //top face
      // V8: V4 repeated
      -0.5f, 0.5f, -0.5f,
      // V9: V5 repeated
      0.5f, 0.5f, -0.5f,
      // V10: V0 repeated
      -0.5f, 0.5f, 0.5f,
      // V11: V3 repeated
      0.5f, 0.5f, 0.5f,

      //right face
      // V12: V3 repeated
      0.5f, 0.5f, 0.5f,
      // V13: V2 repeated
      0.5f, -0.5f, 0.5f,

      //left face
      // V14: V0 repeated
      -0.5f, 0.5f, 0.5f,
      // V15: V1 repeated
      -0.5f, -0.5f, 0.5f,

      //bottom face
      // V16: V6 repeated
      -0.5f, -0.5f, -0.5f,
      // V17: V7 repeated
      0.5f, -0.5f, -0.5f,
      // V18: V1 repeated
      -0.5f, -0.5f, 0.5f,
      // V19: V2 repeated
      0.5f, -0.5f, 0.5f,
	};
	public static final int[] INDICES=new int[]{
		// Front face
      0, 1, 3, 3, 1, 2,
      // Top Face
      8, 10, 11, 9, 8, 11,
      // Right face
      12, 13, 7, 5, 12, 7,
      // Left face
      14, 15, 6, 4, 14, 6,
      // Bottom face
      16, 18, 19, 17, 16, 19,
      // Back face
      4, 6, 7, 5, 4, 7,
	};
	public static final float[] COLORS=new float[]{
		0.5f, 0.0f, 0.0f,
		0.0f, 0.5f, 0.0f,
		0.0f, 0.0f, 0.5f,
		0.0f, 0.5f, 0.5f,
		0.5f, 0.0f, 0.0f,
		0.0f, 0.5f, 0.0f,
		0.0f, 0.0f, 0.5f,
		0.0f, 0.5f, 0.5f,
	};
	public static final float[] TEX_COORDS=new float[]{
		0.0f, 0.0f,
      0.0f, 0.5f,
      0.5f, 0.5f,
      0.5f, 0.0f,
      
      0.0f, 0.0f,
      0.5f, 0.0f,
      0.0f, 0.5f,
      0.5f, 0.5f,
      
      // top face
      0.0f, 0.5f,
      0.5f, 0.5f,
      0.0f, 1.0f,
      0.5f, 1.0f,

      // right face
      0.0f, 0.0f,
      0.0f, 0.5f,

      // left face
      0.5f, 0.0f,
      0.5f, 0.5f,

      // bottom face
      0.5f, 0.0f,
      1.0f, 0.0f,
      0.5f, 0.5f,
      1.0f, 0.5f,
	};
	private String textureFile;
// @formatter:on	
	private @Getter Tile[][] tiles;
	private float tileScale = 1.0f;
	
	public Terrain(Tile[][] tiles, String textureFile, IGame game) throws Exception {
		super(game);
		this.tiles = tiles;
		this.textureFile = textureFile;
		this.terrainSize = new int[]{tiles.length, tiles[0].length};
		entitiesByMesh = new HashMap<>();
		entitiesByPos = new Entity[terrainSize[0]][terrainSize[1]];
	}
	
	public void init() throws Exception {
		createBackgroundMesh();
		
		for (int row = 0; row < terrainSize[0]; row++) {
			for (int col = 0; col < terrainSize[1]; col++) {
				float xDisplacement = row * tileScale;
				float zDisplacement = col * tileScale;
				Mesh mesh = tiles[row][col].getMesh();
				Entity terrainBlock = new Entity(mesh, getGame());
				terrainBlock.setScale(tileScale);
				terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
				if (!entitiesByMesh.containsKey(mesh)) {
					entitiesByMesh.put(mesh, new ArrayList<>());
				}
				entitiesByMesh.get(mesh).add(terrainBlock);
				entitiesByPos[row][col] = terrainBlock;
			}
		}
	}
	
	private void createBackgroundMesh() throws Exception {
		Mesh mesh = new Mesh(VERTICES, TEX_COORDS, Utils.calcNormals(VERTICES, INDICES), INDICES);
		Texture texture = new Texture(textureFile);
		Material material = new Material(texture);
		mesh.setMaterial(material);
		this.meshes = new Mesh[]{mesh};
		setScale(100f);
	}
	
	protected float getDiagonalZCoord(float x1, float z1, float x2, float z2, float x) {
		float z = ((z1 - z2) / (x1 - x2)) * (x - x1) + z1;
		return z;
	}
	
	protected float interpolateHeight(Vector3f pA, Vector3f pB, Vector3f pC, float x, float z) {
		// Plane equation ax+by+cz+d=0
		float a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
		float b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
		float c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
		float d = -(a * pA.x + b * pA.y + c * pA.z);
		// y = (-d -ax -cz) / b
		float y = (-d - a * x - c * z) / b;
		return y;
	}
	
	public Tile getTile(int x, int z) {
		if (x < 0 || z < 0 || x > terrainSize[0] - 1 || z > terrainSize[1] - 1) { return null; }
		return tiles[x][z];
	}
	
	public Tile getTile(float x, float z) {
		int xi = Math.round(x);
		int zi = Math.round(z);
		return getTile(xi, zi);
	}
}
