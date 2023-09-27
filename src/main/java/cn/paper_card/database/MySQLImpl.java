package cn.paper_card.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

class MySQLImpl implements DatabaseApi.MySql {

    private final @NotNull Database plugin;

    MySQLImpl(@NotNull Database plugin) {
        this.plugin = plugin;
    }

    private @NotNull Connection connect(@NotNull String type) throws Exception {

        final String keyAddress = "mysql." + type + ".address";
        final String keyUser = "mysql." + type + ".user";
        final String keyPassword = "mysql." + type + ".password";

        final String address = this.plugin.getConfig().getString(keyAddress, "");
        final String user = this.plugin.getConfig().getString(keyUser, "");
        final String password = this.plugin.getConfig().getString(keyPassword, "");

        this.plugin.getConfig().set(keyAddress, address);
        this.plugin.getConfig().set(keyUser, user);
        this.plugin.getConfig().set(keyPassword, password);

        if (address.isEmpty()) throw new Exception("%s的地址未指定！".formatted(type));
        if (user.isEmpty()) throw new Exception("%s的用户名未指定！".formatted(type));


        return new MySqlConnection(address, user, password).getConnection();
    }

    @Override
    public @NotNull Connection connectImportant() throws Exception {
        return this.connect("important");
    }

    @Override
    public @NotNull Connection connectNormal() throws Exception {
        return this.connect("normal");
    }

    @Override
    public @NotNull Connection connectUnimportant() throws Exception {
        return this.connect("unimportant");
    }
}
