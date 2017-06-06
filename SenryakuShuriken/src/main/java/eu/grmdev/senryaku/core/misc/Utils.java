package eu.grmdev.senryaku.core.misc;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.assimp.Assimp.aiImportFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIScene;

public class Utils {
	
	public static String loadResourceContent(String fileName) throws Exception {
		String result;
		try (InputStream in = loadResourceAsStream(fileName); Scanner scanner = new Scanner(in, "UTF-8")) {
			result = scanner.useDelimiter("\\A").next();
		}
		return result;
	}
	
	public static String loadResourcePath(String fileName, boolean innerResource) {
		if (innerResource) {
			return Utils.class.getResource(fileName).getFile();
		} else {
			return "./resources" + (fileName.startsWith("/") ? "" : "/") + fileName;
		}
	}
	
	public static File getFile(String fileName) throws MalformedURLException {
		String path = fileName.startsWith("./resources") ? fileName : loadResourcePath(fileName, false);
		return new File(path);
	}
	
	public static String loadFullResourcePath(String filename, boolean innerResource) {
		File f = new File(loadResourcePath(filename, innerResource));
		if (!f.exists()) {
			System.out.println("!!!!!!!!! No File: " + filename + "  -> inner: " + innerResource);
			return null;
		}
		return f.getPath();
	}
	
	public static InputStream loadResourceAsStream(String fileName) {
		return Utils.class.getResourceAsStream(fileName);
	}
	
	public static AIScene loadAssimpObject(String filename, int flags) throws Exception {
		System.out.println(" /===== Mesh: =====\\");
		System.out.println(filename);
		System.out.println("  \\-------------/");
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
	
	public static List<String> readAllLines(String fileName) throws IOException {
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
	
	public static ByteBuffer ioResourceToByteBuffer(String resource, boolean innerResource, int bufferSize) throws IOException {
		if (!innerResource) {
			resource = loadFullResourcePath(resource, false);
		}
		System.out.println(" /===== Texture: =====\\");
		System.out.println(resource);
		System.out.println("      \\==========/");
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
}
