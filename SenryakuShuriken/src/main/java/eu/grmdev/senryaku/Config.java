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
	private @Setter float[] va;
	private byte type = 0;
	/*
	 * byte fT=1;
	 * byte faT=2;
	 * byte sT=3;
	 * byte bT=4;
	 * byte iT=5;
	 */
	
	private Config(float f) {
		this.f = f;
		type = 1;
	}
	
	private Config(boolean b) {
		this.b = b;
		type = 4;
	}
	
	private Config(int i) {
		this.i = i;
		type = 5;
	}
	
	private Config(String s) {
		this.s = s;
		type = 3;
	}
	
	private Config(float... va) {
		this.va = va;
		type = 2;
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
				return new String(Arrays.toString(getArray()));
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
		return va;
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
}