package cn.paper_card.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

public interface DatabaseApi {
    @NotNull DatabaseConnection connectImportant() throws Exception;

    @NotNull DatabaseConnection connectNormal() throws Exception;

    @NotNull DatabaseConnection connectUnimportant() throws Exception;

    interface MySql {
        @NotNull Connection connectImportant() throws Exception;

        @NotNull Connection connectNormal() throws Exception;

        @NotNull Connection connectUnimportant() throws Exception;
    }

    @NotNull MySql getMySql();

}
