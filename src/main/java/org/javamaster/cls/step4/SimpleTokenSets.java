package org.javamaster.cls.step4;

import org.javamaster.cls.psi.SimpleTypes;
import com.intellij.psi.tree.TokenSet;

public interface SimpleTokenSets {

    TokenSet IDENTIFIERS = TokenSet.create(SimpleTypes.KEY);

    TokenSet COMMENTS = TokenSet.create(SimpleTypes.COMMENT);

}