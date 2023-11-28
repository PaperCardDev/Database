package cn.paper_card.database;

import cn.paper_card.database.api.DatabaseApi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public final class ThePlugin extends JavaPlugin {
    private final @NotNull TextComponent prefix;

    private final @NotNull DatabaseApiImpl databaseApi;

    public ThePlugin() {
        this.prefix = Component.text()
                .append(Component.text("[").color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(this.getName()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text("]").color(NamedTextColor.LIGHT_PURPLE))
                .build();

        this.databaseApi = new DatabaseApiImpl(this.getDataFolder(),
                new MySqlConfig("important", this),
                new MySqlConfig("normal", this),
                new MySqlConfig("unimportant", this)
        );
    }

    void handleException(@NotNull String msg, @NotNull Throwable t) {
        this.getSLF4JLogger().error(msg, t);
    }

    @Override
    public void onLoad() {
        // 注册API
        this.getSLF4JLogger().info("注册%s...".formatted(DatabaseApi.class.getSimpleName()));
        this.getServer().getServicesManager().register(DatabaseApi.class, this.databaseApi, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {

        // 注册命令
        new MainCommand(this);

        // 测试SQLite
        this.testSQLite();

        // 测试MySQL
        this.testMySQL();

        // 保持配置
        this.saveConfig();
    }

    @Override
    public void onDisable() {
        // 关闭MYSQL数据库连接

        try {
            this.databaseApi.getRemoteMySQL().getConnectionImportant().close();
        } catch (SQLException e) {
            this.handleException("关闭MySQL数据库连接时异常，Important", e);
        }

        try {
            this.databaseApi.getRemoteMySQL().getConnectionNormal().close();
        } catch (SQLException e) {
            this.handleException("关闭MySQL数据库连接时异常，Normal", e);
        }

        try {
            this.databaseApi.getRemoteMySQL().getConnectionUnimportant().close();
        } catch (SQLException e) {
            this.handleException("关闭MySQL数据库连接时异常，Unimportant", e);
        }
    }

    private void testSQLite() {
        this.getSLF4JLogger().info("正在测试SQLite数据库连接...");

        try {
            final Connection connection = this.databaseApi.getLocalSQLite().connectImportant();
            connection.close();
            this.getSLF4JLogger().info("[SQLite]Important数据库正常。");
        } catch (SQLException e) {
            this.handleException("无法连接SQLite数据库：Important", e);
        }

        try {
            final Connection connection = this.databaseApi.getLocalSQLite().connectNormal();
            connection.close();
            this.getSLF4JLogger().info("[SQLite]Normal数据库正常。");
        } catch (SQLException e) {
            this.handleException("无法连接SQLite数据库：Normal", e);
        }

        try {
            final Connection connection = this.databaseApi.getLocalSQLite().connectUnimportant();
            connection.close();
            this.getSLF4JLogger().info("[SQLite]Unimportant数据库正常。");
        } catch (Exception e) {
            this.handleException("无法连接SQLite数据库：Unimportant", e);
        }
    }

    private void testMySQL() {
        this.getSLF4JLogger().info("正在测试MySQL数据库连接...");

        try {
            final Connection connection = this.databaseApi.getRemoteMySQL().getConnectionImportant().getRawConnection();
            this.getSLF4JLogger().info("[MySQL]Important数据库正常。");
        } catch (SQLException e) {
            this.handleException("无法连接到MySQL数据库：Important", e);
        }

        try {
            final Connection connection = this.databaseApi.getRemoteMySQL().getConnectionNormal().getRawConnection();
            this.getSLF4JLogger().info("[MySQL]Normal数据库正常。");
        } catch (Exception e) {
            this.handleException("无法连接到MySQL数据库：Normal", e);
        }

        try {
            final Connection connection = this.databaseApi.getRemoteMySQL().getConnectionUnimportant().getRawConnection();
            this.getSLF4JLogger().info("[MySQL]Unimportant数据库正常。");
        } catch (Exception e) {
            this.handleException("无法连接到MySQL数据库：Unimportant", e);
        }
    }

    void sendInfo(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text()
                .append(this.prefix)
                .appendSpace()
                .append(Component.text("已重载配置").color(NamedTextColor.GREEN))
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

    @NotNull DatabaseApiImpl getDatabaseApi() {
        return this.databaseApi;
    }
}
