package eu.grmdev.senryaku.misc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import eu.grmdev.senryaku.core.map.Terrain;
import eu.grmdev.senryaku.core.misc.VectorUtils;

public class TestVectorUtils {
	@Test
	public void testCalcNormals() {
		//@// @formatter:off 
		float[] vertices6 = new float[]{
			-1,-1,0,
			-1,1,0,
			1,1,0,
			1,-1,0,
		};
		int[] indices6 = new int[] {
			0,1,2,
			0,2,3,
		};
// @formatter:on
		float[] normals = VectorUtils.calcNormals(vertices6, indices6);
		assertThat(normals).isNotNull().isNotEmpty();
		assertThat(normals.length).isEqualTo(vertices6.length);
		assertThat(normals[2]).isNotEqualTo(0.0f);
		normals = VectorUtils.calcNormals(Terrain.VERTICES, Terrain.INDICES);
		assertThat(normals).isNotNull().isNotEmpty();
		assertThat(normals[normals.length - 3]).isNotEqualTo(0.0f);
	}
}
