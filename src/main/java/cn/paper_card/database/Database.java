package cn.paper_card.database;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;

@SuppressWarnings("unused")
public final class Database extends JavaPlugin implements DatabaseApi {

    private final @NotNull MySQLImpl mySQL;

    public Database() {
        this.mySQL = new MySQLImpl(this);
    }

    @Override
    public void onEnable() {
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

    private void testMySql() {
        this.getLogger().info("正在测试MySQL数据库连接...");

        try {
            final Connection connection = this.getMySql().connectImportant();
            connection.close();
            this.getLogger().info("[MySQL]Important数据库正常。");
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
        }

        try {
            final Connection connection = this.getMySql().connectNormal();
            connection.close();
            this.getLogger().info("[MySQL]Normal数据库正常。");
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
        }

        try {
            final Connection connection = this.getMySql().connectUnimportant();
            connection.close();
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
}
