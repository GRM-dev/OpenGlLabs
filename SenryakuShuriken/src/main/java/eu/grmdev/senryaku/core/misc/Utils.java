package eu.grmdev.senryaku.core.misc;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.assimp.Assimp.aiImportFile;

import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

import org.joml.GeometryUtils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIScene;

public class Utils {
	
	public static String loadResourceContent(String fileName) throws Exception {
		String result;
		try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName); Scanner scanner = new Scanner(in, "UTF-8")) {
			result = scanner.useDelimiter("\\A").next();
		}
		return result;
	}
	
	public static File getFile(String fileName) throws MalformedURLException {
		// return
		// Thread.currentThread().getContextClassLoader().getResource(fileName);
		String path = fileName.startsWith("./resources") ? fileName : loadResourcePath(fileName);
		return new File(path);
	}
	
	public static String loadResourcePath(String fileName) {
		return "./resources" + (fileName.startsWith("/") ? "" : "/") + fileName;
	}
	
	public static String loadFullResourcePath(String filename) {
		File f = new File(loadResourcePath(filename));
		if (!f.exists()) {
			System.out.println("");
		}
		return f.getPath();
	}
	
	public static InputStream loadResourceAsStream(String fileName) {
		return Utils.class.getResourceAsStream(fileName);
	}
	
	public static AIScene loadAssimpObject(String filename, int flags) throws Exception {
		File f = getFile(filename);
		if (f == null || !f.exists()) { throw new IOException("Model file not exists: " + filename); }
		AIScene aiScene = aiImportFile(f.getAbsolutePath(), flags);
		if (aiScene == null) {
			System.out.println("null " + filename);
			throw new Exception("Error loading model: " + f.getAbsolutePath());
		}
		return aiScene;
	}
	
	public static URI loadResourceForTemp(String fileName, String tempName, String tempFileSuffix) throws IOException {
		try (InputStream inputStream = Utils.class.getResourceAsStream(fileName)) {
			File tempFile = File.createTempFile(tempName, tempFileSuffix);
			tempFile.deleteOnExit();
			Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return tempFile.toURI();
		}
	}
	
	public static List<String> readAllLines(String fileName) throws Exception {
		List<String> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getClass().getResourceAsStream(fileName)))) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		}
		return list;
	}
	
	public static int[] listIntToArray(List<Integer> list) {
		int[] result = list.stream().mapToInt((Integer v) -> v).toArray();
		return result;
	}
	
	public static float[] listToArray(List<Float> list) {
		int size = list != null ? list.size() : 0;
		float[] floatArr = new float[size];
		for (int i = 0; i < size; i++) {
			floatArr[i] = list.get(i);
		}
		return floatArr;
	}
	
	public static boolean existsResourceFile(String fileName) {
		boolean result;
		try (InputStream is = Utils.class.getResourceAsStream(fileName)) {
			result = is != null;
		}
		catch (Exception excp) {
			result = false;
		}
		return result;
	}
	
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		resource = loadFullResourcePath(resource);
		
		ByteBuffer buffer;
		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1);
			}
		} else {
			try (InputStream source = Utils.class.getResourceAsStream(resource); ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = createByteBuffer(bufferSize);
				
				while (true) {
					int bytes = rbc.read(buffer);
					if (bytes == -1) {
						break;
					}
					if (buffer.remaining() == 0) {
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
					}
				}
			}
		}
		
		buffer.flip();
		return buffer;
	}
	
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
	
	public static <T> T[][] transpose(final T[][] array) {
		Objects.requireNonNull(array);
		// get y count
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
}
