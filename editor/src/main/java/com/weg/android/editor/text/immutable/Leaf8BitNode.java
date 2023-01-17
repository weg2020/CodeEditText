package com.weg.android.editor.text.immutable;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

class Leaf8BitNode extends LeafNode {
	
	private static final long serialVersionUID = 2763979021661909997L;
	private final byte[] data;
	
	Leaf8BitNode(byte[] data) {
		this.data = data;
	}
	
	
	public int length() {
		return data.length;
	}
	
	
	public void getChars(int start, int end, char[] dest, int destPos) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = start; i < end; i++) {
			dest[destPos++] = byteToChar(data[i]);
		}
	}
	
	@Override
	public void drawText(@NonNull Canvas c, int start, int end, float x, float y, @NonNull Paint p) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException();
		}
		
		char[] temp = TemporaryBuffer.obtain(end - start);
		getChars(start, end, temp, 0);
		c.drawText(temp, 0, end - start, x, y, p);
		TemporaryBuffer.recycle(temp);
	}
	
	@Override
	public int measureText(int start, int end, @NonNull Paint p) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException();
		}
		
		char[] temp = TemporaryBuffer.obtain(end - start);
		getChars(start, end, temp, 0);
		int r = (int) p.measureText(temp, 0, end - start);
		TemporaryBuffer.recycle(temp);
		return r;
	}
	
	@Override
	public int getTextWidths(int start, int end, float[] widths, @NonNull Paint p) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException();
		}
		
		char[] temp = TemporaryBuffer.obtain(end - start);
		getChars(start, end, temp, 0);
		int r = p.getTextWidths(temp, 0, end - start, widths);
		TemporaryBuffer.recycle(temp);
		return r;
	}
	
	
	public LeafNode subNode(int start, int end) {
		if (start == 0 && end == length()) {
			return this;
		}
		int length = end - start;
		byte[] chars = new byte[length];
		System.arraycopy(data, start, chars, 0, length);
		return new Leaf8BitNode(chars);
	}
	
	
	public char charAt(int index) {
		return byteToChar(data[index]);
	}
	
	private static char byteToChar(byte b) {
		return (char) (b & 0xff);
	}
}
