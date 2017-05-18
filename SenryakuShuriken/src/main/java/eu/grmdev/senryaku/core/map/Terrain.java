package eu.grmdev.senryaku.core.map;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.Mesh;
import eu.grmdev.senryaku.graphic.material.Material;
import eu.grmdev.senryaku.graphic.material.Texture;
import lombok.Getter;

public class Terrain {
	private @Getter final Entity[] entity;
	private final int[] terrainSize;
	private @Getter Mesh mesh;
	private final Box2D[][] boundingBoxes;
	// @formatter:off
	private static final float[] VERTICES=new float[]{
		// V0
      -0.5f, -0.5f, 0.5f,
      // V1
      0.5f, -0.5f, 0.5f,
      // V2
      0.5f, 0.5f, 0.5f,
      // V3
      -0.5f, 0.5f, 0.5f,
      // V4
      -0.5f, -0.5f, -0.5f,
      // V5
      0.5f, -0.5f, -0.5f,
      // V6
      0.5f, 0.5f, -0.5f,
      // V7
      -0.5f, 0.5f, -0.5f,
	};
	private static final int[] INDICES=new int[]{
		0, 1, 2,
		2, 3, 0,
		// top
		1, 5, 6,
		6, 2, 1,
		// back
		7, 6, 5,
		5, 4, 7,
		// bottom
		4, 0, 3,
		3, 7, 4,
		// left
		4, 5, 1,
		1, 0, 4,
		// right
		3, 2, 6,
		6, 7, 3,
	};
	private static final float[] COLORS=new float[]{
		0.5f, 0.0f, 0.0f,
		0.0f, 0.5f, 0.0f,
		0.0f, 0.0f, 0.5f,
		0.0f, 0.5f, 0.5f,
		0.5f, 0.0f, 0.0f,
		0.0f, 0.5f, 0.0f,
		0.0f, 0.0f, 0.5f,
		0.0f, 0.5f, 0.5f,
	};
	private static final float[] TEX_COORDS=new float[]{
		0.0f, 0.0f,
      0.0f, 0.5f,
      0.5f, 0.5f,
      0.5f, 0.0f,
      
      0.0f, 0.0f,
      0.5f, 0.0f,
      0.0f, 0.5f,
      0.5f, 0.5f,
      
      0.0f, 0.5f,
      0.5f, 0.5f,
      0.0f, 1.0f,
      0.5f, 1.0f,

      0.0f, 0.0f,
      0.0f, 0.5f,

      0.5f, 0.0f,
      0.5f, 0.5f,
      
      0.5f, 0.0f,
      1.0f, 0.0f,
      0.5f, 0.5f,
      1.0f, 0.5f,	    
	};
	private String textureFile;
// @formatter:on	
	private float scale;
	private Tile[][] tiles;
	private int[] poss;
	
	public Terrain(int[] poss, Tile[][] tiles, float scale, String textureFile) throws Exception {
		this.poss = poss;
		this.tiles = tiles;
		this.scale = scale;
		this.textureFile = textureFile;
		this.terrainSize = new int[]{tiles.length, tiles[0].length};
		entity = new Entity[terrainSize[0] * terrainSize[1]];
		boundingBoxes = new Box2D[terrainSize[0]][terrainSize[1]];
	}
	
	public void init() throws Exception {
		mesh = new Mesh(VERTICES, TEX_COORDS, Utils.calcNormals(VERTICES, INDICES), INDICES);
		Texture texture = new Texture(textureFile);
		Material material = new Material(texture);
		mesh.setMaterial(material);
		
		for (int row = 0; row < terrainSize[0]; row++) {
			for (int col = 0; col < terrainSize[1]; col++) {
				float xDisplacement = row * scale;
				float zDisplacement = col * scale;
				// float xDisplacement = (row - ((float) terrainSize[0] - 1) / 2) *
				// scale;
				// float zDisplacement = (col - ((float) terrainSize[1] - 1) / 2) *
				// scale;
				
				Entity terrainBlock = new Entity(mesh);
				terrainBlock.setScale(scale);
				terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
				entity[row * terrainSize[1] + col] = terrainBlock;
				
				// boundingBoxes[row][col] = getBoundingBox(terrainBlock);
			}
		}
	}
	
