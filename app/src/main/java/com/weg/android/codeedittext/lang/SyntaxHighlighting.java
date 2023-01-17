package com.weg.android.codeedittext.lang;

import com.weg.android.editor.EditorView;
import com.weg.android.editor.syntax.Highlighting;

public abstract class SyntaxHighlighting {
	public abstract void highlighting(OpenFileModel model, Highlighting.Builder builder);
}
