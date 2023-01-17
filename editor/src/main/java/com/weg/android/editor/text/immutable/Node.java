package com.weg.android.editor.text.immutable;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import java.io.Serializable;

abstract class Node implements CharSequence, Serializable {
	abstract public void getChars(int start, int end, char[] dest, int destPos);
	abstract public void drawText(@NonNull Canvas c, int start, int end, float x, float y, @NonNull Paint p);
	
	abstract public int measureText(int start, int end, @NonNull Paint p);
	
	abstract public int getTextWidths(int start, int end, float[] widths, @NonNull Paint p);
	
	abstract public Node subNode(int start, int end);
	
	@NonNull
	public String toString() {
		int len = length();
		char[] data = new char[len];
		getChars(0, len, data, 0);
		return new String(data);
	}
	
	@NonNull
	public CharSequence subSequence(int start, int end) {
		return subNode(start, end);
	}
}
