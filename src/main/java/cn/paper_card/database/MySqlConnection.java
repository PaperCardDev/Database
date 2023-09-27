package cn.paper_card.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class MySqlConnection {

    private final Connection connection;

    MySqlConnection(@NotNull String address, @NotNull String userName, @NotNull String password) throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        this.connection = DriverManager.getConnection("jdbc:mysql://" + address, userName, password);
    }

    @NotNull Connection getConnection() {
        return this.connection;
    }
}
