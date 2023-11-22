package cn.paper_card.database;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

class RemoteMySqlDbImpl implements DatabaseApi.RemoteMySqlDb {
    private final @NotNull Database plugin;

    private final @NotNull MySqlConnectionImpl important;
    private final @NotNull MySqlConnectionImpl normal;
    private final @NotNull MySqlConnectionImpl unimportant;

    RemoteMySqlDbImpl(@NotNull Database plugin) {
        this.plugin = plugin;
        this.important = new MySqlConnectionImpl(new MyConfig("important"));
        this.normal = new MySqlConnectionImpl(new MyConfig("normal"));
        this.unimportant = new MySqlConnectionImpl(new MyConfig("unimportant"));
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

    void closeAll() {
        try {
            this.important.close();
        } catch (SQLException e) {
            plugin.getLogger().severe(e.toString());
            e.printStackTrace();
        }

        try {
            this.normal.close();
        } catch (SQLException e) {
            plugin.getLogger().severe(e.toString());
            e.printStackTrace();
        }

        try {
            this.unimportant.close();
        } catch (SQLException e) {
            plugin.getLogger().severe(e.toString());
            e.printStackTrace();
        }
    }

    private class MyConfig implements MySqlConnectionImpl.Config {

        private final @NotNull String type;

        private MyConfig(@NotNull String type) {
            this.type = type;
        }

        @Override
        public @NotNull String getAddress() {
            final String key = "mysql.remote.%s.address".formatted(this.type);
            final String address = plugin.getConfig().getString(key, "");
            plugin.getConfig().set(key, address);
            return address;
        }

        @Override
        public @NotNull String getUser() {
            final String key = "mysql.remote.%s.user".formatted(this.type);
            final String user = plugin.getConfig().getString(key, "");
            plugin.getConfig().set(key, user);
            return user;
        }

        @Override
        public @NotNull String getPassword() {
            final String key = "mysql.remote.%s.password".formatted(this.type);
            final String password = plugin.getConfig().getString(key, "");
            plugin.getConfig().set(key, password);
            return password;
        }
    }
}
