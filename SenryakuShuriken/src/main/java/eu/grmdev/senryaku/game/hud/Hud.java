package eu.grmdev.senryaku.game.hud;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.nanovg.NVGColor;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.handlers.EventHandler;
import eu.grmdev.senryaku.core.handlers.MouseHandler;
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
	private @Getter boolean menuActive;
	private ByteBuffer fontBuffer;
	private NVGColor color;
	private int counter = 0;
	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private boolean initialized;
	
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
		int font = nvgCreateFontMem(nvg, Config.FONT_NAME, fontBuffer, 0);
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
					}
					break;
			}
		});
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
		renderScore();
		renderClock();
		
		if (showEndLevelScreen) {
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
			if (!menuActive && HudUtils.isHoveringOn(HudObj.MENU_BUTTON, mouseHandler.getCurrentPos())) {
				menuActive = true;
			}
		}
	}
	
	private void renderMenuButton() {
		nvgBeginPath(nvg);
		HudObj mb = HudObj.MENU_BUTTON;
		nvgRoundedRect(nvg, mb.getX(), mb.getY(), mb.getW(), mb.getH(), mb.getRounding());
		nvgFillColor(nvg, HudUtils.rgba(0x99, 0x78, 0x5f, 200, color));
		nvgFill(nvg);
		
		boolean hover = HudUtils.isHoveringOn(mb, mouseHandler.getCurrentPos());
		if (hover) {
			nvgFillColor(nvg, HudUtils.rgba(0x00, 0x00, 0x00, 0xff, color));
		} else {
			nvgFillColor(nvg, HudUtils.rgba(0xfd, 0xe1, 0xab, 0xff, color));
		}
		HudUtils.renderText(nvg, "Menu", mb.getX() + 30, mb.getY() + 1, 25.0f, Config.FONT_NAME, NVG_ALIGN_CENTER | NVG_ALIGN_TOP, color);
	}
	
	private void renderScore() {
		nvgBeginPath(nvg);
		nvgFillColor(nvg, HudUtils.rgba(0xe6, 0xea, 0xed, 0xff, color));
		HudUtils.renderText(nvg, "Moves: " + String.format("%02d", counter), 150, 8, 30.0f, Config.FONT_NAME, NVG_ALIGN_CENTER | NVG_ALIGN_TOP, color);
	}
	
	private void renderClock() {
		HudUtils.renderText(nvg, dateFormat.format(new Date()), window.getWidth() - 150, 5, 40.0f, Config.FONT_NAME, NVG_ALIGN_LEFT | NVG_ALIGN_TOP, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
	}
	
	private void renderEndLevelScreen() {
		int cx = window.getWidth() / 2;
		int cy = window.getHeight() / 2;
		int sx = HudObj.LEVEL_COMPLETE_WINDOW.getX();
		int sy = HudObj.LEVEL_COMPLETE_WINDOW.getY();
		HudUtils.drawRectangle(nvg, cx - sx / 2 - 10, cy - sy / 2 - 10, sx + 20, sy + 20, MAIN_COLOR_BRIGHTER());
		HudUtils.drawRectangle(nvg, cx - sx / 2, cy - sy / 2, sx, sy, MAIN_COLOR_DARKER());
		HudUtils.renderText(nvg, "Level Complete!", cx - sx / 3, cy - sy / 3, 60f, Config.FONT_NAME, NVG_ALIGN_LEFT | NVG_ALIGN_TOP, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
	}
	
	public void incCounter() {
		if (counter < 99) {
			counter++;
		}
	}
	
	public void destroy() {
		if (nvg != 0) {
			nvgDelete(nvg);
		}
	}
}
