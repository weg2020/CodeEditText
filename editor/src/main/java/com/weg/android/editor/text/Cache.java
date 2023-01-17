package com.weg.android.editor.text;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.abs;

class Cache {
	private static final int CACHE_SIZE = 12;
	private final int[][] lines = new int[CACHE_SIZE][2];
	
	public Cache() {
		lines[0] = new int[]{0, 0};
		for (int i = 1; i < CACHE_SIZE; i++) {
			lines[i] = new int[]{-1, -1};
		}
	}
	
	public int[] getNearestAtLine(int lineIndex) {
		int nearestMatch = 0;
		int nearestDistance = MAX_VALUE;
		for (int i = 0; i < CACHE_SIZE; i++) {
			int distance = abs(lineIndex - lines[i][0]);
			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearestMatch = i;
			}
		}
		
		int[] nearestEntry = lines[nearestMatch];
		moveTop(nearestMatch);
		return nearestEntry;
	}
	
	public int[] getNearestAtOffset(int charOffset) {
		int nearestMatch = 0;
		int nearestDistance = MAX_VALUE;
		for (int i = 0; i < CACHE_SIZE; i++) {
			int distance = abs(charOffset - lines[i][1]);
			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearestMatch = i;
			}
		}
		
		int[] nearestEntry = lines[nearestMatch];
		moveTop(nearestMatch);
		return nearestEntry;
	}
	
	private void moveTop(int index) {
		if (index == 0) return;
		
		int[] temp = lines[index];
		for (int i = index; i > 1; i--) {
			lines[i] = lines[i - 1];
		}
		lines[1] = temp;
	}
	
	public void updateEntry(int lineIndex, int charOffset) {
		if (lineIndex <= 0) return;
		
		if (!replaceEntry(lineIndex, charOffset))
			insertEntry(lineIndex, charOffset);
	}
	
	private boolean replaceEntry(int lineIndex, int charOffset) {
		for (int i = 1; i < CACHE_SIZE; i++) {
			if (lines[i][0] == lineIndex) {
				lines[i][1] = charOffset;
				return true;
			}
		}
		return false;
	}
	
	private void insertEntry(int lineIndex, int charOffset) {
		moveTop(CACHE_SIZE - 1);
		lines[1] = new int[]{lineIndex, charOffset};
	}
	
	
	public void invalidate(int startOffset) {
		for (int i = 1; i < CACHE_SIZE; i++) {
			if (lines[i][1] >= startOffset) {
				lines[i] = new int[]{-1, -1};
			}
		}
	}
}
