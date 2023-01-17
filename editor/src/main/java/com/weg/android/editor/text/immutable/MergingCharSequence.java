package com.weg.android.editor.text.immutable;

import androidx.annotation.NonNull;

import java.io.Serializable;

class MergingCharSequence implements CharSequence, Serializable {
	
	private static final long serialVersionUID = 1768010987012204929L;
	private final CharSequence s1;
	private final CharSequence s2;
	
	public MergingCharSequence(CharSequence s1, CharSequence s2) {
		this.s1 = s1;
		this.s2 = s2;
	}
	
	public int length() {
		return s1.length() + s2.length();
	}
	
	public char charAt(int index) {
		if (index < s1.length()) return s1.charAt(index);
		return s2.charAt(index - s1.length());
	}
	
	@NonNull
	public CharSequence subSequence(int start, int end) {
		if (start == 0 && end == length()) return this;
		if (start < s1.length() && end < s1.length()) return s1.subSequence(start, end);
		if (start >= s1.length() && end >= s1.length())
			return s2.subSequence(start - s1.length(), end - s1.length());
		return new MergingCharSequence(s1.subSequence(start, s1.length()), s2.subSequence(0, end - s1.length()));
	}
	
	@NonNull
	public String toString() {
		return s1 + s2.toString();
	}
}
