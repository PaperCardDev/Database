package cn.paper_card.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

@SuppressWarnings("unused")
public final class Database extends JavaPlugin {

    @Override
    public void onEnable() {
        final File folder = this.getDataFolder();

        this.getLogger().info("真正测试数据库连接，如果没有任何错误信息，则数据库连接正常");

        try {
            final DatabaseConnection connection = new DatabaseConnection.Important(folder);
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            this.getLogger().severe(e.toString());
            e.printStackTrace();
        }

        try {
            final DatabaseConnection connection = new DatabaseConnection.Normal(folder);
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            this.getLogger().severe(e.toString());
            e.printStackTrace();
        }

        try {
            final DatabaseConnection connection = new DatabaseConnection.Unimportant(folder);
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            this.getLogger().severe(e.toString());
            e.printStackTrace();
        }
    }
}
