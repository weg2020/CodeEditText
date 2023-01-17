package com.weg.android.editor.text.immutable;

import androidx.annotation.NonNull;

import java.io.Serializable;

class CharArrayCharSequence implements CharSequence, Serializable {
	
	private static final long serialVersionUID = -58658556577454275L;
	protected final char[] myChars;
	protected final int myStart;
	protected final int myEnd;
	
	public CharArrayCharSequence(char... chars) {
		this(chars, 0, chars.length);
	}
	
	public CharArrayCharSequence(char[] chars, int start, int end) {
		if (start < 0 || end > chars.length || start > end) {
			throw new IndexOutOfBoundsException("chars.length:" + chars.length + ", start:" + start + ", end:" + end);
		}
		myChars = chars;
		myStart = start;
		myEnd = end;
	}
	
	public final int length() {
		return myEnd - myStart;
	}
	
	public final char charAt(int index) {
		return myChars[index + myStart];
	}
	
	@NonNull
	public CharSequence subSequence(int start, int end) {
		return start == 0 && end == length() ? this : new CharArrayCharSequence(myChars, myStart + start, myStart + end);
	}
	
	
	@NonNull
	public String toString() {
		return new String(myChars, myStart, myEnd - myStart); //TODO StringFactory
	}
	
	public char[] getChars() {
		if (myStart == 0) return myChars;
		char[] chars = new char[length()];
		getChars(chars, 0);
		return chars;
	}
	
	public void getChars(char[] dst, int dstOffset) {
		System.arraycopy(myChars, myStart, dst, dstOffset, length());
	}
	
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject == null || getClass() != anObject.getClass() || length() != ((CharSequence) anObject).length()) {
			return false;
		}
		return ImmutableText.regionMatches(myChars, myStart, myEnd, (CharSequence) anObject);
	}
	
	/**
	 * See {@link java.io.Reader#read(char[], int, int)};
	 */
	public int readCharsTo(int start, char[] cbuf, int off, int len) {
		final int readChars = Math.min(len, length() - start);
		if (readChars <= 0) return -1;
		
		System.arraycopy(myChars, myStart + start, cbuf, off, readChars);
		return readChars;
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
