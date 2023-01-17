/*
 * Javolution - Java(tm) Solution for Real-Time and Embedded Systems
 * Copyright (c) 2012, Javolution (http://javolution.org/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.weg.android.editor.text.immutable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * A pruned and optimized version of javolution.text.Text
 *
 * <p> This class represents an immutable character sequence with
 * fast {@link #concat concatenation}, {@link #insert insertion} and
 * {@link #delete deletion} capabilities (O[Log(n)]) instead of
 * O[n] for StringBuffer/StringBuilder).</p>
 *
 * <p><i> Implementation Note: To avoid expensive copy operations ,
 * {@link ImmutableText} instances are broken down into smaller immutable
 * sequences, they form a minimal-depth binary tree.
 * The tree is maintained balanced automatically through <a
 * href="http://en.wikipedia.org/wiki/Tree_rotation">tree rotations</a>.
 * Insertion/deletions are performed in {@code O[Log(n)]}
 * instead of {@code O[n]} for
 * {@code StringBuffer/StringBuilder}.</i></p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author Wilfried Middleton
 * @version 5.3, January 10, 2007
 */
@SuppressWarnings({"AssignmentToForLoopParameter", "UnnecessaryThis"})
public final class ImmutableText implements CharSequence, Parcelable {
	/**
	 * Holds the default size for primitive blocks of characters.
	 */
	private static final int BLOCK_SIZE = 1 << 6;
	
	/**
	 * Holds the mask used to ensure a block boundary cesures.
	 */
	private static final int BLOCK_MASK = -BLOCK_SIZE;
	
	private Node myNode;
	
	private ImmutableText(Node node) {
		myNode = node;
	}
	
	/**
	 * Returns the text representing the specified object.
	 *
	 * @param obj the object to represent as text.
	 * @return the textual representation of the specified object.
	 */
	public static ImmutableText valueOf(Object obj) {
		if (obj instanceof ImmutableText) return (ImmutableText) obj;
		if (obj instanceof CharSequence)
			return ((CharSequence) obj).length() == 0 ? EMPTY : valueOf((CharSequence) obj);
		return valueOf(String.valueOf(obj));
	}
	
	public static ImmutableText valueOf(CharSequence str) {
		return new ImmutableText(createLeafNode(str));
	}
	
	public static ImmutableText valueOf(char[] chars) {
		return new ImmutableText(createLeafNode(new CharArrayCharSequence(chars)));
	}
	
	static LeafNode createLeafNode(CharSequence str) {
		byte[] bytes = toBytesIfPossible(str);
		if (bytes != null) {
			return new Leaf8BitNode(bytes);
		}
		char[] chars = new char[str.length()];
		getChars(str, chars, 0, 0, str.length());
		return new WideLeafNode(chars);
	}
	
	private static final int GET_CHARS_THRESHOLD = 10;
	
	/**
	 * Copies necessary number of symbols from the given char sequence to the given array.
	 *
	 * @param src       source data holder
	 * @param dst       output data buffer
	 * @param srcOffset source text offset
	 * @param dstOffset start offset to use within the given output data buffer
	 * @param len       number of source data symbols to copy to the given buffer
	 */
	static void getChars(CharSequence src, char[] dst, int srcOffset, int dstOffset, int len) {
		if (len >= GET_CHARS_THRESHOLD) {
			if (src instanceof String) {
				((String) src).getChars(srcOffset, srcOffset + len, dst, dstOffset);
				return;
			} else if (src instanceof StringBuffer) {
				((StringBuffer) src).getChars(srcOffset, srcOffset + len, dst, dstOffset);
				return;
			} else if (src instanceof StringBuilder) {
				((StringBuilder) src).getChars(srcOffset, srcOffset + len, dst, dstOffset);
				return;
			}
		}
		
		for (int i = 0, j = srcOffset, max = srcOffset + len; j < max && i < dst.length; i++, j++) {
			dst[i + dstOffset] = src.charAt(j);
		}
	}
	
	
	private static byte[] toBytesIfPossible(CharSequence seq) {
		byte[] bytes = new byte[seq.length()];
		for (int i = 0; i < bytes.length; i++) {
			char c = seq.charAt(i);
			if ((c & 0xff00) != 0) {
				return null;
			}
			bytes[i] = (byte) c;
		}
		return bytes;
	}
	
	/**
	 * When first loaded, ImmutableText contents are stored as a single large array. This saves memory but isn't
	 * modification-friendly as it disallows slightly changed texts to retain most of the internal structure of the
	 * original document. Whoever retains old non-chunked version will use more memory than really needed.
	 *
	 * @return a copy of this text better prepared for small modifications to fully enable structure-sharing capabilities
	 */
	private ImmutableText ensureChunked() {
		if (length() > BLOCK_SIZE && myNode instanceof LeafNode) {
			return new ImmutableText(nodeOf((LeafNode) myNode, 0, length()));
		}
		return this;
	}
	
