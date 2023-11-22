package cn.paper_card.database;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;

public class DatabaseConnection {

    private final @NotNull Connection connection;

    DatabaseConnection(@NotNull File file) throws SQLException {

        // 数据库文件
        final File parentFile = file.getParentFile();

        if (!parentFile.isDirectory()) {
            if (!parentFile.mkdir()) {
                throw new SQLException("创建父目录[%s]失败！".formatted(parentFile.getAbsolutePath()));
            }
        }

        // 驱动类
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("无法加载SQLITE驱动类", e);
        }

        // 数据库连接
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }

    @SuppressWarnings("unused")
    public @NotNull Connection getConnection() {
        return this.connection;
    }

    public void close() throws SQLException {
        this.connection.close();
    }

    @SuppressWarnings("unused")
    public static void createTable(@NotNull Connection connection, @NotNull String sql) throws SQLException {
        final Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            try {
                statement.close();
            } catch (SQLException ignored) {
            }
            throw e;
        }
        statement.close();
    }

    @SuppressWarnings("unused")
    public static void closeAllStatements(@NotNull Class<?> klass, @NotNull Object obj) throws SQLException {
        SQLException exception = null;
        for (final Field f : klass.getDeclaredFields()) {

            final Object o;

            f.setAccessible(true);

            try {
                o = f.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (o instanceof final PreparedStatement ps) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    exception = e;
                }
            }
        }
        if (exception != null) throw exception;
    }


    static class Important extends DatabaseConnection {

        Important(@NotNull File folder) throws SQLException {
            super(new File(folder, "Important.db"));
        }
    }

    static class Normal extends DatabaseConnection {
        Normal(@NotNull File folder) throws SQLException {
            super(new File(folder, "Normal.db"));
        }
    }

    static class Unimportant extends DatabaseConnection {
        Unimportant(@NotNull File folder) throws SQLException {
            super(new File(folder, "Unimportant.db"));
        }
    }
}
