package tankgame.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    private String initError = null;  // 记录初始化失败的原因

    private DatabaseManager() {
        try {
            // 先验证 MySQL 驱动是否可用
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("MySQL 驱动加载成功");
            } catch (ClassNotFoundException e) {
                initError = "MySQL 驱动未找到！请检查 pom.xml 中 mysql-connector-j 依赖是否正确，并重新 Maven 构建。\n"
                          + "具体错误: " + e.getMessage();
                System.err.println(initError);
                return;
            }

            HikariConfig config = new HikariConfig();
            // ===== MySQL 连接配置 =====
            config.setJdbcUrl("jdbc:mysql://localhost:3306/tank_game"
                    + "?useSSL=false"
                    + "&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8"
                    + "&allowPublicKeyRetrieval=true");
            config.setUsername("root");
            config.setPassword("276183495");

            // 连接池配置
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);                    // 减少最小空闲连接
            config.setConnectionTimeout(10000);           // 10秒连接超时
            config.setIdleTimeout(300000);                // 5分钟空闲超时
            config.setMaxLifetime(600000);                // 10分钟最大生命周期
            config.setAutoCommit(true);
            config.setValidationTimeout(3000);
            config.setConnectionTestQuery("SELECT 1");

            // 初始化连接池（这里会真正尝试连接）
            dataSource = new HikariDataSource(config);

            // 立即验证连接是否可用
            try (Connection testConn = dataSource.getConnection()) {
                if (testConn.isValid(3)) {
                    System.out.println("数据库连接池初始化成功，连接验证通过");
                }
            }
        } catch (Exception e) {
            initError = "数据库连接失败: " + e.getMessage();
            System.err.println(initError);
            e.printStackTrace();

            // 如果连接池已创建但连接失败，关闭它
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
            dataSource = null;
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            String msg = (initError != null)
                    ? "数据库未初始化: " + initError
                    : "数据库连接池未初始化，请检查 MySQL 是否正在运行";
            throw new SQLException(msg);
        }
        return dataSource.getConnection();
    }

    /** 检测数据库是否可用 */
    public boolean isAvailable() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    /** 获取初始化错误信息（null 表示初始化成功） */
    public String getInitError() {
        return initError;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("数据库连接池已关闭");
        }
    }
}
