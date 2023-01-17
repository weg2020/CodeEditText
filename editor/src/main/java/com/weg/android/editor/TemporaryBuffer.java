package com.weg.android.editor;


import static androidx.annotation.VisibleForTesting.PACKAGE_PRIVATE;

import androidx.annotation.VisibleForTesting;

@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
 class TemporaryBuffer {

	public static char[] obtain(int len) {
		char[] buf;
		
		synchronized (TemporaryBuffer.class) {
			buf = sTemp;
			sTemp = null;
		}
		
		if (buf == null || buf.length < len) {
			buf = new char[len];
		}
		
		return buf;
	}
	
	public static void recycle(char[] temp) {
		if (temp.length > 1000) return;
		
		synchronized (TemporaryBuffer.class) {
			sTemp = temp;
		}
	}
	
	private static char[] sTemp = null;
}

