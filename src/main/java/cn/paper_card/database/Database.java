package cn.paper_card.database;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@SuppressWarnings("unused")
public final class Database extends JavaPlugin implements DatabaseApi {

    @Override
    public void onEnable() {
        final File folder = this.getDataFolder();

        this.getLogger().info("正在测试数据库连接，如果没有任何错误信息，则数据库连接正常");

        try {
            final DatabaseConnection connection = this.connectImportant();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final DatabaseConnection connection = this.connectNormal();
            connection.close();
        } catch (Exception e) {
            this.getLogger().severe(e.toString());
            e.printStackTrace();
        }

        try {
            final DatabaseConnection connection = this.connectUnimportant();
            connection.close();
        } catch (Exception e) {
            this.getLogger().severe(e.toString());
            e.printStackTrace();
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
}
