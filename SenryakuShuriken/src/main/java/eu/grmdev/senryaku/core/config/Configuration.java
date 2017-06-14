package eu.grmdev.senryaku.core.config;

import java.io.*;
import java.util.Properties;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.misc.Utils;
import lombok.Getter;

public class Configuration {
	private String configFile;
	private static @Getter boolean changed;
	
	public Configuration(String configFile) {
		this.configFile = configFile;
	}
	
	public boolean saveToFile() {
		System.out.println("Config: Save to file");
		try {
			checkFile();
			Properties config = new Properties();
			for (Config c : Config.values()) {
				config.setProperty(c.name(), c.getCasted());
			}
			config.store(new FileOutputStream(configFile), null);
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean loadFromFile() {
		System.out.println("Config: Load from file");
		Properties config = new Properties();
		try {
			checkFile();
			config.load(new FileInputStream(configFile));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		String p = null;
		Config c;
		for (final String name : config.stringPropertyNames()) {
			try {
				c = Config.valueOf(name);
				p = config.getProperty(name);
				if (c != null && p != null) {
					if (c.isBoolean()) {
						change(c, Boolean.valueOf(p));
					} else if (c.isFloat()) {
						change(c, Float.parseFloat(p));
					} else if (c.isFloatArray()) {
						change(c, Utils.extractFloats(p));
					} else if (c.isInt()) {
						change(c, Integer.parseInt(p));
					} else if (c.isString()) {
						change(c, p);
					}
				}
			}
			catch (Exception ex) {
				System.err.println("Error while loading property " + name + " & value " + p + ". Got: " + ex);
			}
		}
		return true;
	}
	
	private void checkFile() throws IOException {
		File f = new File(configFile);
		if (!f.exists()) {
			if (!f.createNewFile()) { throw new IOException("File could not be created."); }
		}
	}
	
	public void loadDefaults() {
		System.out.println("Config: Reset to defaults");
		for (Config c : Config.values()) {
			c.resetToDefault();
		}
	}
	
	public static void change(Config c, boolean o) {
		if (c.isBoolean()) {
			c.setB(o);
			changed = true;
		} else {
			System.err.println("Wrong parameter type when saving to config " + c.name());
		}
	}
	
	public static void change(Config c, int o) {
		if (c.isInt()) {
			c.setI(o);
			changed = true;
		} else {
			System.err.println("Wrong parameter type when saving to config " + c.name());
		}
	}
	
	public static void change(Config c, float o) {
		if (c.isFloat()) {
			c.setF(o);
			changed = true;
		} else {
			System.err.println("Wrong parameter type when saving to config " + c.name());
		}
	}
	
	public static void change(Config c, float[] o) {
		if (c.isFloatArray()) {
			c.setFa(o);
			changed = true;
		} else {
			System.err.println("Wrong parameter type when saving to config " + c.name());
		}
	}
	
	public static void change(Config c, String o) {
		if (c.isString()) {
			c.setS(o);
			changed = true;
		} else {
			System.err.println("Wrong parameter type when saving to config " + c.name());
		}
	}
	
	public static void resetChanged(boolean b) {
		changed = false;
	}
}
