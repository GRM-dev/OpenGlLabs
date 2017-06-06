package eu.grmdev.senryaku.core.misc;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

import org.joml.GeometryUtils;
import org.joml.Vector3f;

public class VectorUtils {
	
	public static float[] calcNormals(float[] vertices, int[] indices) {
		float[] normals = new float[vertices.length];
		for (int i = 0; i < indices.length - 9; i += 3 * 3) {
			Vector3f v0 = new Vector3f(vertices[indices[i + 0]], vertices[indices[i + 1]], vertices[indices[i + 2]]);
			Vector3f v1 = new Vector3f(vertices[indices[i + 3]], vertices[indices[i + 4]], vertices[indices[i + 5]]);
			Vector3f v2 = new Vector3f(vertices[indices[i + 6]], vertices[indices[i + 7]], vertices[indices[i + 8]]);
			Vector3f dest = new Vector3f();
			GeometryUtils.normal(v0, v1, v2, dest);
			dest.normalize();
			normals[i + 0] += dest.x;
			normals[i + 1] += dest.y;
			normals[i + 2] += dest.z;
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
