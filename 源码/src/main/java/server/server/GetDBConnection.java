package server.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 获取数据库连接的工具类.
 */
public class GetDBConnection {
    /**
     * 连接数据库的方法.
     * @param DB_name 数据库名
     * @param DB_user 账户名
     * @param DB_pwd 账户密码
     * @return 返回数据库的连接
     */
    public static Connection connectDB(String DB_name, String DB_user, String DB_pwd) {
        //加载数据库驱动程序jdbc
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException cne) {
            cne.printStackTrace();
        }
        // 连接数据库
        Connection con = null;
        try {
            String DB_url = "jdbc:mysql://localhost:3307/" + DB_name + "?useSSL=true&characterEncoding=utf-8";
            con = DriverManager.getConnection(DB_url, DB_user, DB_pwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
