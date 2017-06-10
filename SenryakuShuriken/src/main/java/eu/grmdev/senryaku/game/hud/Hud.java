package eu.grmdev.senryaku.game.hud;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joml.Vector4i;
import org.lwjgl.nanovg.NVGColor;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.events.listeners.GameEventListener;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.handlers.MouseHandler;
import eu.grmdev.senryaku.core.map.LevelManager;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.Window;
import eu.grmdev.senryaku.graphic.hud.HudUtils;
import lombok.Getter;
import lombok.Setter;

public class Hud {
	private @Getter long nvg;
	private @Getter Window window;
	private MenuHud menuHud;
	private @Getter MouseHandler mouseHandler;
	private @Getter EventHandler eventHandler;
	private @Setter @Getter boolean showEndLevelScreen;
	private @Getter @Setter boolean menuActive;
	private ByteBuffer fontBuffer;
	private NVGColor color;
	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private boolean initialized;
	private GameEventListener nextLevelListener;
	private GameEventListener openMenuListener;
	
	public void initLogic(EventHandler eh, MouseHandler mh) throws Exception {
		eventHandler = eh;
		mouseHandler = mh;
		assignHudListeners();
		initialized = true;
	}
	
	public void initRender(Window window) throws Exception {
		this.window = window;
		this.nvg = window.getWindowOptions().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);
		if (this.nvg == NULL) { throw new Exception("Cannot init nanovg"); }
		
		fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", true, 150 * 1024);
		int font = nvgCreateFontMem(nvg, Config.FONT_NAME.<String> get(), fontBuffer, 0);
		if (font == -1) { throw new Exception("Could not add font"); }
		color = NVGColor.create();
		
