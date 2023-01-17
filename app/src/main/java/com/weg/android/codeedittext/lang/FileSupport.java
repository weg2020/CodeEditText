package com.weg.android.codeedittext.lang;

import androidx.annotation.NonNull;

import com.android.internal.org.bouncycastle.util.Arrays;

import java.util.List;
import java.util.Map;

public abstract class FileSupport {
	
	@NonNull
	public abstract String getName();
	
	@NonNull
	public abstract String[] getFileExtensions();
	
	public void fileOpened(OpenFileModel model) {
	
	}
	
	@NonNull
	public abstract List<SyntaxHighlighting> getHighlightingList();
}
