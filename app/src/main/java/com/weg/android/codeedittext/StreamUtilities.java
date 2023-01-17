package com.weg.android.codeedittext;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class StreamUtilities {
	@NonNull
	public static String readFully(InputStream stream) throws IOException {
		return readFully(new InputStreamReader(stream));
	}
	
	@NonNull
	public static String readFully(Reader reader) throws IOException {
		Reader bufferReader = new BufferedReader(reader);
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[8192];
		while (true) {
			int read = bufferReader.read(buffer, 0, buffer.length);
			if (read <= 0) {
				return builder.toString();
			}
			builder.append(buffer, 0, read);
		}
	}
}