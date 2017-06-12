package eu.grmdev.senryaku.core.entity;

import org.joml.Vector4f;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.loaders.obj.StaticMeshesLoader;
import eu.grmdev.senryaku.graphic.mesh.*;

public class SkyBox extends Entity {
	
	public SkyBox(String objModel, String textureFile, IGame game) throws Exception {
		super(game);
		Mesh skyBoxMesh = StaticMeshesLoader.load(objModel, "")[0];
		Texture skyBoxtexture = new Texture(textureFile);
		skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
		skyBoxMesh.setNormalsEnabled(false);
		setMesh(skyBoxMesh);
		setPosition(0, 0, 0);
	}
	
	public SkyBox(String objModel, Vector4f colour, IGame game) throws Exception {
		super(game);
		Mesh skyBoxMesh = StaticMeshesLoader.load(objModel, "", 0)[0];
		Material material = new Material(colour, 0);
		skyBoxMesh.setMaterial(material);
		setMesh(skyBoxMesh);
		setPosition(0, 0, 0);
	}
	
}
