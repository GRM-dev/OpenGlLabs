package eu.grmdev.senryaku.core.misc;

import java.lang.reflect.Array;
import java.util.*;

import org.joml.GeometryUtils;
import org.joml.Vector3f;

public class VectorUtils {
	
	public static float[] calcNormals(float[] vertices, int[] indices) {
		HashMap<Vector3f, Vector3f> vecs = new HashMap<>();
		for (int i = 0; i <= indices.length - 3; i += 3) {
			int f0 = indices[i + 0];
			int f1 = indices[i + 1];
			int f2 = indices[i + 2];
			
			float v0x = vertices[3 * f0 + 0];
			float v0y = vertices[3 * f0 + 1];
			float v0z = vertices[3 * f0 + 2];
			
			float v1x = vertices[3 * f1 + 0];
			float v1y = vertices[3 * f1 + 1];
			float v1z = vertices[3 * f1 + 2];
			
			float v2x = vertices[3 * f2 + 0];
			float v2y = vertices[3 * f2 + 1];
			float v2z = vertices[3 * f2 + 2];
			
			Vector3f v0 = new Vector3f(v0x, v0y, v0z);
			Vector3f v1 = new Vector3f(v1x, v1y, v1z);
			Vector3f v2 = new Vector3f(v2x, v2y, v2z);
			
			Vector3f dest = new Vector3f();
			GeometryUtils.normal(v0, v1, v2, dest);
			
			if (!vecs.containsKey(v0)) {
				vecs.put(v0, new Vector3f());
			}
			if (!vecs.containsKey(v1)) {
				vecs.put(v1, new Vector3f());
			}
			if (!vecs.containsKey(v2)) {
				vecs.put(v2, new Vector3f());
			}
			
			vecs.get(v0).add(dest).normalize();
			vecs.get(v1).add(dest).normalize();
			vecs.get(v2).add(dest).normalize();
		}
		
		float[] normals = new float[vertices.length];
		for (int i = 0; i <= vertices.length - 3; i += 3) {
			float vx = vertices[i];
			float vy = vertices[i + 1];
			float vz = vertices[i + 2];
			
			Vector3f v = new Vector3f(vx, vy, vz);
			Vector3f n = vecs.get(v);
			
			normals[i] = n.x;
			normals[i + 1] = n.y;
			normals[i + 2] = n.z;
		}
		return normals;
	}
	
	public static <T> T[][] transpose(final T[][] array) {
		Objects.requireNonNull(array);
		final int yCount = Arrays.stream(array).mapToInt(a -> a.length).max().orElse(0);
		final int xCount = array.length;
		final Class<?> componentType = array.getClass().getComponentType().getComponentType();
		@SuppressWarnings("unchecked")
		final T[][] newArray = (T[][]) Array.newInstance(componentType, yCount, xCount);
		for (int x = 0; x < xCount; x++) {
			for (int y = 0; y < yCount; y++) {
				if (array[x] == null || y >= array[x].length) break;
				newArray[y][x] = array[x][y];
			}
		}
		return newArray;
	}
}
