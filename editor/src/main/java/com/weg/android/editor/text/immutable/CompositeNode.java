package com.weg.android.editor.text.immutable;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

class CompositeNode extends Node {
	
	private static final long serialVersionUID = -7874644415678216997L;
	final int count;
	final Node head;
	final Node tail;
	
	CompositeNode(Node head, Node tail) {
		count = head.length() + tail.length();
		this.head = head;
		this.tail = tail;
	}
	
	
	public int length() {
		return count;
	}
	
	
	public char charAt(int index) {
		int headLength = head.length();
		return index < headLength ? head.charAt(index) : tail.charAt(index - headLength);
	}
	
	Node rightRotation() {
		// See: http://en.wikipedia.org/wiki/Tree_rotation
		Node P = this.head;
		if (!(P instanceof CompositeNode)) {
			return this; // Head not a composite, cannot rotate.
		}
		Node A = ((CompositeNode) P).head;
		Node B = ((CompositeNode) P).tail;
		return new CompositeNode(A, new CompositeNode(B, this.tail));
	}
	
	Node leftRotation() {
		// See: http://en.wikipedia.org/wiki/Tree_rotation
		Node Q = this.tail;
		if (!(Q instanceof CompositeNode)) {
			return this; // Tail not a composite, cannot rotate.
		}
		Node B = ((CompositeNode) Q).head;
		Node C = ((CompositeNode) Q).tail;
		return new CompositeNode(new CompositeNode(this.head, B), C);
	}
	
	
	public void getChars(int start, int end, char[] dest, int destPos) {
		final int cesure = head.length();
		if (end <= cesure) {
			head.getChars(start, end, dest, destPos);
		} else if (start >= cesure) {
			tail.getChars(start - cesure, end - cesure, dest, destPos);
		} else { // Overlaps head and tail.
			head.getChars(start, cesure, dest, destPos);
			tail.getChars(0, end - cesure, dest, destPos + cesure - start);
		}
	}
	
	@Override
	public void drawText(@NonNull Canvas c, int start, int end, float x, float y, @NonNull Paint p) {
		final int cesure = head.length();
		if (end <= cesure) {
			c.drawText(head, start, end, x, y, p);
		} else if (start >= cesure) {
			c.drawText(tail, start - cesure, end - cesure, x, y, p);
		} else { // Overlaps head and tail.
			char[] temp = TemporaryBuffer.obtain(end - start);
			head.getChars(start, cesure, temp, 0);
			tail.getChars(0, end - cesure, temp, cesure - start);
			c.drawText(temp, 0, end - start, x, y, p);
			TemporaryBuffer.recycle(temp);
		}
	}
	
	@Override
	public int measureText(int start, int end, @NonNull Paint p) {
		final int cesure = head.length();
		if (end <= cesure) {
			return (int) p.measureText(head,start,end);
		} else if (start >= cesure) {
			return (int) p.measureText(tail,start-cesure,end-cesure);
		} else { // Overlaps head and tail.
			char[] temp = TemporaryBuffer.obtain(end - start);
			head.getChars(start, cesure, temp, 0);
			tail.getChars(0, end - cesure, temp, cesure - start);
			int r= (int) p.measureText(temp,0,end - start);
			TemporaryBuffer.recycle(temp);
			return r;
		}
	}
	
	@Override
	public int getTextWidths(int start, int end, float[] widths, @NonNull Paint p) {
		final int cesure = head.length();
		if (end <= cesure) {
			return p.getTextWidths(head, start, end, widths);
		} else if (start >= cesure) {
			return p.getTextWidths(tail, start - cesure, end - cesure, widths);
		} else { // Overlaps head and tail.
			char[] temp = TemporaryBuffer.obtain(end - start);
			head.getChars(start, cesure, temp, 0);
			tail.getChars(0, end - cesure, temp, cesure - start);
			int r = p.getTextWidths(temp, 0, end - start, widths);
			TemporaryBuffer.recycle(temp);
			return r;
		}
	}
	
	
	public Node subNode(int start, int end) {
		final int cesure = head.length();
		if (end <= cesure) {
			return head.subNode(start, end);
		}
		if (start >= cesure) {
			return tail.subNode(start - cesure, end - cesure);
		}
		if (start == 0 && end == count) {
			return this;
		}
		// Overlaps head and tail.
		return ImmutableText.concatNodes(head.subNode(start, cesure), tail.subNode(0, end - cesure));
	}
}
