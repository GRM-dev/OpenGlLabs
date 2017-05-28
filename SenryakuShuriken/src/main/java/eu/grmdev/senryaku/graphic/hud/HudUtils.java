package eu.grmdev.senryaku.graphic.hud;

import static org.lwjgl.nanovg.NanoVG.*;

import org.joml.Vector2d;
import org.lwjgl.nanovg.NVGColor;

import eu.grmdev.senryaku.game.hud.HudObj;

public class HudUtils {
	
	public static NVGColor rgba(int r, int g, int b, int a, NVGColor color) {
		color.r(r / 255.0f);
		color.g(g / 255.0f);
		color.b(b / 255.0f);
		color.a(a / 255.0f);
		return color;
	}
	
	public static void drawRectangle(long nvg, int x, int y, int width, int height, NVGColor color) {
		nvgBeginPath(nvg);
		nvgRect(nvg, x, y, width, height);
		nvgFillColor(nvg, color);
		nvgFill(nvg);
	}
	
	public static void renderText(long nvg, String text, float x, float y, float size, String font, int alignment, NVGColor color) {
		nvgFontSize(nvg, size);
		nvgFontFace(nvg, font);
		nvgTextAlign(nvg, alignment);
		nvgFillColor(nvg, color);
		nvgText(nvg, x, y, text);
	}
	
	public static boolean isHoveringOn(float x, float y, float w, float h, Vector2d pos) {
		return pos.x > x && pos.x < x + w && pos.y > y && pos.y < y + h;
	}
	
	public static boolean isHoveringOn(HudObj o, Vector2d currentPos) {
		return isHoveringOn(o.getX(), o.getY(), o.getW(), o.getH(), currentPos);
	}
	
}
