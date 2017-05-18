package eu.grmdev.senryaku;

import java.util.*;

class HelloWorld {
	static Scanner scaner = new Scanner(System.in);
	
	public static void main(String[] args) throws java.lang.Exception {
		int n = scaner.nextInt();
		for (int i = 0; i < n; i++) {
			int r;
			if (scaner.hasNextInt()) {
				int a = scaner.nextInt();
				if (scaner.hasNextInt()) {
					int b = scaner.nextInt();
					r = lastDigit(a, b);
				} else {
					long b = scaner.nextLong();
					r = lastDigit(a, b);
				}
			} else {
				long a = scaner.nextLong();
				long b = scaner.nextLong();
				r = lastDigit(a, b);
			}
			System.out.println(r);
		}
	}
	
	public static int lastDigit(int a, int b) {
		if (b == 0) { return 1; }
		String aS = a + "";
		int aLastDigit = Integer.parseInt(aS.substring(aS.length() - 1));
		int lastDigit = aLastDigit;
		List<Integer> r = new ArrayList<>();
		do {
			r.add(lastDigit);
			String nNumber = lastDigit * aLastDigit + "";
			lastDigit = Integer.parseInt(nNumber.substring(nNumber.length() - 1));
		}
		while (lastDigit != aLastDigit);
		return r.get((b - 1) % r.size());
	}
	
	public static int lastDigit(long a, long b) {
		if (b == 0) { return 1; }
		String aS = a + "";
		int aLastDigit = Integer.parseInt(aS.substring(aS.length() - 1));
		int lastDigit = aLastDigit;
		List<Integer> r = new ArrayList<>();
		do {
			r.add(lastDigit);
			String nNumber = lastDigit * aLastDigit + "";
			lastDigit = Integer.parseInt(nNumber.substring(nNumber.length() - 1));
		}
		while (lastDigit != aLastDigit);
		return r.get((int) ((b - 1) % r.size()));
	}
}