	// public float getHeight(Vector3f position) {
	// float result = Float.MIN_VALUE;
	// Box2D boundingBox = null;
	// boolean found = false;
	// Entity terrainBlock = null;
	// for (int row = 0; row < terrainSize[0] && !found; row++) {
	// for (int col = 0; col < terrainSize[1] && !found; col++) {
	// terrainBlock = entity[row * terrainSize[0] + col];
	// boundingBox = boundingBoxes[row][col];
	// found = boundingBox.contains(position.x, position.z);
	// }
	// }
	//
	// if (found) {
	// Vector3f[] triangle = getTriangle(position, boundingBox, terrainBlock);
	// result = interpolateHeight(triangle[0], triangle[1], triangle[2],
	// position.x, position.z);
	// }
	//
	// return result;
	// }
	//
	// protected Vector3f[] getTriangle(Vector3f position, Box2D boundingBox,
	// Entity terrainBlock) {
	// float cellWidth = boundingBox.width / verticesPerCol;
	// float cellHeight = boundingBox.height / verticesPerRow;
	// int col = (int) ((position.x - boundingBox.x) / cellWidth);
	// int row = (int) ((position.z - boundingBox.y) / cellHeight);
	//
	// Vector3f[] triangle = new Vector3f[3];
	// triangle[1] = new Vector3f(boundingBox.x + col * cellWidth,
	// getWorldHeight(row + 1, col, terrainBlock), boundingBox.y + (row + 1) *
	// cellHeight);
	// triangle[2] = new Vector3f(boundingBox.x + (col + 1) * cellWidth,
	// getWorldHeight(row, col + 1, terrainBlock), boundingBox.y + row *
	// cellHeight);
	// if (position.z < getDiagonalZCoord(triangle[1].x, triangle[1].z,
	// triangle[2].x, triangle[2].z, position.x)) {
	// triangle[0] = new Vector3f(boundingBox.x + col * cellWidth,
	// getWorldHeight(row, col, terrainBlock), boundingBox.y + row * cellHeight);
	// } else {
	// triangle[0] = new Vector3f(boundingBox.x + (col + 1) * cellWidth,
	// getWorldHeight(row + 2, col + 1, terrainBlock), boundingBox.y + (row + 1)
	// * cellHeight);
	// }
	//
	// return triangle;
	// }
	//
	// protected float getDiagonalZCoord(float x1, float z1, float x2, float z2,
	// float x) {
	// float z = ((z1 - z2) / (x1 - x2)) * (x - x1) + z1;
	// return z;
	// }
	//
	// protected float getWorldHeight(int row, int col, Entity gameItem) {
	// float y = mesh.getHeight(row, col);
	// return y * gameItem.getScale() + gameItem.getPosition().y;
	// }
	//
	// protected float interpolateHeight(Vector3f pA, Vector3f pB, Vector3f pC,
	// float x, float z) {
	// // Plane equation ax+by+cz+d=0
	// float a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
	// float b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
	// float c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
	// float d = -(a * pA.x + b * pA.y + c * pA.z);
	// // y = (-d -ax -cz) / b
	// float y = (-d - a * x - c * z) / b;
	// return y;
	// }
	//
	// /**
	// * Gets the bounding box of a terrain block
	// *
	// * @param terrainBlock
	// * A GameItem instance that defines the terrain block
	// * @return The boundingg box of the terrain block
	// */
	// private Box2D getBoundingBox(Entity terrainBlock) {
	// float scale = terrainBlock.getScale();
	// Vector3f position = terrainBlock.getPosition();
	//
	// float topLeftX = Mesh.STARTX * scale + position.x;
	// float topLeftZ = Mesh.STARTZ * scale + position.z;
	// float width = Math.abs(Mesh.STARTX * 2) * scale;
	// float height = Math.abs(Mesh.STARTZ * 2) * scale;
	// Box2D boundingBox = new Box2D(topLeftX, topLeftZ, width, height);
	// return boundingBox;
	// }
	
	static class Box2D {
		public float x;
		public float y;
		public float width;
		public float height;
		
		public Box2D(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public boolean contains(float x2, float y2) {
			return x2 >= x && y2 >= y && x2 < x + width && y2 < y + height;
		}
	}
}
