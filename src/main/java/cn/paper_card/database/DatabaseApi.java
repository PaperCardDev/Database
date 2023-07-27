package cn.paper_card.database;

import org.jetbrains.annotations.NotNull;

public interface DatabaseApi {
    @NotNull DatabaseConnection connectImportant() throws Exception;

    @NotNull DatabaseConnection connectNormal() throws Exception;

    @NotNull DatabaseConnection connectUnimportant() throws Exception;

}
