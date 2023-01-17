package com.weg.android.editor.scheme;

import static com.weg.android.editor.TextStyle.BOLD;

import com.weg.android.editor.ColorScheme;
import com.weg.android.editor.TextStyle;

public class ColorSchemeDark extends ColorScheme {
	public ColorSchemeDark() {
		super("Dark", true);
	}
	
	@Override
	protected void registerDefaults() {
		setBackgroundColor(0xFF292A2F);
		setForegroundColor(0xFFFFFFFF);
		setComposingTextColor(0xFF78ACEF);
		setCaretColor(0xFFFFFFFF);
		setCaretLineColor(0xFF23252B);
		setCaretLineNumberColor(0xFF03A9F4);
		setLineNumberColor(0xFF82888F);
		setSelectionColor(0xFF515B70);
		setSelectionHandleColor(0xFF03A9F4);
		setSearchResultColor(0xFF32593D);
		setHyperlinkColor(0xFF5482FF);
		setWhitespaceColor(0xFF606060);
		setTodoColor(0xFF92A1B1);
		setErrorColor(0xFF9E2927);
		setWarningColor(0xFFBE9117);
		setDeprecatedColor(0xFFC3C3C3);
		setBreakpointColor(0xFF700000);
		setBreakpointLineColor(0xFF700000);
		putStyle(PLAIN, new TextStyle(0xFFFFFFFF));
		putStyle(OPERATOR);//PLAIN
		putStyle(SEPARATOR);//PLAIN
		putStyle(METADATA, new TextStyle(0xFFD0A8FF));
		putStyle(KEYWORD, new TextStyle(0xFFFC5FA3, 0, BOLD));
		putStyle(IDENTIFIER, new TextStyle(0xFF67B7A4));
		putStyle(NUMBER, new TextStyle(0xFFD0BF69));
		putStyle(STRING, new TextStyle(0xFFFC6A5D));
		putStyle(COMMENT, new TextStyle(0xFF6C7986));
	}
}
