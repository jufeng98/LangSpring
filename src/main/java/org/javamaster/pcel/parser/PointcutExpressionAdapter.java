package org.javamaster.pcel.parser;

import org.javamaster.pcel._PointcutExpressionLexer;
import com.intellij.lexer.FlexAdapter;

/**
 * 第十步:定义词法分析器适配器
 */
public class PointcutExpressionAdapter extends FlexAdapter {

    public PointcutExpressionAdapter() {
        super(new _PointcutExpressionLexer(null));
    }

}
