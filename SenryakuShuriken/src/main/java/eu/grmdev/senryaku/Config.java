package eu.grmdev.senryaku;

import eu.grmdev.senryaku.graphic.Window;

/**
 * Static final config values for game
 */
public final class Config {
	public final Window.WindowOptions opts;
	public final boolean vSync = true;
	public static final float PLAYER_DEF_Y_POS = 0.5f;
	public static final String IMAGE_FORMAT = "png";
	public static final String FONT_NAME = "BOLD";
	public static final int TARGET_UPS = 20;
	public static final int TARGET_FPS = 60;
	public static final int MAX_SPOT_LIGHTS = 5;
	public static final int MAX_POINT_LIGHTS = 5;
	public static final float Z_FAR = 1000.f;
	public static final float Z_NEAR = 0.01f;
	public static final float FOV = (float) Math.toRadians(60.0f);
	public static final boolean SHADOWS_ENABLED = true;
	public final static float CAMERA_POS_STEP = 0.7f;
	public final static float MOUSE_SENSITIVITY = 0.2f;
	public static final int NUM_SHADOW_CASCADES = 3;
	public static final float[] SHADOW_CASCADE_SPLITS = new float[]{Z_FAR / 20.0f, Z_FAR / 10.0f, Z_FAR};
	public static final int SHADOW_MAP_WIDTH = (int) Math.pow(65, 2);
	public static final int SHADOW_MAP_HEIGHT = SHADOW_MAP_WIDTH;
	public static final boolean FOG_ENABLED = false;
	public static final String SAVE_FILE_NAME = "save.dat";
	
	public Config() {
		opts = new Window.WindowOptions();
		opts.cullFace = false;
		opts.showFps = true;
		opts.compatibleProfile = true;
		opts.antialiasing = true;
		opts.frustumCulling = false;
		opts.maximized = false;
		opts.width = 800;
		opts.height = 600;
	}
}