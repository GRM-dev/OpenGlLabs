package eu.grmdev.senryaku.game.hud;

import lombok.Getter;

public enum HudObj {
	MENU_BUTTON(12,9,62,30,5) ,
	MENU_WINDOW(500,300) ,
	LEVEL_COMPLETE_WINDOW(500,300) ,
	MENU_CLOSE_BUTTON(350,60);
	
	private @Getter int x;
	private @Getter int y;
	private @Getter int w;
	private @Getter int h;
	private @Getter int rounding;
	private @Getter int r;
	
	private HudObj(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	private HudObj(int x, int y, int w, int h) {
		this(x, y);
		this.w = w;
		this.h = h;
	}
	
	private HudObj(int x, int y, int w, int h, int rounding) {
		this(x, y, w, h);
		this.rounding = rounding;
	}
	
	private HudObj(int x, int y, int r) {
		this(x, y);
		this.r = r;
	}
}
