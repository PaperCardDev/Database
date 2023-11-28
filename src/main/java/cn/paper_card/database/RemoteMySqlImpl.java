package cn.paper_card.database;

import cn.paper_card.database.api.DatabaseApi;
import org.jetbrains.annotations.NotNull;

class RemoteMySqlImpl implements DatabaseApi.RemoteMySQL {



    private final @NotNull MySqlConnectionImpl important;
    private final @NotNull MySqlConnectionImpl normal;
    private final @NotNull MySqlConnectionImpl unimportant;

    RemoteMySqlImpl(@NotNull MySqlConnectionImpl.Config important,
                    @NotNull MySqlConnectionImpl.Config normal,
                    @NotNull MySqlConnectionImpl.Config unimportant) {

        this.important = new MySqlConnectionImpl(important);
        this.normal = new MySqlConnectionImpl(normal);
        this.unimportant = new MySqlConnectionImpl(unimportant);
    }

    @Override
    public @NotNull DatabaseApi.MySqlConnection getConnectionImportant() {
        return this.important;
    }

    @Override
    public @NotNull DatabaseApi.MySqlConnection getConnectionNormal() {
        return this.normal;
    }

    @Override
    public @NotNull DatabaseApi.MySqlConnection getConnectionUnimportant() {
        return this.unimportant;
    }
}
