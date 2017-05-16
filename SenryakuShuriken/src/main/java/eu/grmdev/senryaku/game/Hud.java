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

import eu.grmdev.senryaku.Main.Config;
import eu.grmdev.senryaku.core.Utils;
import eu.grmdev.senryaku.core.Window;

public class Hud {
	private long nvg;
	private NVGColor color;
	private ByteBuffer fontBuffer;
	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private DoubleBuffer posx;
	private DoubleBuffer posy;
	private int counter;
	
	public void init(Window window) throws Exception {
		this.nvg = window.getWindowOptions().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);
		if (this.nvg == NULL) { throw new Exception("Cannot init nanovg"); }
		
		fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024);
		int font = nvgCreateFontMem(nvg, Config.FONT_NAME, fontBuffer, 0);
		if (font == -1) { throw new Exception("Could not add font"); }
		
		color = NVGColor.create();
		posx = MemoryUtil.memAllocDouble(1);
		posy = MemoryUtil.memAllocDouble(1);
		counter = 0;
	}
	
	public void render(Window window) {
		nvgBeginFrame(nvg, window.getWidth(), window.getHeight(), 1);
		
		drawRibbon(window, 0, 0, window.getWidth(), 50, rgba(0x23, 0xa1, 0xff, 200, color));
		drawRibbon(window, 0, 50, window.getWidth(), 10, rgba(0xc1, 0xe3, 0xf9, 200, color));
		drawRibbon(window, 0, window.getHeight() - 100, window.getWidth(), 50, rgba(0x23, 0xa1, 0xff, 200, color));
		drawRibbon(window, 0, window.getHeight() - 50, window.getWidth(), 10, rgba(0xc1, 0xe3, 0xf9, 200, color));
		
		glfwGetCursorPos(window.getWindowHandle(), posx, posy);
		
		renderScore(window);
		renderClock(window);
		
		nvgEndFrame(nvg);
		window.restoreState();
	}
	
	private void drawRibbon(Window window, int x, int y, int width, int height, NVGColor color) {
		nvgBeginPath(nvg);
		nvgRect(nvg, 0, 0, width, height);
		nvgFillColor(nvg, color);
		nvgFill(nvg);
	}
	
	private void renderScore(Window window) {
		int xcenter = 50;
		int ycenter = 30;
		int radius = 17;
		int x = (int) posx.get(0);
		int y = (int) posy.get(0);
		
		nvgBeginPath(nvg);
		nvgCircle(nvg, xcenter, ycenter, radius);
		nvgFillColor(nvg, rgba(0xc1, 0xe3, 0xf9, 200, color));
		nvgFill(nvg);
		
		nvgFontSize(nvg, 25.0f);
		nvgFontFace(nvg, Config.FONT_NAME);
		nvgTextAlign(nvg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
		
		boolean hover = Math.pow(x - xcenter, 2) + Math.pow(y - ycenter, 2) < Math.pow(radius, 2);
		if (hover) {
			nvgFillColor(nvg, rgba(0x00, 0x00, 0x00, 255, color));
		} else {
			nvgFillColor(nvg, rgba(0x23, 0xa1, 0xf1, 255, color));
		}
		nvgText(nvg, 50, 17, String.format("%02d", counter));
	}
	
	private void renderClock(Window window) {
		nvgFontSize(nvg, 40.0f);
		nvgFontFace(nvg, Config.FONT_NAME);
		nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
		nvgFillColor(nvg, rgba(0xe6, 0xea, 0xed, 255, color));
		nvgText(nvg, window.getWidth() - 150, 5, dateFormat.format(new Date()));
	}
	
	public void incCounter() {
		counter++;
		if (counter > 99) {
			counter = 0;
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
		nvgDelete(nvg);
		if (posx != null) {
			MemoryUtil.memFree(posx);
		}
		if (posy != null) {
			MemoryUtil.memFree(posy);
		}
	}
}