		menuHud = new MenuHud(this);
	}
	
	private void assignHudListeners() {
		eventHandler.addHudKeyEventListener(event -> {
			int key = event.getKey();
			switch (key) {
				case GLFW_KEY_ESCAPE :
					if (menuActive) {
						menuActive = false;
						event.setConsumed(true);
					}
					break;
			}
		});
		nextLevelListener = event -> {
			try {
				LevelManager.getInstance().goToNextLevel();
				setShowEndLevelScreen(false);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		};
		openMenuListener = event -> {
			menuActive = true;
		};
	}
	
	public void render() {
		if (!initialized) { return; }
		
		nvgBeginFrame(nvg, window.getWidth(), window.getHeight(), 1);
		checkClicks();
		
		HudUtils.drawRectangle(nvg, 0, 0, window.getWidth(), 45, MAIN_COLOR_BRIGHTER());
		HudUtils.drawRectangle(nvg, 0, 45, window.getWidth(), 10, MAIN_COLOR_DARKER());
		HudUtils.drawRectangle(nvg, 0, window.getHeight() - 80, window.getWidth(), 20, MAIN_COLOR_DARKER());
		HudUtils.drawRectangle(nvg, 0, window.getHeight() - 60, window.getWidth(), 60, MAIN_COLOR_BRIGHTER());
		
		renderMenuButton();
		nvgBeginPath(nvg);
		renderTopHudInfo();
		
		if (showEndLevelScreen && !menuActive) {
			renderEndLevelScreen();
		}
		if (menuActive) {
			menuHud.render();
		}
		
		nvgEndFrame(nvg);
		window.restoreState();
	}
	
	public NVGColor MAIN_COLOR_BRIGHTER() {
		return HudUtils.rgba(0xb3, 0x40, 0x19, 200, color);
	}
	
	public NVGColor MAIN_COLOR_DARKER() {
		return HudUtils.rgba(0x5f, 0x16, 0x07, 200, color);
	}
	
	private void checkClicks() {
		if (mouseHandler.isLeftButtonPressed()) {
			if (!menuActive && !showEndLevelScreen && HudUtils.isHoveringOn(HudObjs.MENU_BUTTON, mouseHandler.getCurrentPos())) {
				menuActive = true;
			}
		}
	}
	
	private void renderMenuButton() {
		nvgBeginPath(nvg);
		HudObjs mb = HudObjs.MENU_BUTTON;
		nvgRoundedRect(nvg, mb.getX(), mb.getY(), mb.getW(), mb.getH(), mb.getRounding());
		nvgFillColor(nvg, HudUtils.rgba(0x99, 0x78, 0x5f, 200, color));
		nvgFill(nvg);
		
		boolean active = HudUtils.isHoveringOn(mb, mouseHandler.getCurrentPos()) && !isMenuActive() && !isShowEndLevelScreen();
		if (active) {
			nvgFillColor(nvg, HudUtils.rgba(0x00, 0x00, 0x00, 0xff, color));
		} else {
			nvgFillColor(nvg, HudUtils.rgba(0xfd, 0xe1, 0xab, 0xff, color));
		}
		HudUtils.renderText(nvg, "Menu", mb.getX() + 30, mb.getY() + 1, 25.0f, Config.FONT_NAME.<String> get(), NVG_ALIGN_CENTER | NVG_ALIGN_TOP, color);
	}
	
	private void renderTopHudInfo() {
		LevelManager lm = LevelManager.getInstance();
		if (lm == null || lm.getCurrentMap() == null) { return; }
		nvgFillColor(nvg, HudUtils.rgba(0xe6, 0xea, 0xed, 0xff, color));
		String text = "Moves: " + lm.getCurrentMap().getStepCounter() + "      Map title: '" + lm.getCurrentMap().getTitle() + "', lvl: " + lm.getCurrentMap().getLevel();
		HudUtils.renderText(nvg, text, 80, 8, 30.0f, Config.FONT_NAME.<String> get(), HudUtils.LEFT_TOP_ALIGNMENT, color);
		HudUtils.renderText(nvg, dateFormat.format(new Date()), window.getWidth() - 150, 5, 40.0f, Config.FONT_NAME.<String> get(), HudUtils.LEFT_TOP_ALIGNMENT, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
	}
	
	private void renderEndLevelScreen() {
		int cx = window.getWidth() / 2;
		int cy = window.getHeight() / 2;
		int sx = HudObjs.LEVEL_COMPLETE_WINDOW.getX();
		int sy = HudObjs.LEVEL_COMPLETE_WINDOW.getY();
		int leftMargin = cx - sx / 2;
		int topMargin = cy - sy / 2;
		HudUtils.fadeScreen(nvg, window.getWidth(), window.getHeight(), color);
		HudUtils.drawRectangle(nvg, leftMargin - 10, topMargin - 10, sx + 20, sy + 20, MAIN_COLOR_BRIGHTER());
		HudUtils.drawRectangle(nvg, leftMargin, topMargin, sx, sy, MAIN_COLOR_DARKER());
		int firstTextX = cx - sx / 3;
		int firstTextY = topMargin + 5;
		String font = Config.FONT_NAME.<String> get();
		HudUtils.renderText(nvg, "Level Complete!", firstTextX, firstTextY, 60f, font, HudUtils.LEFT_TOP_ALIGNMENT, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
		HudUtils.renderText(nvg, "You scored:", leftMargin + 5, firstTextY + 70, 40f, font, HudUtils.LEFT_TOP_ALIGNMENT, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
		HudUtils.renderText(nvg, "Best score:", leftMargin + 5, firstTextY + 110, 40f, font, HudUtils.LEFT_TOP_ALIGNMENT, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
		LevelManager lm = LevelManager.getInstance();
		int score = lm.getCurrentMap().getScore();
		int bScore = lm.getCurrentMap().getBestScore();
		bScore = bScore == 0 ? score : bScore;
		HudUtils.renderText(nvg, score + " moves", cx + 60, firstTextY + 70, 40f, font, HudUtils.LEFT_TOP_ALIGNMENT, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
		HudUtils.renderText(nvg, bScore + " moves", cx + 60, firstTextY + 110, 40f, font, HudUtils.LEFT_TOP_ALIGNMENT, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
		
		if (lm.nextMapExist()) {
			HudUtils.drawButton(nvg, "Next Level", leftMargin + 5, cy + sy / 2 - 80, 240, 80, 40f, HudUtils.CENTER_MID_ALIGNMENT, new Vector4i(0xe6, 0xea, 0xed, 255), HudUtils.BUTTON_BG_COLOR(color), nextLevelListener, mouseHandler.getCurrentPos(),
				mouseHandler.isLeftButtonPressed() && !menuActive);
		}
		HudUtils.drawButton(nvg, "Open Menu", leftMargin + 255, cy + sy / 2 - 80, 240, 80, 40f, HudUtils.CENTER_MID_ALIGNMENT, new Vector4i(0xe6, 0xea, 0xed, 255), HudUtils.BUTTON_BG_COLOR(color), openMenuListener, mouseHandler.getCurrentPos(),
			mouseHandler.isLeftButtonPressed() && !menuActive);
	}
	
	public void destroy() {
		if (nvg != 0) {
			nvgDelete(nvg);
		}
		menuActive = false;
	}
}
