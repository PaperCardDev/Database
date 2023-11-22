package cn.paper_card.database;

import cn.paper_card.mc_command.TheMcCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

class MainCommand extends TheMcCommand.HasSub {

    private final @NotNull Permission permission;

    private final @NotNull Database plugin;

    MainCommand(@NotNull Database plugin) {
        super("database");
        this.plugin = plugin;
        this.permission = Objects.requireNonNull(plugin.getServer().getPluginManager().getPermission(this.getLabel() + ".command"));

        final PluginCommand command = plugin.getCommand("database");
        assert command != null;
        command.setTabCompleter(this);
        command.setExecutor(this);

        this.addSubCommand(new Reload());
        this.addSubCommand(new Status());
    }

    @Override
    protected boolean canNotExecute(@NotNull CommandSender commandSender) {
        return !commandSender.hasPermission(this.permission);
    }

    class Status extends TheMcCommand {

        private final @NotNull Permission permission;

        protected Status() {
            super("status");
            this.permission = plugin.addPermission(MainCommand.this.permission.getName() + "." + this.getLabel());
        }

        @Override
        protected boolean canNotExecute(@NotNull CommandSender commandSender) {
            return !commandSender.hasPermission(this.permission);
        }

        private void appendConInfo(@NotNull TextComponent.Builder text, @NotNull MySqlConnectionImpl connection,
                                   @NotNull String name, long cur) {

            final long d = cur - connection.getLastUseTime();

            text.append(Component.text(name));
            text.append(Component.text(" | "));
            text.append(Component.text(connection.isConnected() ? "已连接" : "未连接"));
            text.append(Component.text(" | "));
            text.append(Component.text("最长空闲: %dms".formatted(connection.getMaxIdleTime())));
            text.append(Component.text(" | "));
            text.append(Component.text("最短空闲: %dms".formatted(connection.getMinIdleTime())));
            text.append(Component.text(" | "));
            text.append(Component.text("连接次数: %d".formatted(connection.getConnectCount())));
            text.append(Component.text(" | "));
            text.append(Component.text("上次使用: %dms前".formatted(d)));
        }

        @Override
        public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

            final DatabaseApi.RemoteMySqlDb remoteMySqlDb = plugin.getRemoteMySqlDb();
            final MySqlConnectionImpl important = (MySqlConnectionImpl) remoteMySqlDb.getConnectionImportant();
            final MySqlConnectionImpl normal = (MySqlConnectionImpl) remoteMySqlDb.getConnectionNormal();
            final MySqlConnectionImpl unimportant = (MySqlConnectionImpl) remoteMySqlDb.getConnectionUnimportant();

            final TextComponent.Builder text = Component.text();
            text.append(Component.text("---- 远程MySQL数据库连接状态 ----"));
            text.appendNewline();

            final long cur = System.currentTimeMillis();
            appendConInfo(text, important, "Important", cur);
            text.appendNewline();
            appendConInfo(text, normal, "Normal", cur);
            text.appendNewline();
            appendConInfo(text, unimportant, "Unimportant", cur);

            plugin.sendInfo(commandSender, text.build());

            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            return null;
        }
    }

    class Reload extends TheMcCommand {

        private final @NotNull Permission permission;

        protected Reload() {
            super("reload");
            this.permission = plugin.addPermission(MainCommand.this.permission.getName() + "." + this.getLabel());
        }

        @Override
        protected boolean canNotExecute(@NotNull CommandSender commandSender) {
            return !commandSender.hasPermission(this.permission);
        }

        @Override
        public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            plugin.reloadConfig();
            plugin.sendInfo(commandSender, "已重载配置");
            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            return null;
        }
    }
}
