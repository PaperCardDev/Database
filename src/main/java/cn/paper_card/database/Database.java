package cn.paper_card.database;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public final class Database extends JavaPlugin implements DatabaseApi {

    private final @NotNull MySQLImpl mySQL;

    private final @NotNull RemoteMySqlDbImpl remoteMySqlDb;

    private final @NotNull LocalSQLite localSQLite;

    private final @NotNull TextComponent prefix;

    public Database() {
        this.mySQL = new MySQLImpl(this);
        this.remoteMySqlDb = new RemoteMySqlDbImpl(this);

        this.prefix = Component.text()
                .append(Component.text("[").color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(this.getName()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text("]").color(NamedTextColor.LIGHT_PURPLE))
                .build();

        this.localSQLite = new LocalSQLite() {
            @Override
            public @NotNull Connection connectImportant() throws SQLException {
                return new DatabaseConnection.Important(Database.this.getDataFolder()).getConnection();
            }

            @Override
            public @NotNull Connection connectNormal() throws SQLException {
                return new DatabaseConnection.Normal(Database.this.getDataFolder()).getConnection();
            }

            @Override
            public @NotNull Connection connectUnimportant() throws SQLException {
                return new DatabaseConnection.Unimportant(Database.this.getDataFolder()).getConnection();
            }
        };
    }

    @Override
    public void onEnable() {

        new MainCommand(this);

        final File folder = this.getDataFolder();

        this.getLogger().info("正在测试SQLite数据库连接...");

        try {
            final DatabaseConnection connection = this.connectImportant();
            connection.close();
            this.getLogger().info("[SQLite]Important数据库正常。");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final DatabaseConnection connection = this.connectNormal();
            connection.close();
            this.getLogger().info("[SQLite]Normal数据库正常。");
        } catch (Exception e) {
            this.getLogger().severe(e.toString());
            e.printStackTrace();
        }

        try {
            final DatabaseConnection connection = this.connectUnimportant();
            connection.close();
            this.getLogger().info("[SQLite]Unimportant数据库正常。");
        } catch (Exception e) {
            this.getLogger().severe(e.toString());
            e.printStackTrace();
        }

        testMySql();

        this.saveConfig();
    }

    @Override
    public void onDisable() {
        this.remoteMySqlDb.closeAll();
    }

    private void testMySql() {
        this.getLogger().info("正在测试MySQL数据库连接...");

        try {
            final Connection connection = this.getRemoteMySqlDb().getConnectionImportant().getRowConnection();
//            connection.close();
            this.getLogger().info("[MySQL]Important数据库正常。");
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
        }

        try {
            final Connection connection = this.getRemoteMySqlDb().getConnectionNormal().getRowConnection();
//            connection.close();
            this.getLogger().info("[MySQL]Normal数据库正常。");
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
        }

        try {
            final Connection connection = this.getRemoteMySqlDb().getConnectionUnimportant().getRowConnection();
//            connection.close();
            this.getLogger().info("[MySQL]Unimportant数据库正常。");
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
        }

    }


    @Override
    public @NotNull DatabaseConnection connectImportant() throws Exception {
        return new DatabaseConnection.Important(this.getDataFolder());
    }

    @Override
    public @NotNull DatabaseConnection connectNormal() throws Exception {
        return new DatabaseConnection.Normal(this.getDataFolder());
    }

    @Override
    public @NotNull DatabaseConnection connectUnimportant() throws Exception {
        return new DatabaseConnection.Unimportant(this.getDataFolder());
    }

    @Override
    public @NotNull MySql getMySql() {
        return this.mySQL;
    }

    @Override
    public @NotNull RemoteMySqlDb getRemoteMySqlDb() {
        return this.remoteMySqlDb;
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

    void sendInfo(@NotNull CommandSender sender, @NotNull String info) {
        sender.sendMessage(Component.text()
                .append(this.prefix)
                .appendSpace()
                .append(Component.text(info).color(NamedTextColor.GREEN))
                .build()
        );
    }

    void sendInfo(@NotNull CommandSender sender, @NotNull TextComponent info) {
        sender.sendMessage(Component.text()
                .append(this.prefix)
                .appendSpace()
                .append(info)
                .build()
        );
    }

    @NotNull Permission addPermission(@NotNull String name) {
        final Permission permission = new Permission(name);
        this.getServer().getPluginManager().addPermission(permission);
        return permission;
    }
}
