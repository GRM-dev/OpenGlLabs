package eu.grmdev.senryaku.game;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.misc.Utils;
import eu.grmdev.senryaku.graphic.Window;
import lombok.Getter;
import lombok.Setter;

public class Hud {
	private long nvg;
	private NVGColor color;
	private ByteBuffer fontBuffer;
	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private DoubleBuffer posx;
	private DoubleBuffer posy;
	private int counter;
	private Window window;
	private @Setter @Getter boolean showEndLevelScreen;
	
	public void init(Window window) throws Exception {
		this.window = window;
		this.nvg = window.getWindowOptions().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);
		if (this.nvg == NULL) { throw new Exception("Cannot init nanovg"); }
		
		fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", true, 150 * 1024);
		int font = nvgCreateFontMem(nvg, Config.FONT_NAME, fontBuffer, 0);
		if (font == -1) { throw new Exception("Could not add font"); }
		
		color = NVGColor.create();
		posx = MemoryUtil.memAllocDouble(1);
		posy = MemoryUtil.memAllocDouble(1);
		counter = 0;
	}
	
	public void render() {
		nvgBeginFrame(nvg, window.getWidth(), window.getHeight(), 1);
		
		drawRectangle(0, 0, window.getWidth(), 45, rgba(0xb3, 0x40, 0x19, 200, color));
		drawRectangle(0, 45, window.getWidth(), 10, rgba(0x5f, 0x16, 0x07, 200, color));
		drawRectangle(0, window.getHeight() - 80, window.getWidth(), 20, rgba(0x5f, 0x16, 0x07, 200, color));
		drawRectangle(0, window.getHeight() - 60, window.getWidth(), 60, rgba(0xb3, 0x40, 0x19, 200, color));
		
		glfwGetCursorPos(window.getWindowHandle(), posx, posy);
		
		renderMenuButton();
		renderScore();
		renderClock();
		
		if (showEndLevelScreen) {
			renderEndLevelScreen();
		}
		
		nvgEndFrame(nvg);
		window.restoreState();
	}
	
	private void renderMenuButton() {
		float x = 12;
		float y = 11;
		float w = 60;
		float h = 28;
		float r = 5;
		int mx = (int) posx.get(0);
		int my = (int) posy.get(0);
		
		nvgBeginPath(nvg);
		nvgRoundedRect(nvg, x, y, w, h, r);
		nvgFillColor(nvg, rgba(0x99, 0x78, 0x5f, 200, color));
		nvgFill(nvg);
		
		boolean hover = mx > x && mx < x + w && my > y && my < y + h;
		if (hover) {
			nvgFillColor(nvg, rgba(0x00, 0x00, 0x00, 0xff, color));
		} else {
			nvgFillColor(nvg, rgba(0x23, 0xa1, 0xf1, 0xff, color));
		}
		renderText("Menu", x + 30, 12, 25.0f, Config.FONT_NAME, NVG_ALIGN_CENTER | NVG_ALIGN_TOP, color);
	}
	
	private void renderScore() {
		int xcenter = 150;
		int ycenter = 23;
		
		nvgBeginPath(nvg);
		nvgFillColor(nvg, rgba(0xe6, 0xea, 0xed, 0xff, color));
		renderText("Moves: " + String.format("%02d", counter), xcenter, ycenter - 10, 30.0f, Config.FONT_NAME, NVG_ALIGN_CENTER | NVG_ALIGN_TOP, color);
	}
	
	private void renderClock() {
		renderText(dateFormat.format(new Date()), window.getWidth() - 150, 5, 40.0f, Config.FONT_NAME, NVG_ALIGN_LEFT | NVG_ALIGN_TOP, rgba(0xe6, 0xea, 0xed, 255, color));
	}
	
	private void drawRectangle(int x, int y, int width, int height, NVGColor color) {
		nvgBeginPath(nvg);
		nvgRect(nvg, x, y, width, height);
		nvgFillColor(nvg, color);
		nvgFill(nvg);
	}
	
	private void renderText(String text, float x, float y, float size, String font, int alignment, NVGColor color) {
		nvgFontSize(nvg, size);
		nvgFontFace(nvg, font);
		nvgTextAlign(nvg, alignment);
		nvgFillColor(nvg, color);
		nvgText(nvg, x, y, text);
	}
	
	private void renderEndLevelScreen() {
		int cx = window.getWidth() / 2;
		int cy = window.getHeight() / 2;
		int sx = 500;
		int sy = 300;
		drawRectangle(cx - sx / 2 - 10, cy - sy / 2 - 10, sx + 20, sy + 20, rgba(0x23, 0xa1, 0xff, 200, color));
		drawRectangle(cx - sx / 2, cy - sy / 2, sx, sy, rgba(0xc1, 0xe3, 0xf9, 200, color));
		renderText("Level Complete!", cx - sx / 3, cy - sy / 3, 60f, Config.FONT_NAME, NVG_ALIGN_LEFT | NVG_ALIGN_TOP, rgba(0xe6, 0xea, 0xed, 255, color));
	}
	
	public void incCounter() {
		if (counter < 99) {
			counter++;
		}
	}
	
	private NVGColor rgba(int r, int g, int b, int a, NVGColor color) {
		color.r(r / 255.0f);
		color.g(g / 255.0f);
		color.b(b / 255.0f);
		color.a(a / 255.0f);
		return color;
	}
	
	public void destroy() {
		if (nvg != 0) {
			nvgDelete(nvg);
		}
		if (posx != null) {
			MemoryUtil.memFree(posx);
		}
		if (posy != null) {
			MemoryUtil.memFree(posy);
		}
	}
}
