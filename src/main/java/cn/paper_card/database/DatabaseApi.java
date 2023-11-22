package cn.paper_card.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseApi {
    @Deprecated
    @NotNull DatabaseConnection connectImportant() throws Exception;

    @Deprecated
    @NotNull DatabaseConnection connectNormal() throws Exception;

    @Deprecated
    @NotNull DatabaseConnection connectUnimportant() throws Exception;

    @Deprecated
    interface MySql {
        @NotNull Connection connectImportant() throws Exception;

        @NotNull Connection connectNormal() throws Exception;

        @NotNull Connection connectUnimportant() throws Exception;
    }

    interface MySqlConnection {
        long getLastUseTime();

        void setLastUseTime();

        @NotNull Connection getRowConnection() throws SQLException;

        int getConnectCount();

        void testConnection() throws SQLException;

        void checkClosedException(@NotNull SQLException e) throws SQLException;
    }

    interface RemoteMySqlDb {

        @NotNull MySqlConnection getConnectionImportant();

        @NotNull MySqlConnection getConnectionNormal();

        @NotNull MySqlConnection getConnectionUnimportant();

    }

    interface LocalSQLite {
        @NotNull Connection connectImportant() throws SQLException;

        @NotNull Connection connectNormal() throws SQLException;

        @NotNull Connection connectUnimportant() throws SQLException;
    }

    @Deprecated
    @NotNull MySql getMySql();

    @NotNull RemoteMySqlDb getRemoteMySqlDb();

    @NotNull LocalSQLite getLocalSQLite();

    @NotNull MySqlConnection createMySqlConnection(@NotNull String address, @NotNull String user, @NotNull String password);
}
