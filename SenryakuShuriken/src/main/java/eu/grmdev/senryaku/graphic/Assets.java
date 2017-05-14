package eu.grmdev.senryaku.graphic;

import lombok.Getter;

public class Assets {
	
	public static void loadAssets() {
		
	}
	
	public enum Asset {
		WOOD_1("wood_1") ,
		WOOD_2("wood_2");
		
		@Getter
		private String file;
		private Texture txt;
		
		private Asset(String file) {
			this.file = file;
		}
		
		public Texture GetTexture() {
			if (txt == null) {
				txt = Texture.loadTexture(file);
			}
			return txt;
		}
	}
}
