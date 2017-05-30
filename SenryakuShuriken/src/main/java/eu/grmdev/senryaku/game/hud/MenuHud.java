package eu.grmdev.senryaku.game.hud;

import static org.lwjgl.nanovg.NanoVG.*;

import org.joml.Vector4i;
import org.lwjgl.nanovg.NVGColor;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.Main;
import eu.grmdev.senryaku.core.events.listeners.GameEventListener;
import eu.grmdev.senryaku.core.handlers.LevelManager;
import eu.grmdev.senryaku.graphic.hud.HudUtils;

public class MenuHud {
	private long nvg;
	private NVGColor color;
	private Hud hud;
	private GameEventListener resumeListener;
	private GameEventListener restartListener;
	private GameEventListener exitListener;
	
	public MenuHud(Hud hud) {
		this.hud = hud;
		this.nvg = hud.getNvg();
		this.color = NVGColor.create();
		resumeListener = event -> hud.setMenuActive(false);
		restartListener = event -> {
			LevelManager.getInstance().restartLevel();
			resumeListener.actionPerformed(null);
		};
		exitListener = event -> Main.closeApp();
	}
	
	public void render() {
		int cx = hud.getWindow().getWidth() / 2;
		int cy = hud.getWindow().getHeight() / 2;
		int w = HudObj.MENU_WINDOW.getX();
		int h = HudObj.MENU_WINDOW.getY();
		int leftMargin = cx - w / 2;
		int topMargin = cy - h / 2;
		HudObj mcb = HudObj.MENU_CLOSE_BUTTON;
		HudUtils.drawRectangle(nvg, 0, 0, hud.getWindow().getWidth(), hud.getWindow().getHeight(), HudUtils.rgba(0xCC, 0xCC, 0xCC, 0x55, color));
		renderMenuWindow(cx, cy, w, h, leftMargin, topMargin);
		drawButtons(cx, cy, w, h, leftMargin, topMargin, mcb);
	}
	
	private void renderMenuWindow(int cx, int cy, int w, int h, int leftMargin, int topMargin) {
		HudUtils.drawRectangle(nvg, leftMargin - 10, topMargin - 10, w + 20, h + 20, hud.MAIN_COLOR_BRIGHTER());
		HudUtils.drawRectangle(nvg, leftMargin, topMargin, w, h, hud.MAIN_COLOR_DARKER());
		HudUtils.renderText(nvg, "MENU", cx - 70, topMargin, 60f, Config.FONT_NAME, NVG_ALIGN_LEFT | NVG_ALIGN_TOP, HudUtils.rgba(0xe6, 0xea, 0xed, 255, color));
	}
	
	private void drawButtons(int cx, int cy, int windowWidth, int windowHeight, int leftMargin, int topMargin, HudObj mcb) {
		drawButton("Resume Game", cx - mcb.getX() / 2, topMargin + mcb.getY(), mcb.getX(), mcb.getY(), 45f, resumeListener);
		drawButton("Restart Level", cx - mcb.getX() / 2, topMargin + mcb.getY() * 2 + 10, mcb.getX(), mcb.getY(), 45f, restartListener);
		drawButton("Exit Game", cx - mcb.getX() / 2, topMargin + mcb.getY() * 3 + 20, mcb.getX(), mcb.getY(), 45f, exitListener);
	}
	
	private void drawButton(String text, int x, int y, int w, int h, float size, GameEventListener action) {
		HudUtils.drawButton(nvg, text, x, y, w, h, size, HudUtils.CENTER_MID_ALIGNMENT, new Vector4i(0xe6, 0xea, 0xed, 255), BUTTON_BG_COLOR(), action, hud.getMouseHandler().getCurrentPos(), hud.getMouseHandler().isLeftButtonPressed());
	}
	
	private NVGColor BUTTON_BG_COLOR() {
		return HudUtils.rgba(0x1f, 0x2c, 0x1c, 255, color);
	}
}
