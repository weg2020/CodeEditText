package com.weg.android.editor;

import static androidx.annotation.VisibleForTesting.PACKAGE_PRIVATE;

import android.view.inputmethod.BaseInputConnection;

import androidx.annotation.VisibleForTesting;

@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
class TextInputConnection extends BaseInputConnection {
	private static final String TAG = "TextInputConnection";
	private final EditorView view;
	
	public TextInputConnection(EditorView view) {
		super(view, true);
		this.view = view;
	}
}
