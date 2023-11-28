package cn.paper_card.database;

import cn.paper_card.database.api.DatabaseApi;
import org.jetbrains.annotations.NotNull;

import java.io.File;

class DatabaseApiImpl implements DatabaseApi {

    private final @NotNull RemoteMySqlImpl remoteMySql;
    private final @NotNull LocalSQLiteImpl localSQLite;

    DatabaseApiImpl(@NotNull File dataFolder,
                    @NotNull MySqlConnectionImpl.Config important,
                    @NotNull MySqlConnectionImpl.Config normal,
                    @NotNull MySqlConnectionImpl.Config unimportant) {
        this.localSQLite = new LocalSQLiteImpl(dataFolder);
        this.remoteMySql = new RemoteMySqlImpl(important, normal, unimportant);
    }

    @Override
    public @NotNull RemoteMySQL getRemoteMySQL() {
        return this.remoteMySql;
    }

    @Override
    public @NotNull LocalSQLite getLocalSQLite() {
        return this.localSQLite;
    }

    @Override
    public @NotNull MySqlConnection createMySqlConnection(@NotNull String address, @NotNull String user, @NotNull String password) {
        return new MySqlConnectionImpl(new MySqlConnectionImpl.Config() {
            @Override
            public @NotNull String getAddress() {
                return address;
            }

            @Override
            public @NotNull String getUser() {
                return user;
            }

            @Override
            public @NotNull String getPassword() {
                return password;
            }
        });
    }
}
