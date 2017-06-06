package eu.grmdev.senryaku.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

import eu.grmdev.senryaku.core.entity.Direction;

public class TestDirection {
	
	@Test
	public void testGetDir() {
		assertThat(Direction.getDir(0, 1)).isEqualTo(Direction.UP);
		assertThat(Direction.getDir(0, -1)).isEqualTo(Direction.DOWN);
		assertThat(Direction.getDir(1, 0)).isEqualTo(Direction.RIGHT);
		assertThat(Direction.getDir(-1, 0)).isEqualTo(Direction.LEFT);
		assertThat(Direction.getDir(-1, 1)).isEqualTo(Direction.UNDEFINED);
	}
	
	@Test
	public void testRotCcw() {
		assertThat(Direction.DOWN.rotCcw()).isEqualTo(Direction.RIGHT);
		assertThat(Direction.RIGHT.rotCcw()).isEqualTo(Direction.UP);
		assertThat(Direction.UP.rotCcw()).isEqualTo(Direction.LEFT);
		assertThat(Direction.LEFT.rotCcw()).isEqualTo(Direction.DOWN);
		assertThat(Direction.UNDEFINED.rotCcw()).isEqualTo(Direction.UNDEFINED);
	}
	
	@Test
	public void testRotCw() {
		assertThat(Direction.DOWN.rotCw()).isEqualTo(Direction.LEFT);
		assertThat(Direction.LEFT.rotCw()).isEqualTo(Direction.UP);
		assertThat(Direction.UP.rotCw()).isEqualTo(Direction.RIGHT);
		assertThat(Direction.RIGHT.rotCw()).isEqualTo(Direction.DOWN);
		assertThat(Direction.UNDEFINED.rotCw()).isEqualTo(Direction.UNDEFINED);
	}
	
	@Test
	public void testAngle() {
		assertThat(Direction.DOWN.angle(Direction.DOWN)).isEqualTo(0, Offset.offset(0.01f));
		assertThat(Direction.DOWN.angle(Direction.UP)).isEqualTo((float) Math.PI, Offset.offset(0.01f));
		assertThat(Direction.DOWN.angle(Direction.RIGHT)).isEqualTo((float) (Math.PI / 2), Offset.offset(0.01f));
		assertThat(Direction.DOWN.angle(Direction.LEFT)).isEqualTo(-(float) (Math.PI / 2), Offset.offset(0.01f));
		assertThat(Direction.LEFT.angle(Direction.UP)).isEqualTo(-(float) (Math.PI / 2), Offset.offset(0.01f));
		assertThat(Direction.RIGHT.angle(Direction.UP)).isEqualTo((float) (Math.PI / 2), Offset.offset(0.01f));
	}
	
	@Test
	public void testAngleff() {
		assertThat(Direction.DOWN.angle(0, -1)).isEqualTo(0, Offset.offset(0.01f));
		assertThat(Direction.DOWN.angle(0, 1)).isEqualTo((float) Math.PI, Offset.offset(0.01f));
		assertThat(Direction.DOWN.angle(1, 0)).isEqualTo((float) (Math.PI / 2), Offset.offset(0.01f));
		assertThat(Direction.DOWN.angle(-1, 0)).isEqualTo(-(float) (Math.PI / 2), Offset.offset(0.01f));
		assertThat(Direction.LEFT.angle(0, 1)).isEqualTo(-(float) (Math.PI / 2), Offset.offset(0.01f));
		assertThat(Direction.RIGHT.angle(0, 1)).isEqualTo((float) (Math.PI / 2), Offset.offset(0.01f));
	}
}
