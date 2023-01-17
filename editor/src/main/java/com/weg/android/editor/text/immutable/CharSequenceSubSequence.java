package com.weg.android.editor.text.immutable;

import androidx.annotation.NonNull;

import java.io.Serializable;

class CharSequenceSubSequence implements CharSequence, Serializable {
	
	private static final long serialVersionUID = 9016956955790207930L;
	private final CharSequence myChars;
	private final int myStart;
	private final int myEnd;
	
	public CharSequenceSubSequence(CharSequence chars) {
		this(chars, 0, chars.length());
	}
	
	public CharSequenceSubSequence(CharSequence chars, int start, int end) {
		if (start < 0 || end > chars.length() || start > end) {
			throw new IndexOutOfBoundsException("chars sequence.length:" + chars.length() +
					", start:" + start +
					", end:" + end);
		}
		myChars = chars;
		myStart = start;
		myEnd = end;
	}
	
	public final int length() {
		return myEnd - myStart;
	}
	
	public final char charAt(int index) {
		return myChars.charAt(index + myStart);
	}
	
	@NonNull
	public CharSequence subSequence(int start, int end) {
		if (start == myStart && end == myEnd) return this;
		return new CharSequenceSubSequence(myChars, myStart + start, myStart + end);
	}
	
	@NonNull
	public String toString() {
		if (myChars instanceof String) return ((String) myChars).substring(myStart, myEnd);
		return new String(fromSequence(myChars, myStart, myEnd));
	}
	
	@NonNull
	private char[] fromSequence(CharSequence seq, int start, int end) {
		char[] result = new char[end - start];
		getChars(seq, result, start, end - start);
		return result;
	}
	
	private static final int GET_CHARS_THRESHOLD = 10;
	
	private void getChars(CharSequence src, char[] dst, int srcOffset, int len) {
		if (len >= GET_CHARS_THRESHOLD) {
			if (src instanceof String) {
				((String) src).getChars(srcOffset, srcOffset + len, dst, 0);
				return;
			} else if (src instanceof StringBuffer) {
				((StringBuffer) src).getChars(srcOffset, srcOffset + len, dst, 0);
				return;
			} else if (src instanceof StringBuilder) {
				((StringBuilder) src).getChars(srcOffset, srcOffset + len, dst, 0);
				return;
			}
		}
		
		for (int i = 0, j = srcOffset, max = srcOffset + len; j < max && i < dst.length; i++, j++) {
			dst[i] = src.charAt(j);
		}
	}
	
	private CharSequence getBaseSequence() {
		return myChars;
	}
	
	public void getChars(int start, int end, char[] dest, int destPos) {
		ImmutableText.getChars(myChars, dest, start + myStart, destPos, end - start);
	}
	
	private transient int hash;
	
	public int hashCode() {
		int h = hash;
		if (h == 0) {
			hash = h = ImmutableText.stringHashCode(this, length());
		}
		return h;
	}
}
