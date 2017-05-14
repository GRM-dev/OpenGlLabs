package eu.grmdev.senryaku.core.tile;

import static org.lwjgl.opengl.GL15.*;

import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.entity.VertexBufferObject;
import eu.grmdev.senryaku.graphic.Assets.Asset;
import eu.grmdev.senryaku.graphic.GameWindow;
import eu.grmdev.senryaku.graphic.Texture;

public class Tile extends Entity {
	
	private int size;
	private float[] vertices;
	private int[] indices;
	private float[] tex;
	private Asset asset;
	
	public Tile(int x, int y, int z, float[] vertices, int[] indices, float[] tex, Asset asset) {
		super();
		transformation.move(x, y, z);
		this.vertices = vertices;
		this.indices = indices;
		this.tex = tex;
		this.size = indices.length;
		this.asset = asset;
	}
	
	@Override
	public void init() {
		mesh.init();
		Texture texture = asset.GetTexture();
		this.mesh.setTexture(texture);
		mesh.bind();
		
		VertexBufferObject vert_vbo = new VertexBufferObject();
		vert_vbo.bind(GL_ARRAY_BUFFER);
		vert_vbo.setData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		mesh.setVert_vbo(vert_vbo);
		
		VertexBufferObject tex_vbo = new VertexBufferObject();
		tex_vbo.bind(GL_ARRAY_BUFFER);
		tex_vbo.setData(GL_ARRAY_BUFFER, tex, GL_STATIC_DRAW);
		mesh.setTex_vbo(tex_vbo);
		
		VertexBufferObject ind_vbo = new VertexBufferObject();
		ind_vbo.bind(GL_ELEMENT_ARRAY_BUFFER);
		ind_vbo.setData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		mesh.setInd_vbo(ind_vbo);
		
		tex_vbo.unbind(GL_ELEMENT_ARRAY_BUFFER);
		vert_vbo.unbind(GL_ARRAY_BUFFER);
		mesh.unbind();
		
	}
	
	@Override
	protected void draw(GameWindow window) {
		mesh.render(this);
	}
	
}
