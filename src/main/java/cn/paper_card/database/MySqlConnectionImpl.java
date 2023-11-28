package cn.paper_card.database;

import cn.paper_card.database.api.DatabaseApi;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

class MySqlConnectionImpl implements DatabaseApi.MySqlConnection {

    interface Config {
        @NotNull String getAddress();

        @NotNull String getUser();

        @NotNull String getPassword();
    }

    private Connection con = null;
    private PreparedStatement testSql = null;

    // 最大空闲时间
    private long maxIdleTime = -1;

    // 最小空闲时间
    private long minIdleTime = -1;

    private long lastUseTime = -1;

    private int conCount = 0;

    private final @NotNull Config config;

    MySqlConnectionImpl(@NotNull Config config) {
        this.config = config;
    }

    @Override
    public long getLastUseTime() {
        synchronized (this) {
            return this.lastUseTime;
        }
    }

    @Override
    public void setLastUseTime() {
        synchronized (this) {
            final long cur = System.currentTimeMillis();

            if (this.lastUseTime == -1) {
                this.lastUseTime = cur;
                return;
            }

            final long idle = cur - this.lastUseTime;

            if (this.minIdleTime == -1) {
                this.minIdleTime = idle;
            } else {
                this.minIdleTime = Math.min(this.minIdleTime, idle);
            }

            if (this.maxIdleTime == -1) {
                this.maxIdleTime = idle;
            } else {
                this.maxIdleTime = Math.max(this.maxIdleTime, idle);
            }

            this.lastUseTime = cur;
        }
    }

    @Override
    public @NotNull Connection getRawConnection() throws SQLException {
        synchronized (this) {
            if (this.con == null) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    throw new SQLException("无法加载MySQL驱动类", e);
                }

                final String address = this.config.getAddress();
                final String user = this.config.getUser();
                final String password = this.config.getPassword();

                if (address.isEmpty()) throw new SQLException("未指定MySQL数据库地址！");

                this.con = DriverManager.getConnection("jdbc:mysql://" + address,
                        user, password);

                try {
                    this.testSql = this.con.prepareStatement("SELECT 1");
                } catch (SQLException e) {
                    try {
                        this.con.close();
                        this.con = null;
                    } catch (SQLException ignored) {
                    }

                    throw e;
                }

                this.conCount += 1;
                this.setLastUseTime();

                return this.con;
            }

            // 判断是否已经挺长时间不用了
            final long cur = System.currentTimeMillis();

            // 最近使用过
            if (cur < this.lastUseTime + 10 * 1000L) {
                return this.con;
            }

            // 挺久没有使用了，测试一下连接
            try {
                this.testConnection();
                return this.con;
            } catch (SQLException ignored) {
                // 连接失效了
            }

            return this.getRawConnection();
        }
    }

    @Override
    public int getConnectCount() {
        synchronized (this) {
            return this.conCount;
        }
    }

    @Override
    public void testConnection() throws SQLException {
        synchronized (this) {
            if (this.testSql == null) throw new SQLException("未连接到数据库！");

            final ResultSet resultSet;
            try {
                resultSet = this.testSql.executeQuery();
                resultSet.close();
                this.setLastUseTime();
            } catch (SQLException e) {
                this.handleException(e);
                throw e;
            }
        }
    }

    @Override
    public void close() throws SQLException {

        synchronized (this) {
            SQLException exception = null;

            if (this.testSql != null) {
                try {
                    this.testSql.close();
                } catch (SQLException e) {
                    exception = e;
                }
                this.testSql = null;
            }

            if (this.con != null) {
                try {
                    this.con.close();
                } catch (SQLException e) {
                    exception = e;
                }
                this.con = null;
            }

            if (exception != null) throw exception;
        }
    }

    @Override
    public void handleException(@NotNull SQLException e) throws SQLException {
        this.close();
    }

    long getMaxIdleTime() {
        synchronized (this) {
            return maxIdleTime;
        }
    }

    long getMinIdleTime() {
        synchronized (this) {
            return this.minIdleTime;
        }
    }

    boolean isConnected() {
        synchronized (this) {
            return this.con != null;
        }
    }
}
