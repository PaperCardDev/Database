package cn.paper_card.database;

import cn.paper_card.database.api.DatabaseApi;
import cn.paper_card.database.api.Util;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

class LocalSQLiteImpl implements DatabaseApi.LocalSQLite {

    private final @NotNull File dataFolder;

    LocalSQLiteImpl(@NotNull File dataFolder) {
        this.dataFolder = dataFolder;
    }


    @Override
    public @NotNull Connection connectImportant() throws SQLException {
        return Util.connectSQLite(new File(this.dataFolder, "important.db"));
    }

    @Override
    public @NotNull Connection connectNormal() throws SQLException {
        return Util.connectSQLite(new File(this.dataFolder, "normal.db"));
    }

    @Override
    public @NotNull Connection connectUnimportant() throws SQLException {
        return Util.connectSQLite(new File(this.dataFolder, "unimportant.db"));
    }
}
