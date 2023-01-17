package com.weg.android.editor.syntax;

import static java.lang.Integer.compare;
import static java.lang.Math.max;
import static java.util.Collections.sort;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.weg.android.editor.TokenType;
import com.weg.android.editor.util.GapList;

import java.util.Arrays;

public class Highlighting implements Parcelable {
	
	public static class Builder {
		private final GapList<Highlight> lists;
		
		public Builder(int initCapacity) {
			lists = new GapList<>(max(32, initCapacity));
		}
		
		public void highlight(@NonNull TokenType type, int start, int end) {
			lists.add(new Highlight(type, start, end));
		}
		
		@NonNull
		 public Highlighting build() {
			sort(lists);
			Highlight[] highlights = lists.toArray(new Highlight[0]);
			return new Highlighting(highlights);
		}
	}
	
	@NonNull
	public Highlight[] highlights;
	
	private Highlighting(@NonNull Highlight... highlights) {
		this.highlights = highlights;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Highlighting that = (Highlighting) o;
		
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(highlights, that.highlights);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(highlights);
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedArray(this.highlights, flags);
	}
	
	public void readFromParcel(Parcel source) {
		this.highlights = source.createTypedArray(Highlight.CREATOR);
	}
	
	protected Highlighting(Parcel in) {
		this.highlights = in.createTypedArray(Highlight.CREATOR);
	}
	
	public static final Parcelable.Creator<Highlighting> CREATOR = new Parcelable.Creator<Highlighting>() {
		@Override
		public Highlighting createFromParcel(Parcel source) {
			return new Highlighting(source);
		}
		
		@Override
		public Highlighting[] newArray(int size) {
			return new Highlighting[size];
		}
	};
	
	public static class Highlight implements Comparable<Highlight>, Parcelable {
		@NonNull
		public TokenType type;
		public int startIndex;
		public int stopIndex;
		
		public Highlight(@NonNull TokenType type, int startIndex, int stopIndex) {
			this.type = type;
			this.startIndex = startIndex;
			this.stopIndex = stopIndex;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			
			Highlight highlight = (Highlight) o;
			
			if (startIndex != highlight.startIndex) return false;
			if (stopIndex != highlight.stopIndex) return false;
			return type.equals(highlight.type);
		}
		
		@Override
		public int hashCode() {
			int result = type.hashCode();
			result = 31 * result + startIndex;
			result = 31 * result + stopIndex;
			return result;
		}
		
		@Override
		public int compareTo(Highlight o) {
			if (startIndex == o.startIndex)
				return compare(stopIndex, o.stopIndex);
			return compare(startIndex, o.startIndex);
		}
		
		@NonNull
		@Override
		public String toString() {
			return "Highlight{" +
					"type=" + type.getName() +
					", startIndex=" + startIndex +
					", stopIndex=" + stopIndex +
					'}';
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeParcelable(this.type, flags);
			dest.writeInt(this.startIndex);
			dest.writeInt(this.stopIndex);
		}
		
		public void readFromParcel(Parcel source) {
			this.type = source.readParcelable(TokenType.class.getClassLoader());
			this.startIndex = source.readInt();
			this.stopIndex = source.readInt();
		}
		
		protected Highlight(Parcel in) {
			this.type = in.readParcelable(TokenType.class.getClassLoader());
			this.startIndex = in.readInt();
			this.stopIndex = in.readInt();
		}
		
		public static final Creator<Highlight> CREATOR = new Creator<Highlight>() {
			@Override
			public Highlight createFromParcel(Parcel source) {
				return new Highlight(source);
			}
			
			@Override
			public Highlight[] newArray(int size) {
				return new Highlight[size];
			}
		};
	}
}
