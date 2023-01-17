package com.weg.android.editor;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class TokenType implements Parcelable {
	private TokenType parent;
	private String name;
	
	public TokenType(String name) {
		this(null, name);
	}
	
	public TokenType(@Nullable TokenType parent, @NonNull String name) {
		if (parent == this) {
			parent = null;
		}
		this.parent = parent;
		this.name = name;
	}
	
	@Nullable
	public final TokenType getParent() {
		return parent;
	}
	
	public final boolean hasParent() {
		return parent != null;
	}
	
	public final String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		TokenType tokenType = (TokenType) o;
		
		if (!Objects.equals(parent, tokenType.parent))
			return false;
		return name.equals(tokenType.name);
	}
	
	@Override
	public int hashCode() {
		int result = parent != null ? parent.hashCode() : 0;
		result = 31 * result + name.hashCode();
		return result;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.parent, flags);
		dest.writeString(this.name);
	}
	
	public void readFromParcel(Parcel source) {
		this.parent = source.readParcelable(TokenType.class.getClassLoader());
		this.name = source.readString();
	}
	
	protected TokenType(Parcel in) {
		this.parent = in.readParcelable(TokenType.class.getClassLoader());
		this.name = in.readString();
	}
	
	public static final Parcelable.Creator<TokenType> CREATOR = new Parcelable.Creator<TokenType>() {
		@Override
		public TokenType createFromParcel(Parcel source) {
			return new TokenType(source);
		}
		
		@Override
		public TokenType[] newArray(int size) {
			return new TokenType[size];
		}
	};
}