	private static Node nodeOf(LeafNode node, int offset, int length) {
		if (length <= BLOCK_SIZE) {
			return node.subNode(offset, offset + length);
		}
		// Splits on a block boundary.
		int half = ((length + BLOCK_SIZE) >> 1) & BLOCK_MASK;
		return new CompositeNode(nodeOf(node, offset, half), nodeOf(node, offset + half, length - half));
	}
	
	private static final LeafNode EMPTY_NODE = new Leaf8BitNode(new byte[0]);
	private static final ImmutableText EMPTY = new ImmutableText(EMPTY_NODE);
	
	/**
	 * Returns the length of this text.
	 *
	 * @return the number of characters (16-bits Unicode) composing this text.
	 */
	
	public int length() {
		return myNode.length();
	}
	
	/**
	 * Concatenates the specified text to the end of this text.
	 * This method is very fast (faster even than
	 * {@code StringBuffer.append(String)}) and still returns
	 * a text instance with an internal binary tree of minimal depth!
	 *
	 * @param that the text that is concatenated.
	 * @return {@code this + that}
	 */
	private ImmutableText concat(ImmutableText that) {
		return that.length() == 0 ? this : length() == 0 ? that : new ImmutableText(concatNodes(ensureChunked().myNode, that.ensureChunked().myNode));
	}
	
	
	public ImmutableText concat(CharSequence sequence) {
		return concat(valueOf(sequence));
	}
	
	/**
	 * Returns a portion of this text.
	 *
	 * @param start the index of the first character inclusive.
	 * @return the sub-text starting at the specified position.
	 * @throws IndexOutOfBoundsException if {@code (start < 0) ||
	 *                                   (start > this.length())}
	 */
	private ImmutableText subtext(int start) {
		return subtext(start, length());
	}
	
	/**
	 * Returns the text having the specified text inserted at
	 * the specified location.
	 *
	 * @param index the insertion position.
	 * @param txt   the text being inserted.
	 * @return {@code subtext(0, index).concat(txt).concat(subtext(index))}
	 * @throws IndexOutOfBoundsException if {@code (index < 0) ||
	 *                                   (index > this.length())}
	 */
	private ImmutableText insert(int index, ImmutableText txt) {
		return subtext(0, index).concat(txt).concat(subtext(index));
	}
	
	
	public ImmutableText insert(int index, CharSequence seq) {
		return insert(index, valueOf(seq));
	}
	
	/**
	 * Returns the text without the characters between the specified indexes.
	 *
	 * @param start the beginning index, inclusive.
	 * @param end   the ending index, exclusive.
	 * @return {@code subtext(0, start).concat(subtext(end))}
	 * @throws IndexOutOfBoundsException if {@code (start < 0) || (end < 0) ||
	 *                                   (start > end) || (end > this.length()}
	 */
	
