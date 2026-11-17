package Dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DB接続ユーティリティ。
 * context.xml の <Resource name="jdbc/sotugyoub" ...> で定義した DataSource（JNDI）から接続を取得します。
 * 接続先（H2 の URL・ユーザー・パスワード）は context.xml 側だけで管理します。
 */
public final class DBUtil {
    private static final String JNDI_NAME = "java:comp/env/jdbc/sotugyoub";

    private static volatile DataSource dataSource;

    private DBUtil() {}

    public static Connection getConnection() throws SQLException {
        return lookup().getConnection();
    }

    private static DataSource lookup() throws SQLException {
        DataSource ds = dataSource;
        if (ds == null) {
            try {
                ds = (DataSource) new InitialContext().lookup(JNDI_NAME);
            } catch (NamingException e) {
                throw new SQLException("DataSource が見つかりません: " + JNDI_NAME
                        + "（context.xml の Resource 名を確認してください）", e);
            }
            dataSource = ds;
        }
        return ds;
    }
}