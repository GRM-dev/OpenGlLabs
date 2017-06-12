package eu.grmdev.senryaku;

import java.util.Arrays;

import lombok.Setter;

/**
 * Static final config values for game
 */
public enum Config {
	PLAYER_DEF_Y_POS(0.5f) ,
	IMAGE_FORMAT("png") ,
	FONT_NAME("BOLD") ,
	TARGET_UPS(20) ,
	TARGET_FPS(60) ,
	MAX_SPOT_LIGHTS(5) ,
	MAX_POINT_LIGHTS(5) ,
	Z_FAR(1000.f) ,
	Z_NEAR(0.01f) ,
	FOV((float) Math.toRadians(60.0f)) ,
	SHADOWS_ENABLED(true) ,
	CAMERA_POS_STEP(0.7f) ,
	MOUSE_SENSITIVITY(0.2f) ,
	NUM_SHADOW_CASCADES(3) ,
	SHADOW_CASCADE_SPLITS(Z_FAR.<Float> get() / 20.0f,Z_FAR.<Float> get() / 10.0f,Z_FAR.<Float> get()) ,
	SHADOW_MAP_WIDTH((int) Math.pow(65, 2)) ,
	SHADOW_MAP_HEIGHT(SHADOW_MAP_WIDTH.<Integer> get()) ,
	FOG_ENABLED(false) ,
	SAVE_FILE_NAME("save.dat") ,
	START_LEVEL(3) ,
	SHOW_DEBUG_INFO(false) ,
	CAMERA_OFFSET_X(0.3f) ,
	CAMERA_OFFSET_Y(0f) ,
	CAMERA_OFFSET_Z(3f) ,
	CLOSE_GUI_ON_GAME_START(false);
	
	private @Setter Float f;
	private @Setter Boolean b;
	private @Setter Integer i;
	private @Setter String s;
	private @Setter float[] fa;
	
	private Float df;
	private Boolean db;
	private Integer di;
	private String ds;
	private float[] dfa;
	
	/*
	 * f=1;
	 * fa=2;
	 * s=3;
	 * b=4;
	 * i=5;
	 */
	private byte type = 0;
	
	private Config(float f) {
		this.f = f;
		type = 1;
		df = f;
	}
	
	private Config(boolean b) {
		this.b = b;
		type = 4;
		db = b;
	}
	
	private Config(int i) {
		this.i = i;
		type = 5;
		di = i;
	}
	
	private Config(String s) {
		this.s = s;
		type = 3;
		ds = s;
	}
	
	private Config(float... fa) {
		this.fa = fa;
		type = 2;
		dfa = fa;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get() {
		switch (type) {
			case 1 :
				return (T) f;
			case 2 :
				return (T) getArray();
			case 3 :
				return (T) s;
			case 4 :
				return (T) b;
			case 5 :
				return (T) i;
			default :
				throw new ClassCastException("Type for " + name() + " is not valid.");
		}
	}
	
	public String getCasted() {
		switch (type) {
			case 1 :
				return f + "";
			case 2 :
				return Arrays.toString(getArray());
			case 3 :
				return s + "";
			case 4 :
				return b + "";
			case 5 :
				return i + "";
			default :
				throw new ClassCastException("Type String for " + name() + " is not valid.");
		}
	}
	
	public float[] getArray() {
		return fa;
	}
	
	public boolean isBoolean() {
		return type == 4;
	}
	
	public boolean isInt() {
		return type == 5;
	}
	
	public boolean isString() {
		return type == 3;
	}
	
	public boolean isFloat() {
		return type == 1;
	}
	
	public boolean isFloatArray() {
		return type == 2;
	}
	
	public void resetToDefault() {
		fa = dfa;
		f = df;
		b = db;
		i = di;
		s = ds;
		
	}
}