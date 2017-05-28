package eu.grmdev.senryaku.graphic.hud;

import static org.lwjgl.nanovg.NanoVG.*;

import org.joml.Vector2d;
import org.joml.Vector4i;
import org.lwjgl.nanovg.NVGColor;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.core.events.GameEvent;
import eu.grmdev.senryaku.core.events.listeners.GameEventListener;
import eu.grmdev.senryaku.game.hud.HudObj;

public class HudUtils {
	
	private static final int DEFAULT_ALIGNMENT = NVG_ALIGN_LEFT | NVG_ALIGN_TOP;
	
	public static NVGColor rgba(int r, int g, int b, int a, NVGColor color) {
		color.r(r / 255.0f);
		color.g(g / 255.0f);
		color.b(b / 255.0f);
		color.a(a / 255.0f);
		return color;
	}
	
	public static NVGColor rgba(Vector4i c, NVGColor ch) {
		return rgba(c.x, c.y, c.z, c.w, ch);
	}
	
	private static NVGColor rgbai(Vector4i iv, NVGColor color) {
		return rgba((int) (color.r() * 255 + iv.x), (int) (color.g() * 255 + iv.y), (int) (color.b() * 255 + iv.z), (int) (color.a() * 255 + iv.w), color);
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
	
	public static void drawButton(long nvg, String text, int x, int y, int width, int heigth, float size, Vector4i textColor, NVGColor bgColor, GameEventListener action, Vector2d currentPos, boolean clicked) {
		if (isHoveringOn(x, y, width, heigth, currentPos)) {
			rgbai(new Vector4i(-20, 0, 0, 0), bgColor);
			if (clicked) {
				GameEvent event = new GameEvent(false, null) {};
				action.actionPerformed(event);
			}
		} else {
			rgbai(new Vector4i(20, 0, 0, 0), bgColor);
		}
		drawRectangle(nvg, x, y, width, heigth, bgColor);
		renderText(nvg, text, x + 80, y + 10, size, Config.FONT_NAME, DEFAULT_ALIGNMENT, rgba(textColor, bgColor));
		
	}
	
}
