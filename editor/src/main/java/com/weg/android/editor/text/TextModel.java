package com.weg.android.editor.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

import com.weg.android.editor.text.immutable.ImmutableText;

import java.util.ArrayList;
import java.util.List;

public class TextModel implements CharSequence, GetChars {
	private static final char LF = '\n';
	private static final char CR = '\r';
	private boolean stopForwardTextChanges;
	private List<TextChangeListener> listeners = new ArrayList<>();
	private ImmutableText textStore;
	private Cache cache;
	private int lineCount;
	
	public TextModel() {
		this("");
	}
	
	public TextModel(@NonNull CharSequence text) {
		this(ImmutableText.valueOf(text));
	}
	
	public TextModel(@NonNull ImmutableText textStore) {
		this.textStore = textStore;
		cache = new Cache();
		lineCount = lineCount(0, length()) + 1;
	}
	
	public void addTextChangeListener(@NonNull TextChangeListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeTextChangeListener(@NonNull TextChangeListener listener) {
		listeners.remove(listener);
	}
	
	public void setText(@NonNull CharSequence text) {
		textStore = ImmutableText.valueOf(text);
		lineCount = lineCount(0, length()) + 1;
		cache.invalidate(0);
		for (TextChangeListener listener : listeners) {
			listener.textSet();
		}
	}
	
	public void insert(int index, @NonNull CharSequence text) {
		checkRange("insert", index);
		if (text.length() == 0) return;
		
		if (!stopForwardTextChanges) {
			for (TextChangeListener listener : listeners) {
				listener.textChanging(index, index, text);
			}
		}
		
		textStore = textStore.insert(index, text);
		lineCount += lineCount(index, text.length());
		cache.invalidate(index);
		if (stopForwardTextChanges) return;
		for (TextChangeListener listener : listeners) {
			listener.textInserted(index, text);
		}
	}
	
	public void delete(int start, int end) {
		checkRange("delete", start, end);
		if (end <= start) return;
		if (!stopForwardTextChanges) {
			for (TextChangeListener listener : listeners) {
				listener.textChanging(start, end, null);
			}
		}
		
		int numberCount = lineCount(start, end - start);
		textStore = textStore.delete(start, end);
		lineCount -= numberCount;
		cache.invalidate(start);
		if (stopForwardTextChanges) return;
		for (TextChangeListener listener : listeners) {
			listener.textDeleted(start, end);
		}
	}
	
	
	public void replace(int start, int end, @NonNull CharSequence text) {
		checkRange("replace", start, end);
		stopForwardTextChanges = true;
		for (TextChangeListener listener : listeners) {
			listener.textChanging(start, end, text);
		}
		if (end > start)
			delete(start, end);
		if (text.length() != 0)
			insert(start, text);
		stopForwardTextChanges = false;
		for (TextChangeListener listener : listeners) {
			listener.textReplaced(start, end, text);
		}
	}
	
	@Override
	public int length() {
		return textStore.length();
	}
	
	@Override
	public char charAt(int index) {
		/*if (index < 0 && index >= length()) {
			return '\u0000';
		}*/
		checkRange("charAt", index);
		return textStore.charAt(index);
	}
	
	@NonNull
	public ImmutableText subText(int start, int end) {
		checkRange("subText", start, end);
		return textStore.subtext(start, end);
	}
	
	@NonNull
	@Override
	public CharSequence subSequence(int start, int end) {
		checkRange("subSequence", start, end);
		return textStore.subSequence(start, end);
	}
	
	@NonNull
	@Override
	public String toString() {
		return textStore.toString();
	}
	
	public int getLineCount() {
		return lineCount;
	}
	
	@NonNull
	public ImmutableText lineAt(int lineIndex) {
		if (lineIndex < 0 || lineIndex >= lineCount)
			throw new IndexOutOfBoundsException("invalid line index: " + lineIndex);
		int start = getOffsetAtLine(lineIndex);
		int length = getLineLength(lineIndex);
		return subText(start, start + length);
	}
	
	@NonNull
	public CharSequence getLine(int lineIndex) {
		if (lineIndex < 0 || lineIndex >= lineCount)
			throw new IndexOutOfBoundsException("invalid line index: " + lineIndex);
		int start = getOffsetAtLine(lineIndex);
		int length = getLineLength(lineIndex);
		return subSequence(start, start + length);
	}
	
	public int getOffsetAtLine(int lineIndex) {
		if (lineIndex < 0 || lineIndex >= lineCount)
			throw new IndexOutOfBoundsException("invalid line index: " + lineIndex);
		
		int[] cachedEntry = cache.getNearestAtLine(lineIndex);
		int cachedLine = cachedEntry[0];
		int cachedOffset = cachedEntry[1];
		
		int offset;
		if (lineIndex > cachedLine)
			offset = findCharOffset(lineIndex, cachedLine, cachedOffset);
		else if (lineIndex < cachedLine)
			offset = findCharOffsetBackward(lineIndex, cachedLine, cachedOffset);
		else
			offset = cachedOffset;
		
		if (offset >= 0)
			cache.updateEntry(lineIndex, offset);
		return offset;
	}
	
	private int findCharOffset(int targetLine, int startLine, int startOffset) {
		int workingLine = startLine;
		int offset = startOffset;
		
		while (offset < length() && workingLine < targetLine) {
			if (charAt(offset) == LF) {
				++workingLine;
			} else if (charAt(offset) == CR) {
				if (offset + 1 < length() && charAt(offset + 1) == LF)
					offset++;
				++workingLine;
			}
			++offset;
		}
		
		if (workingLine != targetLine)
			return -1;
		return offset;
	}
	
	private int findCharOffsetBackward(int targetLine, int startLine, int startOffset) {
		if (targetLine == 0) return 0;
		
		int workingLine = startLine;
		int offset = startOffset;
		while (offset >= 0 && workingLine > (targetLine - 1)) {
			--offset;
			
			if (charAt(offset) == LF) {
				if (offset - 1 >= 0 && charAt(offset - 1) == CR)
					--offset;
				--workingLine;
			} else if (charAt(offset) == CR)
				--workingLine;
		}
		
		int charOffset;
		if (offset >= 0) {
			charOffset = offset;
			++charOffset;
		} else
			charOffset = -1;
		return charOffset;
	}
	
	public int getLineAtOffset(int charOffset) {
		checkRange("getLineAtOffset", charOffset);
		int[] cachedEntry = cache.getNearestAtOffset(charOffset);
		int line = cachedEntry[0];
		int offset = cachedEntry[1];
		int lastKnownLine = -1;
		int lastKnownCharOffset = -1;
		
		if (charOffset > offset) {
			// search forward
			while (offset < length() && offset < charOffset) {
				if (charAt(offset) == LF) {
					++line;
					lastKnownLine = line;
					lastKnownCharOffset = offset + 1;
				} else if (charAt(offset) == CR) {
					if (offset + 1 < length() && charAt(offset + 1) == LF)
						offset++;
					++line;
					lastKnownLine = line;
					lastKnownCharOffset = offset + 1;
				}
				++offset;
			}
		} else if (charOffset < offset) {
			// search backward
			while (offset > charOffset) {
				--offset;
				if (charAt(offset) == LF) {
					if (offset - 1 >= 0 && charAt(offset - 1) == CR)
						offset--;
					lastKnownLine = line;
					lastKnownCharOffset = offset + 1;
					--line;
				} else if (charAt(offset) == CR) {
					lastKnownLine = line;
					lastKnownCharOffset = offset + 1;
					--line;
				}
			}
		}
		
		
		if (offset == charOffset) {
			if (lastKnownLine != -1)
				cache.updateEntry(lastKnownLine, lastKnownCharOffset);
			return line;
		} else {
			return -1;
		}
	}
	
	
	public int getLineLength(int lineIndex) {
		if (lineIndex < 0 || lineIndex >= lineCount)
			throw new IndexOutOfBoundsException("invalid line index: " + lineIndex);
		
		int length = 0;
		int offset = getOffsetAtLine(lineIndex);
		while (offset < length() && !isDelimiter(charAt(offset))) {
			++length;
			++offset;
		}
		return length;
	}
	
	private boolean isDelimiter(char ch) {
		if (ch == CR) return true;
		return ch == LF;
	}
	
	public void drawText(@NonNull Canvas c, int start, int end, float x, float y, @NonNull Paint p) {
		checkRange("drawText", start, end);
		textStore.drawText(c, start, end, x, y, p);
	}
	
	public int measureText(int start, int end, @NonNull Paint p) {
		checkRange("measureText", start, end);
		return textStore.measureText(start, end, p);
	}
	
	public int getTextWidths(int start, int end, float[] widths, @NonNull Paint p) {
		checkRange("getTextWidths", start, end);
		return textStore.getTextWidths(start, end, widths, p);
	}
	
	@Override
	public void getChars(int start, int end, char[] dest, int destoff) {
		checkRange("getChars", start, end);
		textStore.getChars(start, end, dest, destoff);
	}
	
	private int lineCount(int startOffset, int length) {
		if (length == 0) return 0;
		
		int lineCount = 0;
		int count = 0;
		int i = startOffset;
		
		while (count < length) {
			char ch = charAt(i);
			if (ch == CR) {
				if (i + 1 < length()) {
					ch = charAt(i + 1);
					if (ch == LF) {
						i++;
						count++;
					}
				}
				lineCount++;
			} else if (ch == LF)
				lineCount++;
			
			count++;
			i++;
		}
		return lineCount;
	}
	
	private static String region(int start, int end) {
		return "(" + start + " ... " + end + ")";
	}
	
	private static String position(int index) {
		return "(" + index + ")";
	}
	
	private void checkRange(final String operation, int index) {
		int len = length();
		
		if (index > len)
			throw new IndexOutOfBoundsException(operation + " " + position(index) + " ends beyond length " + len);
		if (index < 0)
			throw new IndexOutOfBoundsException(operation + " " + position(index) + " starts before 0");
	}
	
	private void checkRange(final String operation, int start, int end) {
		if (end < start)
			throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " has end before start");
		
		int len = length();
		if (start > len || end > len)
			throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " ends beyond length " + len);
		if (start < 0)
			throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " starts before 0");
	}
	
}
