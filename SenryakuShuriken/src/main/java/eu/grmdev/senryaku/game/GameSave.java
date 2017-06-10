package eu.grmdev.senryaku.game;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.map.GameMap;

public class GameSave implements Serializable {
	private static final long serialVersionUID = 1L;
	private static GameSave save = new GameSave();
	private Map<Integer, Integer> moveScores;
	private static File file;
	
	private GameSave() {
		moveScores = new HashMap<>();
		
	}
	
	public static void init() {
		loadStateFromFile();
		
	}
	
	private static void loadStateFromFile() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getFile()))) {
			GameSave s = (GameSave) ois.readObject();
			if (s == null || s.moveScores == null || s.moveScores.isEmpty()) { return; }
			save = s;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveToFile() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFile()))) {
			oos.writeObject(save);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static File getFile() throws IOException {
		if (file == null) {
			file = new File(Config.SAVE_FILE_NAME.<String> get());
			if (!file.exists()) {
				file.createNewFile();
			}
		}
		return file;
	}
	
	public static void save(Integer level, GameMap gameMap) {
		int score = gameMap.getScore();
		if (!save.moveScores.containsKey(level)) {
			save.moveScores.put(level, score);
		} else if (score > 0 && save.moveScores.get(level) > score) {
			save.moveScores.put(level, score);
		}
	}
	
	public static int getScore(int level) {
		if (save.moveScores.containsKey(level)) { return save.moveScores.get(level); }
		return 0;
	}
}
