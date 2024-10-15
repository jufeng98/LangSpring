package org.javamaster.pcel.highlight;

import org.javamaster.pcel.parser.PointcutExpressionAdapter;
import org.javamaster.pcel.psi.PointcutExpressionTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * 第十三步:定义语法高亮器
 */
public class PointcutExpressionSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey AOP_KEYWORD =
            createTextAttributesKey("AOP_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey AOP_EXPR =
            createTextAttributesKey("AOP_EXPR", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] AOP_KEYWORD_KEYS = new TextAttributesKey[]{AOP_KEYWORD};
    private static final TextAttributesKey[] AOP_EXPR_KEYS = new TextAttributesKey[]{AOP_EXPR};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new PointcutExpressionAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(PointcutExpressionTypes.AT_ANNOTATION)
                || tokenType.equals(PointcutExpressionTypes.BEAN)
                || tokenType.equals(PointcutExpressionTypes.AT_TARGET)
                || tokenType.equals(PointcutExpressionTypes.METHOD_REFERENCE)
                || tokenType.equals(PointcutExpressionTypes.EXECUTION)) {
            return AOP_KEYWORD_KEYS;
        }

        if (tokenType.equals(PointcutExpressionTypes.EXPR)) {
            return AOP_EXPR_KEYS;
        }

        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

}