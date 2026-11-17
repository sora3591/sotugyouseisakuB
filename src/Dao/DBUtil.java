package Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * H2 接続ユーティリティ。
 * 接続先は環境変数 CAMPUS_DB_URL / CAMPUS_DB_USER / CAMPUS_DB_PASS で上書きできます。
 * 既定はユーザーホーム直下のファイルDB（~/campustask）です。
 * ※ H2 は同じファイルDBを複数プロセスで開けないので、H2 Console と同時に使う場合は
 *    AUTO_SERVER=TRUE を付けています。
 */
public final class DBUtil {
    private static final String URL  = env("CAMPUS_DB_URL", "jdbc:h2:~/campustask;AUTO_SERVER=TRUE");
    private static final String USER = env("CAMPUS_DB_USER", "sa");
    private static final String PASS = env("CAMPUS_DB_PASS", "");

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static String env(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? fallback : v;
    }
}
