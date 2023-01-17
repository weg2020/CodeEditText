package com.weg.android.editor.scheme;

import static com.weg.android.editor.TextStyle.BOLD;

import com.weg.android.editor.ColorScheme;
import com.weg.android.editor.TextStyle;

public class ColorSchemeLight extends ColorScheme {
	
	public ColorSchemeLight() {
		super("Light", false);
	}
	
	@Override
	protected void registerDefaults() {
		setBackgroundColor(0xFFFFFFFF);
		setForegroundColor(0xFF000000);
		setComposingTextColor(0xFF11A1B3);
		setCaretColor(0xFF000000);
		setCaretLineColor(0xFFE8F2FF);
		setCaretLineNumberColor(0xFF869EF3);
		setLineNumberColor(0xFF999999);
		setSelectionColor(0xFFA4CDFF);
		setSelectionHandleColor(0xFF03A9F4);
		setSearchResultColor(0xFFFFFF00);
		setHyperlinkColor(0xFF0E0EFF);
		setWhitespaceColor(0xFFADADAD);
		setTodoColor(0xFF4A5560);
		setErrorColor(0xFFFF0000);
		setWarningColor(0xFFEBC700);
		setDeprecatedColor(0xFF404040);
		setBreakpointColor(0xFFFFC8C8);
		setBreakpointLineColor(0xFFFFC8C8);
		putStyle(PLAIN, new TextStyle(0xFF000000));
		putStyle(OPERATOR);//PLAIN
		putStyle(SEPARATOR);//PLAIN
		putStyle(METADATA, new TextStyle(0xFF3900A0));
		putStyle(KEYWORD, new TextStyle(0xFF9B2393, 0, BOLD));
		putStyle(IDENTIFIER, new TextStyle(0xFF326D74));
		putStyle(NUMBER, new TextStyle(0xFF1C00CF));
		putStyle(STRING, new TextStyle(0xFFC41A16));
		putStyle(COMMENT, new TextStyle(0xFF5D6C79));
	}
	
}