	public ImmutableText delete(int start, int end) {
		if (start == end) return this;
		if (start > end) {
			throw new IndexOutOfBoundsException();
		}
		return ensureChunked().subtext(0, start).concat(subtext(end));
	}
	
	
	@NonNull
	public CharSequence subSequence(final int start, final int end) {
		if (start == 0 && end == length()) return this;
		return new CharSequenceSubSequence(this, start, end);
	}
	
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ImmutableText)) {
			return false;
		}
		return regionMatches(this, (ImmutableText) obj);
	}
	
	private static boolean regionMatches(CharSequence buffer, CharSequence s) {
		if (s.length() > buffer.length()) return false;
		for (int i = 0; i < s.length(); i++) {
			if (buffer.charAt(i) != s.charAt(i)) return false;
		}
		return true;
	}
	
	static boolean regionMatches(char[] buffer, int start, int end, CharSequence s) {
		final int len = s.length();
		if (start + len > end) return false;
		if (start < 0) return false;
		for (int i = 0; i < len; i++) {
			if (buffer[start + i] != s.charAt(i)) return false;
		}
		return true;
	}
	
	private transient int hash;
	
	/**
	 * Returns the hash code for this text.
	 *
	 * @return the hash code value.
	 */
	
	public int hashCode() {
		int h = hash;
		if (h == 0) {
			hash = h = stringHashCode(this, length());
		}
		return h;
	}
	
	static int stringHashCode(CharSequence chars, int len) {
		int h = 0;
		for (int off = 0; off < len; off++) {
			h = 31 * h + chars.charAt(off);
		}
		return h;
	}
	
	public char charAt(int index) {
		InnerLeaf leaf = myLastLeaf;
		if (leaf == null || index < leaf.offset || index >= leaf.end) {
			myLastLeaf = leaf = findLeaf(index);
		}
		return leaf.leafNode.charAt(index - leaf.offset);
	}
	
	private InnerLeaf myLastLeaf;
	
	private InnerLeaf findLeaf(int index) {
		Node node = myNode;
		if (index < 0 || index >= node.length())
			throw new IndexOutOfBoundsException("Index out of range: " + index);
		
		int offset = 0;
		while (true) {
			if (index >= node.length()) {
				throw new IndexOutOfBoundsException();
			}
			if (node instanceof LeafNode) {
				return new InnerLeaf((LeafNode) node, offset);
			}
			CompositeNode composite = (CompositeNode) node;
			if (index < composite.head.length()) {
				node = composite.head;
			} else {
				offset += composite.head.length();
				index -= composite.head.length();
				node = composite.tail;
			}
		}
	}
	
	/**
	 * Returns a portion of this text.
	 *
	 * @param start the index of the first character inclusive.
	 * @param end   the index of the last character exclusive.
	 * @return the sub-text starting at the specified start position and
	 * ending just before the specified end position.
	 * @throws IndexOutOfBoundsException if {@code (start < 0) || (end < 0) ||
	 *                                   (start > end) || (end > this.length())}
	 */
	
	public ImmutableText subtext(int start, int end) {
		if (start < 0 || start > end || end > length()) {
			throw new IndexOutOfBoundsException();
		}
		if (start == 0 && end == length()) {
			return this;
		}
		if (start == end) {
			return EMPTY;
		}
		
		return new ImmutableText(myNode.subNode(start, end));
	}
	
	/**
	 * Copies the characters from this text into the destination
	 * character array.
	 *
	 * @param start   the index of the first character to copy.
	 * @param end     the index after the last character to copy.
	 * @param dest    the destination array.
	 * @param destPos the start offset in the destination array.
	 * @throws IndexOutOfBoundsException if {@code (start < 0) || (end < 0) ||
	 *                                   (start > end) || (end > this.length())}
	 */
	
	public void getChars(int start, int end, char[] dest, int destPos) {
		myNode.getChars(start, end, dest, destPos);
	}
	
	public void drawText(@NonNull Canvas c, int start, int end, float x, float y, @NonNull Paint p) {
		myNode.drawText(c, start, end, x, y, p);
	}
	
	public int measureText(int start, int end, @NonNull Paint p) {
		return myNode.measureText(start, end, p);
	}
	
	public int getTextWidths(int start, int end, float[] widths, @NonNull Paint p) {
		return myNode.getTextWidths(start, end, widths, p);
	}
	
	
	/**
	 * Returns the {@code String} representation of this text.
	 *
	 * @return the {@code java.lang.String} for this text.
	 */
	
	
	@NonNull
	public String toString() {
		return myNode.toString();
	}
	
	
	static Node concatNodes(Node node1, Node node2) {
		// All Text instances are maintained balanced:
		//   (head < tail * 2) & (tail < head * 2)
		final int length = node1.length() + node2.length();
		if (length <= BLOCK_SIZE) { // Merges to primitive.
			return createLeafNode(new MergingCharSequence(node1, node2));
		} else { // Returns a composite.
			Node head = node1;
			Node tail = node2;
			
			if ((head.length() << 1) < tail.length() && tail instanceof CompositeNode) {
				// head too small, returns (head + tail/2) + (tail/2)
				if (((CompositeNode) tail).head.length() > ((CompositeNode) tail).tail.length()) {
					// Rotates to concatenate with smaller part.
					tail = ((CompositeNode) tail).rightRotation();
				}
				head = concatNodes(head, ((CompositeNode) tail).head);
				tail = ((CompositeNode) tail).tail;
			} else if ((tail.length() << 1) < head.length() && head instanceof CompositeNode) {
				// tail too small, returns (head/2) + (head/2 concat tail)
				if (((CompositeNode) head).tail.length() > ((CompositeNode) head).head.length()) {
					// Rotates to concatenate with smaller part.
					head = ((CompositeNode) head).leftRotation();
				}
				tail = concatNodes(((CompositeNode) head).tail, tail);
				head = ((CompositeNode) head).head;
			}
			return new CompositeNode(head, tail);
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(this.myNode);
		dest.writeSerializable(this.myLastLeaf);
	}
	
	public void readFromParcel(Parcel source) {
		this.myNode = (Node) source.readSerializable();
		this.myLastLeaf = (InnerLeaf) source.readSerializable();
	}
	
	private ImmutableText(Parcel in) {
		this.myNode = (Node) in.readSerializable();
		this.myLastLeaf = (InnerLeaf) in.readSerializable();
	}
	
	public static final Parcelable.Creator<ImmutableText> CREATOR = new Parcelable.Creator<ImmutableText>() {
		@Override
		public ImmutableText createFromParcel(Parcel source) {
			return new ImmutableText(source);
		}
		
		@Override
		public ImmutableText[] newArray(int size) {
			return new ImmutableText[size];
		}
	};
}
