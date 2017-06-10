package eu.grmdev.senryaku.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.config.Configuration;

public class ConfigTest {
	
	@Test
	public void enumCreationTest() {
		Config e = Config.CAMERA_OFFSET_X;
		assertThat(e).isNotNull().isInstanceOf(Config.class);
	}
	
	@Test
	public void enumGetTest() {
		Config e = Config.CAMERA_OFFSET_X;
		assertThat(e).isNotNull().isInstanceOf(Config.class);
		float o = e.get();
		assertThat(o).isNotZero();
		assertThat(Config.FONT_NAME.<String> get()).isNotNull().isNotEmpty();
	}
	
	@Test
	public void enumChangeTest() {
		Config e = Config.FONT_NAME;
		assertThat(e.<String> get()).isNotEqualTo("gg");
		e.setS("gg");
		assertThat(Config.FONT_NAME.<String> get()).isNotNull().isEqualTo("gg");
	}
	
	@Test
	public void saveToFileTest() {
		Configuration c = new Configuration("test.conf");
		assertThatCode(() -> {
			c.saveToFile();
		}).doesNotThrowAnyException();
	}
	
	@Test
	public void readFromFileTest() {
		Configuration c = new Configuration("test.conf");
		assertThatCode(() -> {
			c.loadFromFile();
		}).doesNotThrowAnyException();
		assertThat(Config.CAMERA_OFFSET_X.<Float> get()).isEqualTo(20f);
	}
}
