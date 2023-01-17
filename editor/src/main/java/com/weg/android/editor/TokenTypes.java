package com.weg.android.editor;

public interface TokenTypes {
	TokenType PLAIN=new TokenType("language.plain");
	TokenType OPERATOR=new TokenType(PLAIN,"language.operator");
	TokenType SEPARATOR =new TokenType(PLAIN,"language.separator");
	TokenType METADATA=new TokenType(PLAIN,"language.metadata");
	TokenType KEYWORD=new TokenType(PLAIN,"language.keyword");
	TokenType IDENTIFIER=new TokenType(PLAIN,"language.identifier");
	TokenType NUMBER=new TokenType(PLAIN,"language.number");
	TokenType STRING=new TokenType(PLAIN,"language.string");
	TokenType COMMENT=new TokenType(PLAIN,"language.comment");
}
