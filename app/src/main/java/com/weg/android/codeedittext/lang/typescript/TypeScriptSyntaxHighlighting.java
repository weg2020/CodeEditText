package com.weg.android.codeedittext.lang.typescript;

import static com.weg.android.codeedittext.lang.typescript.TypeScriptLexer.Boolean;
import static com.weg.android.codeedittext.lang.typescript.TypeScriptLexer.Class;
import static com.weg.android.codeedittext.lang.typescript.TypeScriptLexer.Enum;
import static com.weg.android.codeedittext.lang.typescript.TypeScriptLexer.Number;
import static com.weg.android.codeedittext.lang.typescript.TypeScriptLexer.Package;
import static com.weg.android.codeedittext.lang.typescript.TypeScriptLexer.String;
import static com.weg.android.codeedittext.lang.typescript.TypeScriptLexer.Void;
import static com.weg.android.codeedittext.lang.typescript.TypeScriptLexer.*;

import static org.antlr.v4.runtime.CharStreams.*;

import com.weg.android.codeedittext.lang.OpenFileModel;
import com.weg.android.codeedittext.lang.SyntaxHighlighting;
import com.weg.android.editor.TokenTypes;
import com.weg.android.editor.syntax.Highlighting;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.io.IOException;

public class TypeScriptSyntaxHighlighting extends SyntaxHighlighting {
	
	private final TypeScriptLexer lexer = new TypeScriptLexer(null);
	private final TypeScriptParser parser = new TypeScriptParser(null);
	
	@Override
	public void highlighting(OpenFileModel view, Highlighting.Builder builder) {
		try {
			lexer.setInputStream(fromReader(view.getReader()));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			for (Token token : tokens.getTokens()) {
				int type = token.getType();
				int start = token.getStartIndex();
				int end = token.getStopIndex() + 1;
				switch (type) {
					case MultiLineComment:
					case SingleLineComment:
					case HtmlComment:
					case CDataComment:
						builder.highlight(TokenTypes.COMMENT, start, end);
						break;
					case OpenBracket:
					case CloseBracket:
					case OpenParen:
					case CloseParen:
					case OpenBrace:
					case TemplateCloseBrace:
					case CloseBrace:
					case SemiColon:
					case Comma:
					case Assign:
					case QuestionMark:
					case Colon:
					case Ellipsis:
					case Dot:
						builder.highlight(TokenTypes.SEPARATOR, start, end);
						break;
					case PlusPlus:
					case MinusMinus:
					case Plus:
					case Minus:
					case BitNot:
					case Not:
					case Multiply:
					case Divide:
					case Modulus:
					case RightShiftArithmetic:
					case LeftShiftArithmetic:
					case RightShiftLogical:
					case LessThan:
					case MoreThan:
					case LessThanEquals:
					case GreaterThanEquals:
					case Equals_:
					case NotEquals:
					case IdentityEquals:
					case IdentityNotEquals:
					case BitAnd:
					case BitXOr:
					case BitOr:
					case And:
					case Or:
					case MultiplyAssign:
					case DivideAssign:
					case ModulusAssign:
					case PlusAssign:
					case MinusAssign:
					case LeftShiftArithmeticAssign:
					case RightShiftArithmeticAssign:
					case RightShiftLogicalAssign:
					case BitAndAssign:
					case BitXorAssign:
					case BitOrAssign:
					case ARROW:
						builder.highlight(TokenTypes.OPERATOR, start, end);
						break;
					case Break:
					case Do:
					case Instanceof:
					case Typeof:
					case Case:
					case Else:
					case New:
					case Var:
					case Catch:
					case Finally:
					case Return:
					case Void:
					case Continue:
					case For:
					case Switch:
					case While:
					case Debugger:
					case Function_:
					case This:
					case With:
					case Default:
					case If:
					case Throw:
					case Delete:
					case In:
					case Try:
					case As:
					case From:
					case ReadOnly:
					case Async:
					case Class:
					case Enum:
					case Extends:
					case Super:
					case Const:
					case Export:
					case Import:
					case Implements:
					case Let:
					case Private:
					case Public:
					case Interface:
					case Package:
					case Protected:
					case Static:
					case Yield:
					case Any:
					case Number:
					case Boolean:
					case String:
					case Symbol:
					case TypeAlias:
					case Get:
					case Set:
					case Constructor:
					case Namespace:
					case Require:
					case Module:
					case Declare:
					case Abstract:
					case Is:
					case NullLiteral:
					case BooleanLiteral:
						builder.highlight(TokenTypes.KEYWORD, start, end);
						break;
					case BinaryIntegerLiteral:
					case OctalIntegerLiteral2:
					case OctalIntegerLiteral:
					case HexIntegerLiteral:
					case DecimalLiteral:
						builder.highlight(TokenTypes.NUMBER, start, end);
						break;
					case StringLiteral:
						builder.highlight(TokenTypes.STRING, start, end);
						break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
