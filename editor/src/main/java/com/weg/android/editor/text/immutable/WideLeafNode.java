package com.weg.android.editor.text.immutable;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

class WideLeafNode extends LeafNode {
	
	private static final long serialVersionUID = -2997940118120598777L;
	private final char[] data;
	
	WideLeafNode(char[] data) {
		this.data = data;
	}
	
	
	public int length() {
		return data.length;
	}
	
	
	public void getChars(int start, int end, char[] dest, int destPos) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException();
		}
		System.arraycopy(data, start, dest, destPos, end - start);
	}
	
	@Override
	public void drawText(@NonNull Canvas c, int start, int end, float x, float y, @NonNull Paint p) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException();
		}
		c.drawText(data, start, end - start, x, y, p);
	}
	
	@Override
	public int measureText(int start, int end, @NonNull Paint p) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException();
		}
		return (int) p.measureText(data, start, end - start);
	}
	
	@Override
	public int getTextWidths(int start, int end, float[] widths, @NonNull Paint p) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException();
		}
		return p.getTextWidths(data, start, end - start, widths);
	}
	
	
	public Node subNode(int start, int end) {
		if (start == 0 && end == length()) {
			return this;
		}
		return ImmutableText.createLeafNode(new CharArrayCharSequence(data, start, end));
	}
	
	
	@NonNull
	public String toString() {
		return new String(data);
	}
	
	
	public char charAt(int index) {
		return data[index];
	}
}
