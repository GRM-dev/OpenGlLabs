package eu.grmdev.senryaku.game.hud;

import static org.lwjgl.nanovg.NanoVG.*;

import org.lwjgl.nanovg.NVGColor;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.graphic.hud.HudUtils;

public class MenuHud {
	private long nvg;
	private NVGColor color;
	private Hud hud;
	
	public MenuHud(Hud hud) {
		this.hud = hud;
		this.nvg = hud.getNvg();
		this.color = NVGColor.create();
	}
	
	public void render() {
		int cx = hud.getWindow().getWidth() / 2;
		int cy = hud.getWindow().getHeight() / 2;
		int sx = HudObj.MENU_WINDOW.getX();
		int sy = HudObj.MENU_WINDOW.getY();
		HudUtils.drawRectangle(nvg, cx - sx / 2 - 10, cy - sy / 2 - 10, sx + 20, sy + 20, hud.MAIN_COLOR_BRIGHTER());
		HudUtils.drawRectangle(nvg, cx - sx / 2, cy - sy / 2, sx, sy, hud.MAIN_COLOR_DARKER());
		HudUtils.renderText(nvg, "MENU", cx - 70, cy - sy / 2, 60f, Config.FONT_NAME, NVG_ALIGN_LEFT | NVG_ALIGN_TOP, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
	}
	
}
