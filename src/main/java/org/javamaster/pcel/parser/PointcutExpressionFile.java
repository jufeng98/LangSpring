package org.javamaster.pcel.parser;

import org.javamaster.pcel.PointcutExpressionFileType;
import org.javamaster.pcel.PointcutExpressionLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 第十一步:定义根文件
 */
public class PointcutExpressionFile extends PsiFileBase {

    public PointcutExpressionFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, PointcutExpressionLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return PointcutExpressionFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "PointcutExpression File";
    }

}
