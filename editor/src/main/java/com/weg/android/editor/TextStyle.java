package com.weg.android.editor;

import android.annotation.IntDef;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TextStyle implements Parcelable {
	@IntDef(value = {NORMAL, BOLD, ITALIC, BOLD_ITALIC})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Style {}
	public static final int NORMAL = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;
	public static final int BOLD_ITALIC = 3;
	
	@ColorInt
	public int foregroundColor;
	@ColorInt
	public int backgroundColor;
	@Style
	public int fontStyle;
	
	public TextStyle(@ColorInt int foregroundColor){
		this(foregroundColor,0);
	}
	public TextStyle(@ColorInt int foregroundColor,@ColorInt int backgroundColor){
		this(foregroundColor,backgroundColor,NORMAL);
	}
	public TextStyle(@ColorInt int foregroundColor,@ColorInt int backgroundColor,@Style int fontStyle) {
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
		this.fontStyle = fontStyle;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		TextStyle textStyle = (TextStyle) o;
		
		if (foregroundColor != textStyle.foregroundColor) return false;
		if (backgroundColor != textStyle.backgroundColor) return false;
		return fontStyle == textStyle.fontStyle;
	}
	
	@Override
	public int hashCode() {
		int result = foregroundColor;
		result = 31 * result + backgroundColor;
		result = 31 * result + fontStyle;
		return result;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.foregroundColor);
		dest.writeInt(this.backgroundColor);
		dest.writeInt(this.fontStyle);
	}
	
	public void readFromParcel(Parcel source) {
		this.foregroundColor = source.readInt();
		this.backgroundColor = source.readInt();
		this.fontStyle = source.readInt();
	}
	
	protected TextStyle(Parcel in) {
		this.foregroundColor = in.readInt();
		this.backgroundColor = in.readInt();
		this.fontStyle = in.readInt();
	}
	
	public static final Parcelable.Creator<TextStyle> CREATOR = new Parcelable.Creator<TextStyle>() {
		@Override
		public TextStyle createFromParcel(Parcel source) {
			return new TextStyle(source);
		}
		
		@Override
		public TextStyle[] newArray(int size) {
			return new TextStyle[size];
		}
	};
}
