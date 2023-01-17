package com.weg.android.codeedittext.lang;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class OpenFileModels {
	
	public static OpenFileModel of(String name, String text) {
		return new OpenFileModel() {
			@NonNull
			@Override
			public Reader getReader() throws IOException {
				return new StringReader(text);
			}
			
			@NonNull
			@Override
			public String getPath() {
				return "";
			}
			
			@NonNull
			@Override
			public String getName() {
				return name;
			}
		};
	}
	
	public static OpenFileModel of(String name, String path, Reader reader) {
		return new OpenFileModel() {
			@NonNull
			@Override
			public Reader getReader() throws IOException {
				return reader;
			}
			
			@NonNull
			@Override
			public String getPath() {
				return path;
			}
			
			@NonNull
			@Override
			public String getName() {
				return name;
			}
		};
	}
	
	public static OpenFileModel of(File file) {
		return new OpenFileModel() {
			@NonNull
			@Override
			public Reader getReader() throws IOException {
				return new FileReader(file);
			}
			
			@NonNull
			@Override
			public String getPath() {
				return file.getAbsolutePath();
			}
			
			@NonNull
			@Override
			public String getName() {
				return file.getName();
			}
		};
	}
	
}
