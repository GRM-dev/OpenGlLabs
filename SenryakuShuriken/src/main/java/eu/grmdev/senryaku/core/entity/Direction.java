package eu.grmdev.senryaku.core.entity;

public enum Direction {
	UP ,
	LEFT ,
	DOWN ,
	RIGHT ,
	UNDEFINED;
	
	public static Direction getDir(float x, float y) {
		if (x == -1 && y == 0) { return LEFT; }
		if (x == 1 && y == 0) { return RIGHT; }
		if (x == 0 && y == -1) { return DOWN; }
		if (x == 0 && y == 1) { return UP; }
		return Direction.UNDEFINED;
	}
	
	public static Direction getDirInvY(float rx, float rz) {
		Direction dir = getDir(rx, rz);
		return (dir == UP || dir == DOWN) ? dir.invert() : dir;
	}
	
	public Direction rotCcw() {
		if (this == UNDEFINED) { return Direction.UNDEFINED; }
		if (ordinal() < values().length - 2) { return values()[ordinal() + 1]; }
		return values()[0];
	}
	
	public Direction rotCw() {
		if (this == UNDEFINED) { return Direction.UNDEFINED; }
		if (ordinal() == 0) { return values()[values().length - 2]; }
		return values()[ordinal() - 1];
	}
	
	public Direction invert() {
		switch (this) {
			case DOWN :
				return UP;
			case LEFT :
				return RIGHT;
			case RIGHT :
				return LEFT;
			case UNDEFINED :
				return UNDEFINED;
			case UP :
				return DOWN;
		}
		return UNDEFINED;
	}
	
	public float angle(Direction direction) {
		if (this == direction) { return 0; }
		if (direction.invert() == this) { return (float) Math.PI; }
		float angle = 0;
		do {
			angle += Math.PI / 2;
			direction = direction.rotCcw();
		}
		while (direction != this);
		while (angle - 2 * Math.PI > 0.01f) {
			angle -= 2 * Math.PI;
		}
		if (Math.abs(angle - 3 * Math.PI / 2) < 0.01f) {
			angle = (float) (-Math.PI / 2);
		}
		return -angle;
	}
	
	public float angle(float rx, float rz) {
		Direction dir = getDir(rx, rz);
		return angle(dir);
	}
}
