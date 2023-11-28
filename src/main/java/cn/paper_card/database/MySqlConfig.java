package cn.paper_card.database;

import org.jetbrains.annotations.NotNull;

class MySqlConfig implements MySqlConnectionImpl.Config {
    private final @NotNull String type;
    private final @NotNull ThePlugin plugin;

    MySqlConfig(@NotNull String type, @NotNull ThePlugin plugin) {
        this.type = type;
        this.plugin = plugin;
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
