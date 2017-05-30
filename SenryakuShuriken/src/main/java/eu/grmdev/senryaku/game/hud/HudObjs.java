package eu.grmdev.senryaku.game.hud;

import lombok.Getter;

public enum HudObjs {
	MENU_BUTTON(12,9,60,30,5) ,
	MENU_WINDOW(500,300) ,
	LEVEL_COMPLETE_WINDOW(500,380) ,
	MENU_MAIN_BUTTON(350,60);
	
	private @Getter int x;
	private @Getter int y;
	private @Getter int w;
	private @Getter int h;
	private @Getter int rounding;
	private @Getter int r;
	
	private HudObjs(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	private HudObjs(int x, int y, int w, int h) {
		this(x, y);
		this.w = w;
		this.h = h;
	}
	
	private HudObjs(int x, int y, int w, int h, int rounding) {
		this(x, y, w, h);
		this.rounding = rounding;
	}
	
	private HudObjs(int x, int y, int r) {
		this(x, y);
		this.r = r;
	}
}
