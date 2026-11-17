package Dao;

import bean.User;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * USERS テーブルへのアクセス。
 * PASSWORD 列には PBKDF2 のハッシュ（iterations$salt$hash）を保存します。
 */
public class UserDao {

    private static final int ITERATIONS = 120_000;
    private static final int H2_UNIQUE_VIOLATION = 23505;

    /**
     * 新規登録（通知設定の初期行も同一トランザクションで作成）。
     * USER_ID または EMAIL が重複している場合は null を返します。
     */
    public User register(String userId, String name, String email, String password) {
        String insertUser = "INSERT INTO USERS (USER_ID, USER_NAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?)";
        String insertSetting = "INSERT INTO NOTIFICATION_SETTINGS (USER_ID) VALUES (?)";
        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(insertUser);
                 PreparedStatement ps2 = con.prepareStatement(insertSetting)) {
                ps.setString(1, userId);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, hash(password));
                ps.executeUpdate();
                ps2.setString(1, userId);
                ps2.executeUpdate();
                con.commit();
                return new User(userId, name, email);
            } catch (SQLException e) {
                con.rollback();
                if (e.getErrorCode() == H2_UNIQUE_VIOLATION) return null;
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("ユーザー登録に失敗しました", e);
        }
    }

    /** ログイン認証。成功なら User、失敗なら null */
    public User authenticate(String userId, String password) {
        String sql = "SELECT USER_ID, USER_NAME, EMAIL, PASSWORD FROM USERS WHERE USER_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && verify(password, rs.getString("PASSWORD"))) {
                    return new User(rs.getString("USER_ID"), rs.getString("USER_NAME"), rs.getString("EMAIL"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("認証に失敗しました", e);
        }
    }

    public User findById(String userId) {
        String sql = "SELECT USER_ID, USER_NAME, EMAIL FROM USERS WHERE USER_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? new User(rs.getString("USER_ID"), rs.getString("USER_NAME"), rs.getString("EMAIL"))
                        : null;
            }
        } catch (SQLException e) {
            throw new DaoException("ユーザー取得に失敗しました", e);
        }
    }

    /** プロフィール更新。メールが他ユーザーと重複する場合は false */
    public boolean updateProfile(String userId, String name, String email) {
        String sql = "UPDATE USERS SET USER_NAME = ?, EMAIL = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE USER_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == H2_UNIQUE_VIOLATION) return false;
            throw new DaoException("プロフィール更新に失敗しました", e);
        }
    }

    // ---- パスワードハッシュ ----

    private static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(pbkdf2(password, salt, ITERATIONS));
    }

    private static boolean verify(String password, String stored) {
        String[] p = stored.split("\\$");
        if (p.length != 3) return false;
        byte[] expected = Base64.getDecoder().decode(p[2]);
        byte[] actual = pbkdf2(password, Base64.getDecoder().decode(p[1]), Integer.parseInt(p[0]));
        return MessageDigest.isEqual(expected, actual);
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
