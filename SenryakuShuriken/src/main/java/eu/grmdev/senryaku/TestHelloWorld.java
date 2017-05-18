package eu.grmdev.senryaku;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TestHelloWorld {
	
	@Test
	public void testLastDigit() {
		ld(0, 2, 0);
		ld(525, 0, 1);
		ld(1, 200, 1);
		ld(5, 10000, 5);
		ld(6, 600, 6);
		ld(9, 9, 9);
		ld(9, 600, 1);
		ld(1000000000l, 1000000000l, 0);
		ld(17, 19, 3);
	}
	
	private void ld(long a, long b, int r) {
		assertThat(HelloWorld.lastDigit(a, b)).isEqualTo(r);
	}
	
	private void ld(int a, int b, int r) {
		assertThat(HelloWorld.lastDigit(a, b)).isEqualTo(r);
	}
}
