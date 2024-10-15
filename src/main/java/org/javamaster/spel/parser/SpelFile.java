package org.javamaster.spel.parser;

import org.javamaster.spel.SpelFileType;
import org.javamaster.spel.SpelLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 第十一步:定义根文件
 */
public class SpelFile extends PsiFileBase {

    public SpelFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SpelLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SpelFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Spring Expression File";
    }

}
