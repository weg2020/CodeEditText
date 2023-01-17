package com.weg.android.editor;

import androidx.annotation.NonNull;

import com.weg.android.editor.syntax.Highlighting;
import com.weg.android.editor.text.TextModel;
import com.weg.android.editor.text.immutable.ImmutableText;

public class EditorModel extends TextModel {
	
	public EditorModel() {
		super();
		init();
	}
	
	public EditorModel(@NonNull CharSequence text) {
		super(text);
		init();
	}
	
	public EditorModel(@NonNull ImmutableText textStore) {
		super(textStore);
		init();
	}
	
	private Highlighting highlighting;
	private void init(){
	
	}
	
	
	public void setHighlighting(Highlighting highlighting) {
		this.highlighting = highlighting;
	}
	
	public Highlighting getHighlighting() {
		return highlighting;
	}
}
