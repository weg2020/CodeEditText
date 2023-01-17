package com.weg.android.editor;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ColorScheme implements Parcelable, TokenTypes {
	private String name;
	private boolean dark;
	private Map<TokenType, TextStyle> styles;
	private int backgroundColor;
	private int foregroundColor;
	private int composingTextColor;
	private int caretColor;
	private int caretLineColor;
	private int caretLineNumberColor;
	private int lineNumberColor;
	private int selectionColor;
	private int selectionHandleColor;
	private int searchResultColor;
	private int hyperlinkColor;
	private int whitespaceColor;
	private int todoColor;
	private int errorColor;
	private int warningColor;
	private int deprecatedColor;
	private int breakpointColor;
	private int breakpointLineColor;
	
	public ColorScheme(@NonNull String name, boolean dark) {
		this.name = name;
		this.dark = dark;
		styles = new HashMap<>();
		registerDefaults();
	}
	
	protected void registerDefaults() {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	public final void putStyle(@NonNull TokenType type) {
		TextStyle style = getStyle(type);
		if (style != null) {
			putStyle(type, style);
		}
	}
	
	public final void putStyle(@NonNull TokenType type, @NonNull TextStyle textStyle) {
		styles.put(type, textStyle);
	}
	
	@Nullable
	public final TextStyle getStyle(@NonNull TokenType type) {
		TextStyle style = styles.get(type);
		if (style == null) {
			if (type.hasParent()) {
				assert type.getParent() != null;
				return getStyle(type.getParent());
			}
		}
		return style;
	}
	
	public final boolean containsStyle(@NonNull TokenType type) {
		if (styles.containsKey(type))
			return true;
		if (type.hasParent()) {
			assert type.getParent() != null;
			return containsStyle(type.getParent());
		}
		return false;
	}
	
	public final void resetDefaults() {
		styles.clear();
		registerDefaults();
	}
	
	@NonNull
	public final String getName() {
		return name;
	}
	
	public final boolean isDark() {
		return dark;
	}
	
	public final int getBackgroundColor() {
		return backgroundColor;
	}
	
	public final void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public final int getForegroundColor() {
		return foregroundColor;
	}
	
	public final void setForegroundColor(int foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	public final int getComposingTextColor() {
		return composingTextColor;
	}
	
	public final void setComposingTextColor(int composingTextColor) {
		this.composingTextColor = composingTextColor;
	}
	
	public final int getCaretColor() {
		return caretColor;
	}
	
	public final void setCaretColor(int caretColor) {
		this.caretColor = caretColor;
	}
	
	public final int getCaretLineColor() {
		return caretLineColor;
	}
	
	public final void setCaretLineColor(int caretLineColor) {
		this.caretLineColor = caretLineColor;
	}
	
	public final int getCaretLineNumberColor() {
		return caretLineNumberColor;
	}
	
	public final void setCaretLineNumberColor(int caretLineNumberColor) {
		this.caretLineNumberColor = caretLineNumberColor;
	}
	
	public final int getLineNumberColor() {
		return lineNumberColor;
	}
	
	public final void setLineNumberColor(int lineNumberColor) {
		this.lineNumberColor = lineNumberColor;
	}
	
	public final int getSelectionColor() {
		return selectionColor;
	}
	
	public final void setSelectionColor(int selectionColor) {
		this.selectionColor = selectionColor;
	}
	
	public final int getSelectionHandleColor() {
		return selectionHandleColor;
	}
	
	public final void setSelectionHandleColor(int selectionHandleColor) {
		this.selectionHandleColor = selectionHandleColor;
	}
	
	public final int getSearchResultColor() {
		return searchResultColor;
	}
	
	public final void setSearchResultColor(int searchResultColor) {
		this.searchResultColor = searchResultColor;
	}
	
	public final int getHyperlinkColor() {
		return hyperlinkColor;
	}
	
	public final void setHyperlinkColor(int hyperlinkColor) {
		this.hyperlinkColor = hyperlinkColor;
	}
	
	public final int getWhitespaceColor() {
		return whitespaceColor;
	}
	
	public final void setWhitespaceColor(int whitespaceColor) {
		this.whitespaceColor = whitespaceColor;
	}
	
	public final int getTodoColor() {
		return todoColor;
	}
	
	public final void setTodoColor(int todoColor) {
		this.todoColor = todoColor;
	}
	
	public final int getErrorColor() {
		return errorColor;
	}
	
	public final void setErrorColor(int errorColor) {
		this.errorColor = errorColor;
	}
	
	public final int getWarningColor() {
		return warningColor;
	}
	
	public final void setWarningColor(int warningColor) {
		this.warningColor = warningColor;
	}
	
	public final int getDeprecatedColor() {
		return deprecatedColor;
	}
	
	public final void setDeprecatedColor(int deprecatedColor) {
		this.deprecatedColor = deprecatedColor;
	}
	
	public final int getBreakpointColor() {
		return breakpointColor;
	}
	
	public final void setBreakpointColor(int breakpointColor) {
		this.breakpointColor = breakpointColor;
	}
	
	public final int getBreakpointLineColor() {
		return breakpointLineColor;
	}
	
	public final void setBreakpointLineColor(int breakpointLineColor) {
		this.breakpointLineColor = breakpointLineColor;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		ColorScheme that = (ColorScheme) o;
		
		if (dark != that.dark) return false;
		if (backgroundColor != that.backgroundColor) return false;
		if (foregroundColor != that.foregroundColor) return false;
		if (composingTextColor != that.composingTextColor) return false;
		if (caretColor != that.caretColor) return false;
		if (caretLineColor != that.caretLineColor) return false;
		if (caretLineNumberColor != that.caretLineNumberColor) return false;
		if (lineNumberColor != that.lineNumberColor) return false;
		if (selectionColor != that.selectionColor) return false;
		if (selectionHandleColor != that.selectionHandleColor) return false;
		if (searchResultColor != that.searchResultColor) return false;
		if (hyperlinkColor != that.hyperlinkColor) return false;
		if (whitespaceColor != that.whitespaceColor) return false;
		if (todoColor != that.todoColor) return false;
		if (errorColor != that.errorColor) return false;
		if (warningColor != that.warningColor) return false;
		if (deprecatedColor != that.deprecatedColor) return false;
		if (breakpointColor != that.breakpointColor) return false;
		if (breakpointLineColor != that.breakpointLineColor) return false;
		if (!name.equals(that.name)) return false;
		return styles.equals(that.styles);
	}
	
	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (dark ? 1 : 0);
		result = 31 * result + styles.hashCode();
		result = 31 * result + backgroundColor;
		result = 31 * result + foregroundColor;
		result = 31 * result + composingTextColor;
		result = 31 * result + caretColor;
		result = 31 * result + caretLineColor;
		result = 31 * result + caretLineNumberColor;
		result = 31 * result + lineNumberColor;
		result = 31 * result + selectionColor;
		result = 31 * result + selectionHandleColor;
		result = 31 * result + searchResultColor;
		result = 31 * result + hyperlinkColor;
		result = 31 * result + whitespaceColor;
		result = 31 * result + todoColor;
		result = 31 * result + errorColor;
		result = 31 * result + warningColor;
		result = 31 * result + deprecatedColor;
		result = 31 * result + breakpointColor;
		result = 31 * result + breakpointLineColor;
		return result;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeByte(this.dark ? (byte) 1 : (byte) 0);
		dest.writeInt(this.styles.size());
		for (Map.Entry<TokenType, TextStyle> entry : this.styles.entrySet()) {
			dest.writeParcelable(entry.getKey(), flags);
			dest.writeParcelable(entry.getValue(), flags);
		}
		dest.writeInt(this.backgroundColor);
		dest.writeInt(this.foregroundColor);
		dest.writeInt(this.composingTextColor);
		dest.writeInt(this.caretColor);
		dest.writeInt(this.caretLineColor);
		dest.writeInt(this.caretLineNumberColor);
		dest.writeInt(this.lineNumberColor);
		dest.writeInt(this.selectionColor);
		dest.writeInt(this.selectionHandleColor);
		dest.writeInt(this.searchResultColor);
		dest.writeInt(this.hyperlinkColor);
		dest.writeInt(this.whitespaceColor);
		dest.writeInt(this.todoColor);
		dest.writeInt(this.errorColor);
		dest.writeInt(this.warningColor);
		dest.writeInt(this.deprecatedColor);
		dest.writeInt(this.breakpointColor);
		dest.writeInt(this.breakpointLineColor);
	}
	
	public void readFromParcel(Parcel source) {
		this.name = source.readString();
		this.dark = source.readByte() != 0;
		int stylesSize = source.readInt();
		this.styles = new HashMap<>(stylesSize);
		for (int i = 0; i < stylesSize; i++) {
			TokenType key = source.readParcelable(TokenType.class.getClassLoader());
			TextStyle value = source.readParcelable(TextStyle.class.getClassLoader());
			this.styles.put(key, value);
		}
		this.backgroundColor = source.readInt();
		this.foregroundColor = source.readInt();
		this.composingTextColor = source.readInt();
		this.caretColor = source.readInt();
		this.caretLineColor = source.readInt();
		this.caretLineNumberColor = source.readInt();
		this.lineNumberColor = source.readInt();
		this.selectionColor = source.readInt();
		this.selectionHandleColor = source.readInt();
		this.searchResultColor = source.readInt();
		this.hyperlinkColor = source.readInt();
		this.whitespaceColor = source.readInt();
		this.todoColor = source.readInt();
		this.errorColor = source.readInt();
		this.warningColor = source.readInt();
		this.deprecatedColor = source.readInt();
		this.breakpointColor = source.readInt();
		this.breakpointLineColor = source.readInt();
	}
	
	protected ColorScheme(Parcel in) {
		this.name = in.readString();
		this.dark = in.readByte() != 0;
		int stylesSize = in.readInt();
		this.styles = new HashMap<>(stylesSize);
		for (int i = 0; i < stylesSize; i++) {
			TokenType key = in.readParcelable(TokenType.class.getClassLoader());
			TextStyle value = in.readParcelable(TextStyle.class.getClassLoader());
			this.styles.put(key, value);
		}
		this.backgroundColor = in.readInt();
		this.foregroundColor = in.readInt();
		this.composingTextColor = in.readInt();
		this.caretColor = in.readInt();
		this.caretLineColor = in.readInt();
		this.caretLineNumberColor = in.readInt();
		this.lineNumberColor = in.readInt();
		this.selectionColor = in.readInt();
		this.selectionHandleColor = in.readInt();
		this.searchResultColor = in.readInt();
		this.hyperlinkColor = in.readInt();
		this.whitespaceColor = in.readInt();
		this.todoColor = in.readInt();
		this.errorColor = in.readInt();
		this.warningColor = in.readInt();
		this.deprecatedColor = in.readInt();
		this.breakpointColor = in.readInt();
		this.breakpointLineColor = in.readInt();
	}
	
	public static final Creator<ColorScheme> CREATOR = new Creator<ColorScheme>() {
		@Override
		public ColorScheme createFromParcel(Parcel source) {
			return new ColorScheme(source);
		}
		
		@Override
		public ColorScheme[] newArray(int size) {
			return new ColorScheme[size];
		}
	};
}
