package com.weg.android.codeedittext.lang;

import androidx.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

public interface OpenFileModel {
	
	@NonNull
	Reader getReader() throws IOException;
	
	@NonNull
	String getPath();
	@NonNull
	String getName();
	
	@NonNull
	default String getExtension() {
		String name = getName();
		int lastDotIndex = name.lastIndexOf('.');
		return (lastDotIndex < 0) ? "" : name.substring(lastDotIndex).toLowerCase();
	}
	
}
