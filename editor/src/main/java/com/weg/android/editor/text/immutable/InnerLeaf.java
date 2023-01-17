package com.weg.android.editor.text.immutable;

import java.io.Serializable;

class InnerLeaf implements Serializable {
	
	private static final long serialVersionUID = -5216781464500402910L;
	final LeafNode leafNode;
	final int offset;
	final int end;
	
	InnerLeaf(LeafNode leafNode, int offset) {
		this.leafNode = leafNode;
		this.offset = offset;
		this.end = offset + leafNode.length();
	}
}
