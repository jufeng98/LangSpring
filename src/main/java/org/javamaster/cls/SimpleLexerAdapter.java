package org.javamaster.cls;

import com.intellij.lexer.FlexAdapter;

public class SimpleLexerAdapter extends FlexAdapter {

    public SimpleLexerAdapter() {
        super(new _SimpleLexer(null));
    }

}
