package com.weg.android.codeedittext.lang.typescript;

import androidx.annotation.NonNull;

import com.weg.android.codeedittext.lang.FileSupport;
import com.weg.android.codeedittext.lang.SyntaxHighlighting;

import java.util.ArrayList;
import java.util.List;

public class TypeScriptFileSupport extends FileSupport {
	private final List<SyntaxHighlighting> highlighting = new ArrayList<>();
	
	public TypeScriptFileSupport() {
		highlighting.add(new TypeScriptSyntaxHighlighting());
	}
	
	@NonNull
	@Override
	public String getName() {
		return "TypeScript";
	}
	
	@NonNull
	@Override
	public String[] getFileExtensions() {
		return new String[]{".ts"};
	}
	
	@NonNull
	@Override
	public List<SyntaxHighlighting> getHighlightingList() {
		return highlighting;
	}
}
