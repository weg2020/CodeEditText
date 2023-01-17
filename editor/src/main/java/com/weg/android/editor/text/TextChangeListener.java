package com.weg.android.editor.text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface TextChangeListener {
	void textSet();
	
	void textChanging(int start, int end, @Nullable CharSequence newText);
	
	void textInserted(int index, @NonNull CharSequence text);
	
	void textDeleted(int start, int end);
	
	void textReplaced(int start, int end, @NonNull CharSequence newText);
}
